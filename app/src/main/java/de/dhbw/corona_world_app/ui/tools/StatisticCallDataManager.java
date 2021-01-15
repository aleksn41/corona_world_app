package de.dhbw.corona_world_app.ui.tools;

import androidx.core.util.Pair;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.Enum64BitEncoder;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

/**
 * This class manages the saving and reading of all Data related to Statistic requests ({@link StatisticCall}).
 * All Data is saved locally and will be written to the corresponding file if {@link StatisticCallDataManager#saveAllData()} is called.
 * For more information on how the data is structure see
 *
 * @author Aleksandr Stankoski
 */
//TODO if time is left, implement a backup file
public class StatisticCallDataManager {
    private static final char ITEM_SEPARATOR = ',';
    private static final char CATEGORY_SEPARATOR = '|';
    private static final int LINES_READ_PER_REQUEST = 40;
    private static final char PADDING_CHAR = '.';
    public static final String NAME_OF_HISTORY_FILE = "history.txt";
    public static final String NAME_OF_FAV_INDICES_FILE = "fav_indices.txt";
    private static final String NAME_OF_TEMP_FILE = "_temp";
    private static final int MAX_SIZE_ENTRIES = 1000;
    private static final int SIZE_ITEM_SEPARATOR = 1;
    private static final int SIZE_CATEGORY_SEPARATOR = 1;
    private static final int SIZE_FAVOURITE_BIT = 1;
    private static final int MAX_SIZE_DATE_STRING = 2 + SIZE_ITEM_SEPARATOR + 2 + SIZE_ITEM_SEPARATOR + 4;
    private static final int AMOUNT_OF_CATEGORIES = 6;
    private static final int AMOUNT_OF_METHODS_ACCESSING_FILE = 5;

    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallData;
    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallFavouriteData;

    //saving Session Data in order to save in File Later
    private final List<Integer> indicesToDeleteOfEntriesNotCreatedInThisSession = new ArrayList<>();
    private List<Integer> indicesOfFavouriteChangedNotCreatedThisSession = new ArrayList<>();
    private int amountOfNewItemsAddedInSession = 0;

    private List<Integer> indicesOfFavouriteEntries;
    private Enum64BitEncoder<ISOCountry> isoCountryEnum64BitEncoder;
    private Enum64BitEncoder<Criteria> criteriaEnum64BitEncoder;
    private Enum64BitEncoder<ChartType> chartTypeEnum64BitEncoder;
    private final File fileWhereAllDataIsToBeSaved;
    private final File fileWhereFavIndicesAreToBeSaved;
    private final ExecutorService executorService;
    private long currentPositionOnFile;
    private int currentPositionOnFavIndices;
    public boolean readAllAvailableData = false;
    public boolean readAllAvailableFavData = false;
    int linesInCurrentFile;
    //technically a constant but dependent on enum size, which could change, which is why its dynamically generated
    public int MAX_SIZE_ITEM;
    //used in order to check if any other method accesses Data before saving Data
    private Semaphore saveLock;
    //used to not read twice and overwrite old read data because of it
    private ReentrantLock readLock;

    //indicates from what context the function is called
    public enum DataType {
        FAVOURITE_DATA,
        ALL_DATA
    }

    //used to differentiate between contexts when reading files
    interface readDataInterface {

        File getFileToReadFrom();

        int getFilePosition();

        boolean atTheEndOfFile();

        void setAllDataIsRead();

        void increasePosition();

        MutableLiveData<List<Pair<StatisticCall, Boolean>>> getMutableLiveDataToAddDataTo();
    }

    readDataInterface allData = new readDataInterface() {

        @Override
        public File getFileToReadFrom() {
            return fileWhereAllDataIsToBeSaved;
        }

        @Override
        public int getFilePosition() {
            return (int) (currentPositionOnFile * (MAX_SIZE_ITEM + System.lineSeparator().length()));
        }

        @Override
        public boolean atTheEndOfFile() {
            return currentPositionOnFile < 0;
        }

        @Override
        public void setAllDataIsRead() {
            readAllAvailableData = true;
        }

        @Override
        public void increasePosition() {
            currentPositionOnFile -= 1;
        }

        @Override
        public MutableLiveData<List<Pair<StatisticCall, Boolean>>> getMutableLiveDataToAddDataTo() {
            return statisticCallData;
        }
    };

    readDataInterface favData = new readDataInterface() {

        @Override
        public File getFileToReadFrom() {
            return fileWhereAllDataIsToBeSaved;
        }

        @Override
        public int getFilePosition() {
            return indicesOfFavouriteEntries.get(currentPositionOnFavIndices) * (MAX_SIZE_ITEM + System.lineSeparator().length());
        }

        @Override
        public boolean atTheEndOfFile() {
            return currentPositionOnFavIndices == indicesOfFavouriteEntries.size();
        }

        @Override
        public void setAllDataIsRead() {
            readAllAvailableFavData = true;
        }

        @Override
        public void increasePosition() {
            currentPositionOnFavIndices += 1;
        }

        @Override
        public MutableLiveData<List<Pair<StatisticCall, Boolean>>> getMutableLiveDataToAddDataTo() {
            return statisticCallFavouriteData;
        }
    };

    //TODO check if (fav) Data is corrupted
    public StatisticCallDataManager(@NonNull ExecutorService executorService, @NonNull File directoryOfSavedFile) throws IOException, DataException {
        if (!directoryOfSavedFile.isDirectory())
            throw new IllegalArgumentException("directoryOfSavedFile is not a directory");
        this.statisticCallData = new MutableLiveData<>();
        this.statisticCallData.setValue(new ArrayList<>(LINES_READ_PER_REQUEST));
        this.statisticCallFavouriteData = new MutableLiveData<>();
        this.statisticCallFavouriteData.setValue(new ArrayList<>(LINES_READ_PER_REQUEST));
        this.executorService = executorService;
        this.fileWhereAllDataIsToBeSaved = new File(directoryOfSavedFile, NAME_OF_HISTORY_FILE);
        this.fileWhereFavIndicesAreToBeSaved = new File(directoryOfSavedFile, NAME_OF_FAV_INDICES_FILE);
        init();
    }

    private void init() throws IOException, DataException {
        readLock = new ReentrantLock(true);
        saveLock = new Semaphore(AMOUNT_OF_METHODS_ACCESSING_FILE);
        isoCountryEnum64BitEncoder = new Enum64BitEncoder<>(ISOCountry.class);
        criteriaEnum64BitEncoder = new Enum64BitEncoder<>(Criteria.class);
        chartTypeEnum64BitEncoder = new Enum64BitEncoder<>(ChartType.class);
        MAX_SIZE_ITEM = getMaxSizeForItem();
        readAllAvailableData = fileWhereAllDataIsToBeSaved.createNewFile() || fileWhereAllDataIsToBeSaved.length() == 0;
        readAllAvailableFavData = fileWhereFavIndicesAreToBeSaved.createNewFile() || fileWhereFavIndicesAreToBeSaved.length() == 0;
        if (fileWhereAllDataIsToBeSaved.length() % (MAX_SIZE_ITEM + System.lineSeparator().length()) != 0)
            throw new DataException("File does not have expected Format");
        //TODO check if fav File has expected Format
        indicesOfFavouriteEntries = getIndicesFromFavFile();
        currentPositionOnFile = readAllAvailableData ? 0 : fileWhereAllDataIsToBeSaved.length() / (MAX_SIZE_ITEM + System.lineSeparator().length()) - 1;
        currentPositionOnFavIndices = 0;
        linesInCurrentFile = getLinesInFile();
    }

    public boolean hasMoreData(@NonNull DataType dataType) {
        switch (dataType) {
            case ALL_DATA:
                return !readAllAvailableData;
            case FAVOURITE_DATA:
                return !readAllAvailableFavData;
            default:
                throw new IllegalStateException("Unexpected value: " + dataType);
        }
    }

    //TODO if user corrupts Data may cause an array out of bounds exception

    /**
     * Reads more data, if there exists more, into the corresponding MutableLiveData.
     * Only {@link StatisticCallDataManager#LINES_READ_PER_REQUEST} lines are read at most
     *
     * @param dataType describes the data that is supposed to be loaded
     * @return Future<Void> that can be used to get the result in sync if async is not possible
     */
    public Future<Void> requestMoreData(@NonNull DataType dataType) throws ExecutionException, InterruptedException {
        Log.i(this.getClass().getName() + "|" + dataType, "loading more Data of " + dataType);
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                readLock.lock();
                saveLock.acquire();
                try {
                    readDataInterface request;
                    switch (dataType) {
                        case ALL_DATA:
                            request = allData;
                            break;
                        case FAVOURITE_DATA:
                            request = favData;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + dataType);
                    }
                    if (!hasMoreData(dataType)) {
                        Log.v(this.getClass().getName() + "|" + dataType, dataType + " Data has been successfully loaded");
                        return null;
                    }
                    List<Pair<StatisticCall, Boolean>> itemsToAddToData = new ArrayList<>(LINES_READ_PER_REQUEST);
                    byte[] buffer = new byte[MAX_SIZE_ITEM + System.lineSeparator().length()];
                    try (RandomAccessFile r = new RandomAccessFile(request.getFileToReadFrom(), "r")) {
                        for (int i = 0; i < LINES_READ_PER_REQUEST; i++) {
                            if (request.atTheEndOfFile()) {
                                request.setAllDataIsRead();
                                break;
                            }
                            r.seek(request.getFilePosition());
                            int success = r.read(buffer);
                            Log.d(this.getClass().getName() + "|" + dataType, "amount of bytes read: " + success);
                            int positionOfStringWithoutPadding = getStartingPositionOfPadding(buffer, 0, MAX_SIZE_ITEM);
                            if (positionOfStringWithoutPadding == 0)
                                throw new DataException("Line in Data consists only of Padding");
                            itemsToAddToData.add(parseData(new String(buffer, 0, positionOfStringWithoutPadding, StandardCharsets.UTF_8)));
                            request.increasePosition();
                        }
                    }
                    Objects.requireNonNull(request.getMutableLiveDataToAddDataTo().getValue()).addAll(itemsToAddToData);
                    request.getMutableLiveDataToAddDataTo().postValue(request.getMutableLiveDataToAddDataTo().getValue());
                    Log.i(this.getClass().getName() + "|" + dataType, dataType + " Data has been successfully loaded");
                    return null;
                } finally {
                    saveLock.release();
                    readLock.unlock();
                }
            }
        });
    }

    //TODO if an error occurs, undo everything
    //TODO check if space is available before and request if necessary
    //TODO only a maximum Number of MAX_SIZE entries (optional)

    /**
     * add new StatisticCalls to All Data.
     * To save the Data permanently use {@link StatisticCallDataManager#saveAllData()}
     *
     * @param calls the list of {@link StatisticCall} that should be added. The Order should be from oldest item (first item in list) to newest item (last item in list).
     */
    public void addData(List<StatisticCall> calls) {
        try {
            saveLock.acquireUninterruptibly();
            Objects.requireNonNull(statisticCallData.getValue()).addAll(0, calls.parallelStream().map(x -> new Pair<>(x, false)).collect(Collector.of(
                    ArrayDeque::new,
                    ArrayDeque::addFirst,
                    (d1, d2) -> {
                        d2.addAll(d1);
                        return d2;
                    })));
            statisticCallData.postValue(statisticCallData.getValue());
            amountOfNewItemsAddedInSession += calls.size();
        } finally {
            saveLock.release();
        }
    }

    /**
     * deletes the entries of the given indices depended on the context of the given indices
     * To save the Data permanently use {@link StatisticCallDataManager#saveAllData()}
     *
     * @param indices  describes the entries of the MutableLiveData that are supposed to be deleted
     * @param dataType describes if the indices come from ALL_DATA or FAVOURITE_DATA
     */
    public void deleteData(Set<Integer> indices, DataType dataType) {
        try {
            saveLock.acquireUninterruptibly();
            Set<Integer> favouriteIndicesToDelete;
            Set<Integer> indicesOfAllDataToRemove;
            switch (dataType) {
                case ALL_DATA:
                    favouriteIndicesToDelete = indices.parallelStream().map(this::historyIndexToFavIndex).filter(i -> i >= 0).collect(Collectors.toSet());
                    indicesOfAllDataToRemove = indices;
                    break;
                case FAVOURITE_DATA:
                    favouriteIndicesToDelete = indices;
                    indicesOfAllDataToRemove = indices.parallelStream().map(this::favIndexToHistoryIndex).collect(Collectors.toSet());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + dataType);
            }
            //delete data from All Data
            statisticCallData.postValue(IntStream.range(0, Objects.requireNonNull(statisticCallData.getValue()).size()).parallel().boxed().filter(i -> !indicesOfAllDataToRemove.contains(i)).map(i -> statisticCallData.getValue().get(i)).collect(Collectors.toList()));
            //delete fav entries
            statisticCallFavouriteData.postValue(IntStream.range(0, statisticCallFavouriteData.getValue().size()).parallel().boxed().filter(i -> !favouriteIndicesToDelete.contains(i)).map(i -> statisticCallFavouriteData.getValue().get(i)).collect(Collectors.toList()));
            //update fav indices
            int deleted = favouriteIndicesToDelete.size();
            ArrayList<Integer> newList = new ArrayList<>(indicesOfFavouriteEntries.size());
            for (int i = 0; i < indicesOfFavouriteEntries.size(); i++) {
                if (favouriteIndicesToDelete.contains(i)) {
                    deleted -= 1;
                } else newList.add(indicesOfFavouriteEntries.get(i) - deleted);
            }
            indicesOfFavouriteEntries = newList;
            //remember items that are supposed to be deleted from storage to delete them later in saveData()
            indicesToDeleteOfEntriesNotCreatedInThisSession.addAll(indicesOfAllDataToRemove.parallelStream().filter(i -> i >= amountOfNewItemsAddedInSession).collect(Collectors.toList()));
            //update count of newly created Items
            long amountOfNewItemsDeleted = indicesOfAllDataToRemove.parallelStream().filter(i -> i < amountOfNewItemsAddedInSession).count();
            amountOfNewItemsAddedInSession -= amountOfNewItemsDeleted;
            deleted = 0;
            ArrayList<Integer> newList2 = new ArrayList<>(indicesOfFavouriteChangedNotCreatedThisSession.size() - (indicesOfAllDataToRemove.size() - (int) amountOfNewItemsDeleted));
            for (int i = 0; i < indicesOfFavouriteChangedNotCreatedThisSession.size(); i++) {
                if (indicesOfAllDataToRemove.contains(indicesOfFavouriteChangedNotCreatedThisSession.get(i))) {
                    deleted += 1;
                } else
                    newList2.add(indicesOfFavouriteChangedNotCreatedThisSession.get(i) - deleted);
            }
            indicesOfFavouriteChangedNotCreatedThisSession = newList2;
        } finally {
            saveLock.release();
        }
    }

    /**
     * deletes all Data locally and in the File (saving is not needed)
     */
    public void deleteAllData() throws IOException {
        try {
            saveLock.acquireUninterruptibly();
            //delete history data
            try (FileChannel channel = new FileOutputStream(fileWhereAllDataIsToBeSaved, true).getChannel()) {
                channel.truncate(0);
            }
            //delete Fav Data
            try (FileChannel channel = new FileOutputStream(fileWhereFavIndicesAreToBeSaved, true).getChannel()) {
                channel.truncate(0);
            }
            //delete Live Data
            statisticCallData.postValue(new ArrayList<>());
            statisticCallFavouriteData.postValue(new ArrayList<>());
            indicesOfFavouriteEntries.clear();
            readAllAvailableData = true;
            readAllAvailableFavData = true;
            currentPositionOnFile = 0;
            currentPositionOnFavIndices = 0;
            resetSession();
        } finally {
            saveLock.release();
        }
    }

    /**
     * toggle the favourite mark of an item
     *
     * @param index    the index of the item in the MutableLiveData
     * @param dataType the context of this item
     */
    public void toggleFav(int index, @NonNull DataType dataType) {
        try {
            saveLock.acquireUninterruptibly();
            int indexOfAllData;
            //positive if index exists in fav negative if it does not exist
            //if it is negative, it indicates the position where the item is to be inserted
            int potentialIndexOfFavData;
            boolean itemAlreadyLoadedInAllData;
            boolean itemAlreadyLoadedInFavData;
            int lineInFile;

            switch (dataType) {
                case ALL_DATA:
                    indexOfAllData = index;
                    lineInFile = linesInCurrentFile + amountOfNewItemsAddedInSession - index - 1;
                    potentialIndexOfFavData = Collections.binarySearch(indicesOfFavouriteEntries, lineInFile, Collections.reverseOrder());
                    itemAlreadyLoadedInAllData = true;
                    itemAlreadyLoadedInFavData = potentialIndexOfFavData >= 0 && potentialIndexOfFavData < currentPositionOnFavIndices;
                    break;
                case FAVOURITE_DATA:
                    lineInFile = indicesOfFavouriteEntries.get(index);
                    indexOfAllData = linesInCurrentFile + amountOfNewItemsAddedInSession - lineInFile - 1;
                    potentialIndexOfFavData = index;
                    itemAlreadyLoadedInAllData = indexOfAllData < statisticCallData.getValue().size();
                    itemAlreadyLoadedInFavData = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + dataType);
            }

            //only need to save this information for items not added this session
            if (indexOfAllData >= amountOfNewItemsAddedInSession) {
                //if index<0 the item does not exist in List
                int indexInFavouriteChanged = Collections.binarySearch(indicesOfFavouriteChangedNotCreatedThisSession, indexOfAllData);
                if (indexInFavouriteChanged >= 0) {
                    indicesOfFavouriteChangedNotCreatedThisSession.remove(indexInFavouriteChanged);
                } else
                    indicesOfFavouriteChangedNotCreatedThisSession.add(-indexInFavouriteChanged - 1, indexOfAllData);
            }
            //toggle favourite Flag
            if (itemAlreadyLoadedInAllData) {
                Pair<StatisticCall, Boolean> currentItem = Objects.requireNonNull(statisticCallData.getValue()).get(indexOfAllData);
                currentItem = Pair.create(currentItem.first, !currentItem.second);
                statisticCallData.getValue().set(index, currentItem);
                statisticCallData.setValue(statisticCallData.getValue());
            }
            //item has been switched off of Favourite
            if (potentialIndexOfFavData >= 0) {
                //check if item has already been loaded into favList
                if (itemAlreadyLoadedInFavData) {
                    statisticCallFavouriteData.getValue().remove(potentialIndexOfFavData);
                    statisticCallFavouriteData.setValue(statisticCallFavouriteData.getValue());
                }
                currentPositionOnFavIndices -= potentialIndexOfFavData < currentPositionOnFavIndices ? 1 : 0;
                indicesOfFavouriteEntries.remove(potentialIndexOfFavData);
            }
            //item has been switched to Favourite
            else {
                //negate number to get insertion point (read function description of binarySearch)
                potentialIndexOfFavData = -potentialIndexOfFavData - 1;
                indicesOfFavouriteEntries.add(potentialIndexOfFavData, lineInFile);
                //if this entry is in between the already loaded data, insert it into list
                if (potentialIndexOfFavData <= currentPositionOnFavIndices) {
                    statisticCallFavouriteData.getValue().add(potentialIndexOfFavData, statisticCallData.getValue().get(indexOfAllData));
                    currentPositionOnFavIndices += 1;
                }
            }
        } finally {
            saveLock.release();
        }
    }

    /**
     * Saves all local data in permanent Storage
     *
     * @return Future<Void> that can be used to get the result in sync if async is not possible
     */
    //TODO recover data, if process was killed while Data has been changed (unlikely, only implemented if time is left)
    //TODO load in chunks if all Data cannot be loaded at once
    //TODO check if input is correct (if necessary)
    //TODO adjust current position after deleting
    //TODO check if new Data has been set before saving
    public CompletableFuture<Void> saveAllData() {
        //check if old request is finished
        return CompletableFuture.supplyAsync(() -> {
            //only save when no other method accesses any relevant Data
            saveLock.acquireUninterruptibly(AMOUNT_OF_METHODS_ACCESSING_FILE);
            try {
                deleteMarkedIndicesNotCreatedInSession();
                saveNewSessionData();
                updateFavIndicesFile();
                resetSession();
            } catch (IOException e) {
                throw new CompletionException(e);
            } finally {
                saveLock.release(AMOUNT_OF_METHODS_ACCESSING_FILE);
            }
            return null;
        }, executorService);
    }

    private void deleteMarkedIndicesNotCreatedInSession() throws IOException {

        /*if (indicesToDeleteOfEntriesNotCreatedInThisSession.size() == linesInCurrentFile){
            deleteAllData().get();
            return;
        }*/
        File temp = new File(fileWhereAllDataIsToBeSaved + NAME_OF_TEMP_FILE);
        if (!temp.createNewFile()) {
            if (!temp.delete()) throw new IOException("could not create nor delete Temp File");
            if (!temp.createNewFile()) throw new IOException("this should not be possible");
        }
        try (RandomAccessFile reader = new RandomAccessFile(fileWhereAllDataIsToBeSaved, "r")) {
            byte[] dataOfNewFile = new byte[(MAX_SIZE_ITEM + System.lineSeparator().length()) * (linesInCurrentFile - indicesToDeleteOfEntriesNotCreatedInThisSession.size())];
            int skipped = 0;
            for (int i = 0; i < linesInCurrentFile; i++) {
                if (indicesToDeleteOfEntriesNotCreatedInThisSession.contains(linesInCurrentFile + amountOfNewItemsAddedInSession - i - 1)) {
                    skipped += 1;
                } else {
                    reader.seek((MAX_SIZE_ITEM + System.lineSeparator().length()) * i);
                    reader.read(dataOfNewFile, (MAX_SIZE_ITEM + System.lineSeparator().length()) * (i - skipped), MAX_SIZE_ITEM + System.lineSeparator().length());
                }
            }
            reader.close();
            updateFavouriteMarkOfEntriesNotCreatedInThisSession(dataOfNewFile);
            Files.write(temp.toPath(), dataOfNewFile, StandardOpenOption.WRITE);
            if (!fileWhereAllDataIsToBeSaved.delete()) {
                temp.delete();
                throw new IOException("could not delete old File");
            }
            if (!temp.renameTo(fileWhereAllDataIsToBeSaved)) {
                //TODO restore File if this happens
                throw new IOException("could not rename temp File");
            }
            //fileWhereAllDataIsToBeSaved = temp;
        }
    }

    private void updateFavouriteMarkOfEntriesNotCreatedInThisSession(byte[] newFile) {
        for (Integer integer : indicesOfFavouriteChangedNotCreatedThisSession) {
            int positionOfFavouriteByte = (linesInCurrentFile - integer - 1) * (MAX_SIZE_ITEM + System.lineSeparator().length());
            int currentCategory = 0;
            int categoryOfFavByte = 3;
            while ((newFile[positionOfFavouriteByte] != '0' && newFile[positionOfFavouriteByte] != '1') || categoryOfFavByte != currentCategory) {
                if (newFile[positionOfFavouriteByte] == CATEGORY_SEPARATOR) currentCategory += 1;
                positionOfFavouriteByte += 1;
                if (currentCategory > categoryOfFavByte)
                    throw new RuntimeException("Data in new File has become corrupt");
            }
            //changes byte from '0' to '1' and '1' to '0'
            newFile[positionOfFavouriteByte] ^= 1;
        }
    }

    //TODO no need to remove old file if only new data is added
    private void updateFavIndicesFile() throws IOException {
        //if old data needs to be changed create new File and overwrite old
        //TODO implement method to determine if only append is needed to save new data
        if (true) {
            //change old Data
            File temp = new File(fileWhereFavIndicesAreToBeSaved + NAME_OF_TEMP_FILE);
            if (!temp.createNewFile()) {
                if (!temp.delete()) throw new IOException("could not create nor delete Temp File");
                if (!temp.createNewFile()) throw new IOException("this should not be possible");
            }
            if (indicesOfFavouriteEntries.size() != 0)
                //write list in reverse order
                Files.write(temp.toPath(), listOfStringToString(IntStream.range(0, indicesOfFavouriteEntries.size()).map(i -> indicesOfFavouriteEntries.size() - i - 1).mapToObj(indicesOfFavouriteEntries::get).map(Object::toString).collect(Collectors.toList())).getBytes(), StandardOpenOption.WRITE);
            if (!fileWhereFavIndicesAreToBeSaved.delete()) {
                temp.delete();
                throw new IOException("could not delete old File");
            }
            if (!temp.renameTo(fileWhereFavIndicesAreToBeSaved)) {
                //TODO restore File if this happens
                throw new IOException("could not rename temp File");
            }
            //fileWhereFavIndicesAreToBeSaved = temp;
        }
        //else append to old file
        //TODO remove itemSeparator at end of File
        /*else {
            Files.write(fileWhereFavIndicesAreToBeSaved.toPath(),listOfStringToString(indicesOfFavouriteEntries.parallelStream().filter(i->i<amountOfNewItemsAddedInSession).map(Object::toString).collect(Collectors.toList())).getBytes(),StandardOpenOption.APPEND);
        }*/
    }

    private void saveNewSessionData() throws IOException {
        try (FileWriter writer = new FileWriter(fileWhereAllDataIsToBeSaved, true)) {
            StringBuilder stringToWrite = new StringBuilder(MAX_SIZE_ITEM + System.lineSeparator().length());
            List<String> temp;
            for (int i = amountOfNewItemsAddedInSession - 1; i >= 0; i--) {

                temp = isoCountryEnum64BitEncoder.encodeListOfEnums(statisticCallData.getValue().get(i).first.getCountryList());
                stringToWrite.append(listOfStringToString(temp));
                stringToWrite.append(CATEGORY_SEPARATOR);

                temp = criteriaEnum64BitEncoder.encodeListOfEnums(statisticCallData.getValue().get(i).first.getCriteriaList());
                stringToWrite.append(listOfStringToString(temp));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(chartTypeEnum64BitEncoder.encodeListOfEnums(Collections.singletonList(statisticCallData.getValue().get(i).first.getChartType())).get(0));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(statisticCallData.getValue().get(i).second ? '1' : '0');
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(statisticCallData.getValue().get(i).first.getStartDate().format(StatisticCall.DATE_FORMAT).replace('-', ITEM_SEPARATOR));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(statisticCallData.getValue().get(i).first.getEndDate() == null ? new String(new char[]{ITEM_SEPARATOR, ITEM_SEPARATOR, ITEM_SEPARATOR}) : statisticCallData.getValue().get(i).first.getEndDate().format(StatisticCall.DATE_FORMAT).replace('-', ITEM_SEPARATOR));

                stringToWrite.append(createPaddingString(MAX_SIZE_ITEM - stringToWrite.length()));
                stringToWrite.append(System.lineSeparator());
                writer.write(stringToWrite.toString());
                stringToWrite.setLength(0);
            }
        }
    }


    private Pair<StatisticCall, Boolean> parseData(@NonNull String s) throws DataException {
        String[] categories = s.split(Pattern.quote(String.valueOf(CATEGORY_SEPARATOR)));
        if (categories.length != AMOUNT_OF_CATEGORIES) throw new DataException("Data is corrupt");
        List<ISOCountry> decodedISOCountries = isoCountryEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[0].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<Criteria> decodedCriteria = criteriaEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[1].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<ChartType> decodedChartType = chartTypeEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[2].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        if (!categories[3].equals("0") && !categories[3].equals("1"))
            throw new DataException("Data is corrupt");
        LocalDate startDay = parseByteArrayToLocaleDate(categories[4].getBytes(), categories[4].length());
        if (startDay == null) throw new DataException("start Date cannot be null");
        LocalDate endDay = parseByteArrayToLocaleDate(categories[5].getBytes(), categories[5].length());
        try {
            StatisticCall parsedStatisticCall = new StatisticCall(decodedISOCountries, decodedChartType.get(0), decodedCriteria, startDay, endDay);
            return Pair.create(parsedStatisticCall, categories[3].equals("1"));
        } catch (IllegalArgumentException e) {
            throw new DataException("start or endDay are corrupt");
        }
    }

    //TODO check for corruption
    //TODO if MAX_SIZE_ENTRIES is not implemented, change this
    private List<Integer> getIndicesFromFavFile() throws IOException {
        //file is at most (MAX_SIZE_ENTRIES+1)*(new String(MAX_SIZE_ENTRIES).length()) big (currently 4004 bytes)
        byte[] file = Files.readAllBytes(fileWhereFavIndicesAreToBeSaved.toPath());
        List<Integer> result = new ArrayList<>(file.length / 2);
        int indexOfLastSeparator = file.length;
        for (int i = file.length - 1; i >= 0; i--) {
            if (file[i] == ITEM_SEPARATOR) {
                result.add(parseByteArrayToInt(file, i + 1, indexOfLastSeparator - (i + 1)));
                indexOfLastSeparator = i;
            }
        }
        if (file.length != 0)
            result.add(parseByteArrayToInt(file, 0, indexOfLastSeparator));
        return result;
    }

    private int getLinesInFile() {
        return (int) fileWhereAllDataIsToBeSaved.length() / (MAX_SIZE_ITEM + System.lineSeparator().length());
    }

    private void resetSession() {
        indicesToDeleteOfEntriesNotCreatedInThisSession.clear();
        indicesOfFavouriteChangedNotCreatedThisSession.clear();
        amountOfNewItemsAddedInSession = 0;
        linesInCurrentFile = getLinesInFile();
    }

    private int favIndexToHistoryIndex(int favIndex) {
        return linesInCurrentFile + amountOfNewItemsAddedInSession - indicesOfFavouriteEntries.get(favIndex) - 1;
    }

    //returns where the index is or where it should be if it is not in the favourite list
    private int historyIndexToFavIndex(int historyIndex) {
        return Collections.binarySearch(indicesOfFavouriteEntries, linesInCurrentFile + amountOfNewItemsAddedInSession - historyIndex - 1, Collections.reverseOrder());
    }

    //returns the maximum size a String representing an Item can have (excludes lineSeparator)
    private int getMaxSizeForItem() {
        return (isoCountryEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * APIManager.MAX_COUNTRY_LIST_SIZE + chartTypeEnum64BitEncoder.getMaxPossibleEncodedStringSize() + (criteriaEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * Criteria.values().length + SIZE_CATEGORY_SEPARATOR + SIZE_FAVOURITE_BIT + SIZE_CATEGORY_SEPARATOR + MAX_SIZE_DATE_STRING + SIZE_CATEGORY_SEPARATOR + MAX_SIZE_DATE_STRING;
    }

    //TODO used in multiple classes maybe make static?
    private String listOfStringToString(@NonNull List<String> list) {
        StringBuilder stringbuilder = new StringBuilder(list.size() * 4);
        for (int i = 0; i < list.size(); i++) {
            stringbuilder.append(list.get(i));
            stringbuilder.append(ITEM_SEPARATOR);
        }
        if (list.size() != 0) stringbuilder.setLength(stringbuilder.length() - 1);
        return stringbuilder.toString();
    }

    private String createPaddingString(int len) {
        char[] s = new char[len];
        for (int i = 0; i < len; i++) {
            s[i] = PADDING_CHAR;
        }
        return new String(s);
    }

    private int getStartingPositionOfPadding(byte[] buffer, int begin, int length) {
        for (int i = begin + length - 1; i >= begin; --i) {
            if (buffer[i] != PADDING_CHAR) return i + 1;
        }
        return begin;
    }

    private LocalDate parseByteArrayToLocaleDate(byte[] arr, int length) {
        if (length == 3 && arr[0] == ITEM_SEPARATOR && arr[1] == ITEM_SEPARATOR && arr[2] == ITEM_SEPARATOR) {
            return null;
        }
        int day;
        int month;
        int year;
        int[] positionsOfItemSeparators = new int[2];
        int currentIndexOnPositionsOfItemSeparators = 0;
        for (int i = 0; i < length; i++) {
            if (arr[i] == ITEM_SEPARATOR) {
                if (currentIndexOnPositionsOfItemSeparators == 2) {
                    throw new DataException("could not read LocaleDate");
                }
                positionsOfItemSeparators[currentIndexOnPositionsOfItemSeparators++] = i;
            }
        }
        if (currentIndexOnPositionsOfItemSeparators != 2)
            throw new DataException("could not read LocaleDate");
        day = parseByteArrayToInt(arr, 0, positionsOfItemSeparators[0]);
        month = parseByteArrayToInt(arr, positionsOfItemSeparators[0] + 1, positionsOfItemSeparators[1] - positionsOfItemSeparators[0] - 1);
        year = parseByteArrayToInt(arr, positionsOfItemSeparators[1] + 1, length - positionsOfItemSeparators[1] - 1);
        return LocalDate.of(year, month, day);
    }

    //expects an byte array of chars '0'-'9'
    private int parseByteArrayToInt(byte[] arr, int begin, int length) {
        if (length < 1 || length > 4)
            throw new IllegalArgumentException("length can only be between 1 and 4 inclusive");
        int result = 0;
        for (int i = 0; i < length; i++) {
            result *= 10;
            result += arr[begin + i] - '0';
        }
        return result;
    }
}
