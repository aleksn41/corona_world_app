package de.dhbw.corona_world_app.ui.tools;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
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
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

/**
 * This class manages the saving and reading of all Data related to Statistic requests ({@link StatisticCall}).
 * All Data is saved locally and will be written to the corresponding file if {@link StatisticCallDataManager#saveAllData()} is called.
 * @author Aleksandr Stankoski
 */
public class StatisticCallDataManager {
    private static final char ITEM_SEPARATOR = ',';
    private static final char CATEGORY_SEPARATOR = '|';
    private static final int LINES_READ_PER_REQUEST = 40;
    private static final char PADDING_CHAR = '.';
    public static final String NAME_OF_HISTORY_FILE = "history.txt";
    public static final String NAME_OF_FAV_INDICES_FILE = "fav_indices.txt";
    private static final String NAME_OF_TEMP_FILE = "_temp";
    private static final int SIZE_ITEM_SEPARATOR = 1;
    private static final int SIZE_CATEGORY_SEPARATOR = 1;
    private static final int SIZE_FAVOURITE_BIT = 1;
    private static final int MAX_SIZE_DATE_STRING = 2 + SIZE_ITEM_SEPARATOR + 2 + SIZE_ITEM_SEPARATOR + 4;
    private static final int AMOUNT_OF_CATEGORIES = 6;
    private static final int AMOUNT_OF_METHODS_ACCESSING_FILE = 5;

    //List where all Data exists (includes deleted items)
    private final List<Pair<StatisticCall, Boolean>> statisticCallData;

    //List where Data deleted entries are filtered out (all items are from statisticCallData)
    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallAllData;
    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallFavouriteData;

    //save which indices have been deleted in a sorted List (contain method is overwritten to use a binary search)
    private final List<Integer> deletedIndicesAllData = new ArrayList<Integer>() {
        @Override
        public int indexOf(@Nullable Object o) {
            int index = Collections.binarySearch(this, (Integer) o);
            return index < 0 ? -1 : index;
        }
    };
    private final List<Integer> deletedIndicesFavData = new ArrayList<Integer>() {
        @Override
        public int indexOf(@Nullable Object o) {
            int index = Collections.binarySearch(this, (Integer) o);
            return index < 0 ? -1 : index;
        }
    };
    int amountOfNewItemsAddedInSession = 0;

    private List<Integer> indicesOfFavouriteEntriesInAllData;
    private Enum64BitEncoder<ISOCountry> isoCountryEnum64BitEncoder;
    private Enum64BitEncoder<Criteria> criteriaEnum64BitEncoder;
    private Enum64BitEncoder<ChartType> chartTypeEnum64BitEncoder;
    private final File fileWhereAllDataIsToBeSaved;
    private final File fileWhereFavIndicesAreToBeSaved;
    private final ExecutorService executorService;
    private int currentPositionOnData;
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

    public StatisticCallDataManager(@NonNull ExecutorService executorService, @NonNull File directoryOfSavedFile) throws IOException, DataException {
        if (!directoryOfSavedFile.isDirectory())
            throw new IllegalArgumentException("directoryOfSavedFile is not a directory");
        this.statisticCallData = new ArrayList<>(LINES_READ_PER_REQUEST);
        this.statisticCallAllData = new MutableLiveData<>();
        this.statisticCallAllData.setValue(new ArrayList<>(LINES_READ_PER_REQUEST));
        this.statisticCallFavouriteData = new MutableLiveData<>();
        this.statisticCallFavouriteData.setValue(new ArrayList<>(LINES_READ_PER_REQUEST));
        this.executorService = executorService;
        this.fileWhereAllDataIsToBeSaved = new File(directoryOfSavedFile, NAME_OF_HISTORY_FILE);
        this.fileWhereFavIndicesAreToBeSaved = new File(directoryOfSavedFile, NAME_OF_FAV_INDICES_FILE);
        init();
    }

    private void init() throws IOException, DataException {
        Log.v(this.getClass().getName(), "initializing");
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
        indicesOfFavouriteEntriesInAllData = getIndicesFromFavFile();
        currentPositionOnData = 0;
        currentPositionOnFavIndices = 0;
        linesInCurrentFile = getLinesInFile();
        Log.v(this.getClass().getName(), "finished initializing");
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

    /**
     * Reads more data, if there exists more, into the corresponding MutableLiveData.
     * Only {@link StatisticCallDataManager#LINES_READ_PER_REQUEST} lines are read at most
     *
     * @param dataType describes the data that is supposed to be loaded
     * @return CompletableFuture<Void> that can be used to get the result in sync if async is not possible or catch Exceptions
     */
    public CompletableFuture<Void> requestMoreData(@NonNull DataType dataType) {
        Log.i(this.getClass().getName() + "|" + dataType, "loading more Data of " + dataType);
        return CompletableFuture.supplyAsync(() -> {
            try {
                readLock.lock();
                saveLock.acquireUninterruptibly();
                try {
                    switch (dataType) {
                        case ALL_DATA:
                            if (readAllAvailableData) return null;
                            int readLines = LINES_READ_PER_REQUEST;
                            //check if data is already loaded
                            if (currentPositionOnData != statisticCallData.size())
                                currentPositionOnData = statisticCallData.size();
                            //restrict to as much lines the file has
                            if (readLines + currentPositionOnData - amountOfNewItemsAddedInSession > linesInCurrentFile)
                                readLines = linesInCurrentFile - currentPositionOnData + amountOfNewItemsAddedInSession;
                            readFromFileIntoAllData(readLines);
                            currentPositionOnData += readLines;
                            if (currentPositionOnData - amountOfNewItemsAddedInSession == linesInCurrentFile)
                                readAllAvailableData = true;
                            break;
                        case FAVOURITE_DATA:
                            if (readAllAvailableFavData) return null;
                            readLines = LINES_READ_PER_REQUEST;
                            //find how many items have already been loaded
                            int indexOfLastLoadedItem = Collections.binarySearch(indicesOfFavouriteEntriesInAllData, statisticCallData.size() - 1);
                            //get upper bound index
                            indexOfLastLoadedItem = indexOfLastLoadedItem < 0 ? -indexOfLastLoadedItem - 1 : indexOfLastLoadedItem + 1;
                            if (currentPositionOnFavIndices != indexOfLastLoadedItem)
                                currentPositionOnFavIndices = indexOfLastLoadedItem;
                            if (currentPositionOnFavIndices != indicesOfFavouriteEntriesInAllData.size()) {
                                if (readLines + currentPositionOnFavIndices >= indicesOfFavouriteEntriesInAllData.size())
                                    readLines = indicesOfFavouriteEntriesInAllData.size() - currentPositionOnFavIndices;
                                //calculate how many lines of AllData need to be read
                                int linesOfAllData = indicesOfFavouriteEntriesInAllData.get(readLines + currentPositionOnFavIndices - 1) + 1 - currentPositionOnData;
                                readFromFileIntoAllData(linesOfAllData);
                                currentPositionOnFavIndices += readLines;
                                currentPositionOnData += linesOfAllData;
                                if (currentPositionOnFavIndices == indicesOfFavouriteEntriesInAllData.size())
                                    readAllAvailableFavData = true;
                                if (currentPositionOnData - amountOfNewItemsAddedInSession == linesInCurrentFile)
                                    readAllAvailableData = true;
                            } else readAllAvailableFavData = true;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + dataType);
                    }
                    Log.v(this.getClass().getName() + "|" + dataType, "successfully read more " + dataType);
                    notifyChangeInFavData(false);
                    notifyChangeInData(false);
                    return null;
                } finally {
                    saveLock.release();
                    readLock.unlock();
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    private void readFromFileIntoAllData(int readLines) throws IOException {
        List<Pair<StatisticCall, Boolean>> itemsToAddToData = new ArrayList<>(readLines);
        byte[] buffer = new byte[(MAX_SIZE_ITEM + System.lineSeparator().length()) * readLines];
        int success;
        try (RandomAccessFile r = new RandomAccessFile(fileWhereAllDataIsToBeSaved, "r")) {
            r.seek((linesInCurrentFile - (currentPositionOnData - amountOfNewItemsAddedInSession) - readLines) * (MAX_SIZE_ITEM + System.lineSeparator().length()));
            success = r.read(buffer);
            Log.d(this.getClass().getName(), "amount of bytes read: " + success);
        }
        if (success != readLines * (MAX_SIZE_ITEM + System.lineSeparator().length()))
            throw new DataException("Read Lines do not have expected Format");
        for (int i = readLines - 1; i >= 0; i--) {
            int positionOfStringWithoutPadding = getStartingPositionOfPadding(buffer, i * (MAX_SIZE_ITEM + System.lineSeparator().length()), MAX_SIZE_ITEM);
            if (positionOfStringWithoutPadding == 0)
                throw new DataException("Line in Data consists only of Padding");
            itemsToAddToData.add(parseData(new String(buffer, i * (MAX_SIZE_ITEM + System.lineSeparator().length()), positionOfStringWithoutPadding - i * (MAX_SIZE_ITEM + System.lineSeparator().length()), StandardCharsets.UTF_8)));
        }
        statisticCallData.addAll(itemsToAddToData);
    }

    /**
     * add new StatisticCalls to All Data.
     * To save the Data permanently use {@link StatisticCallDataManager#saveAllData()}
     *
     * @param calls the list of {@link StatisticCall} that should be added. The Order should be from oldest item (first item in list) to newest item (last item in list).
     */
    public void addData(List<StatisticCall> calls) {
        try {
            saveLock.acquireUninterruptibly();
            statisticCallData.addAll(0, calls.parallelStream().map(x -> new Pair<>(x, false)).collect(Collector.of(
                    ArrayDeque::new,
                    ArrayDeque::addFirst, (d1, d2) -> {
                        d2.addAll(d1);
                        return d2;
                    })));
            amountOfNewItemsAddedInSession += calls.size();
            currentPositionOnData += calls.size();
            for (int i = 0; i < indicesOfFavouriteEntriesInAllData.size(); i++) {
                indicesOfFavouriteEntriesInAllData.set(i, indicesOfFavouriteEntriesInAllData.get(i) + calls.size());
            }
            notifyChangeInData(true);
            notifyChangeInFavData(true);
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
            //mark indices as deleted
            deletedIndicesAllData.addAll(indicesOfAllDataToRemove);
            deletedIndicesFavData.addAll(favouriteIndicesToDelete);
            //sort List (lists need to be sorted)
            Collections.sort(deletedIndicesAllData);
            Collections.sort(deletedIndicesFavData);
            notifyChangeInData(true);
            notifyChangeInFavData(true);
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
            statisticCallData.clear();
            statisticCallAllData.postValue(new ArrayList<>());
            statisticCallFavouriteData.postValue(new ArrayList<>());
            indicesOfFavouriteEntriesInAllData.clear();
            readAllAvailableData = true;
            readAllAvailableFavData = true;
            currentPositionOnData = 0;
            currentPositionOnFavIndices = 0;
            resetSession();
        } finally {
            saveLock.release();
        }
    }

    public List<Integer> getBlackListedIndices(DataType dataType) {
        return dataType == DataType.ALL_DATA ? deletedIndicesAllData : deletedIndicesFavData;
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
            int indexOfAllData = dataType == DataType.FAVOURITE_DATA ? indicesOfFavouriteEntriesInAllData.get(index) : index;
            int indexOfFavData = dataType == DataType.FAVOURITE_DATA ? index : Collections.binarySearch(indicesOfFavouriteEntriesInAllData, indexOfAllData);
            //check if item is favourite
            if (statisticCallData.get(indexOfAllData).second) {
                int insertIndex = Collections.binarySearch(deletedIndicesFavData, indexOfFavData);
                insertIndex = insertIndex < 0 ? -insertIndex - 1 : insertIndex + 1;
                deletedIndicesFavData.add(insertIndex, indexOfFavData);
                Collections.sort(deletedIndicesFavData);
            } else {
                //check if this item is already in FavList
                if (indexOfFavData >= 0) {
                    deletedIndicesFavData.remove(Collections.binarySearch(deletedIndicesFavData, indexOfFavData));
                }
                //else add new item to list
                else {
                    int indexToInsert = -indexOfFavData - 1;
                    indicesOfFavouriteEntriesInAllData.add(indexToInsert, indexOfAllData);
                    //increase index by one for all indices above inserted Index
                    int indexInDeletedIndices = Collections.binarySearch(deletedIndicesFavData, indexToInsert);
                    indexInDeletedIndices = indexInDeletedIndices < 0 ? -indexInDeletedIndices - 1 : indexInDeletedIndices;
                    for (int i = indexInDeletedIndices; i < deletedIndicesFavData.size(); i++) {
                        deletedIndicesFavData.set(i, deletedIndicesFavData.get(i) + 1);
                    }
                    if (indexToInsert <= currentPositionOnFavIndices)
                        currentPositionOnFavIndices += 1;
                }
            }
            //toggle fav mark
            statisticCallData.get(indexOfAllData).setSecond(!statisticCallData.get(indexOfAllData).second);
            updateAllData(true);
            notifyChangeInFavData(true);
        } finally {
            saveLock.release();
        }
    }

    private void updateAllData(boolean inUIThread) {
        if (inUIThread) statisticCallAllData.setValue(statisticCallAllData.getValue());
        else statisticCallAllData.postValue(statisticCallAllData.getValue());
    }

    private void notifyChangeInFavData(boolean inUIThread) {
        ArrayList<Pair<StatisticCall, Boolean>> temp = new ArrayList<>(indicesOfFavouriteEntriesInAllData.size());
        for (int i = 0; i < currentPositionOnFavIndices; i++) {
            temp.add(statisticCallData.get(indicesOfFavouriteEntriesInAllData.get(i)));
        }
        if (inUIThread) statisticCallFavouriteData.setValue(temp);
        else statisticCallFavouriteData.postValue(temp);
    }

    private void notifyChangeInData(boolean inUIThread) {
        if (inUIThread) statisticCallAllData.setValue(statisticCallData);
        else statisticCallAllData.postValue(statisticCallData);
    }

    /**
     * Saves all local data in permanent Storage
     *
     * @return Future<Void> that can be used to get the result in sync if async is not possible
     */
    public CompletableFuture<Void> saveAllData() {
        //check if old request is finished
        return CompletableFuture.supplyAsync(() -> {
            //only save when no other method accesses any relevant Data
            saveLock.acquireUninterruptibly(AMOUNT_OF_METHODS_ACCESSING_FILE);
            try {
                //deleteMarkedIndicesNotCreatedInSession();
                //saveNewSessionData();
                resetSession();
                saveSessionData();
                updateFavIndicesFile();
            } catch (IOException e) {
                throw new CompletionException(e);
            } finally {
                saveLock.release(AMOUNT_OF_METHODS_ACCESSING_FILE);
            }
            return null;
        }, executorService);
    }

    private void saveSessionData() throws IOException {
        try (FileWriter writer = new FileWriter(fileWhereAllDataIsToBeSaved)) {
            StringBuilder stringToWrite = new StringBuilder(MAX_SIZE_ITEM + System.lineSeparator().length());
            List<String> temp;
            String now = new String(new char[]{ITEM_SEPARATOR, ITEM_SEPARATOR, ITEM_SEPARATOR});
            for (int i = statisticCallData.size() - 1; i >= 0; i--) {
                temp = isoCountryEnum64BitEncoder.encodeListOfEnums(statisticCallData.get(i).first.getCountryList());
                stringToWrite.append(listOfStringToString(temp));
                stringToWrite.append(CATEGORY_SEPARATOR);

                temp = criteriaEnum64BitEncoder.encodeListOfEnums(statisticCallData.get(i).first.getCriteriaList());
                stringToWrite.append(listOfStringToString(temp));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(chartTypeEnum64BitEncoder.encodeListOfEnums(Collections.singletonList(statisticCallData.get(i).first.getChartType())).get(0));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(statisticCallData.get(i).second ? '1' : '0');
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(statisticCallData.get(i).first.getStartDate() == StatisticCall.NOW ? now : statisticCallData.get(i).first.getStartDate().format(StatisticCall.DATE_FORMAT).replace('-', ITEM_SEPARATOR));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(statisticCallData.get(i).first.getEndDate() == StatisticCall.NOW ? now : statisticCallData.get(i).first.getEndDate().format(StatisticCall.DATE_FORMAT).replace('-', ITEM_SEPARATOR));

                stringToWrite.append(createPaddingString(MAX_SIZE_ITEM - stringToWrite.length()));
                stringToWrite.append(System.lineSeparator());
                writer.write(stringToWrite.toString());
                stringToWrite.setLength(0);
            }
        }
    }

    private void updateFavIndicesFile() throws IOException {
        //change old Data
        File temp = new File(fileWhereFavIndicesAreToBeSaved + NAME_OF_TEMP_FILE);
        if (!temp.createNewFile()) {
            if (!temp.delete()) throw new IOException("could not create nor delete Temp File");
            if (!temp.createNewFile()) throw new IOException("this should not be possible");
        }
        if (indicesOfFavouriteEntriesInAllData.size() != 0)
            //write list in reverse order
            Files.write(temp.toPath(), listOfStringToString(IntStream.range(0, indicesOfFavouriteEntriesInAllData.size()).map(i -> indicesOfFavouriteEntriesInAllData.size() - i - 1).mapToObj(indicesOfFavouriteEntriesInAllData::get).map(Object::toString).collect(Collectors.toList())).getBytes(), StandardOpenOption.WRITE);
        if (!fileWhereFavIndicesAreToBeSaved.delete()) {
            temp.delete();
            throw new IOException("could not delete old File");
        }
        if (!temp.renameTo(fileWhereFavIndicesAreToBeSaved)) {
            throw new IOException("could not rename temp File");
        }
    }

    public boolean hasData(DataType dataType) {
        switch (dataType) {
            case FAVOURITE_DATA:
                return indicesOfFavouriteEntriesInAllData.size() > 0;
            case ALL_DATA:
                return statisticCallData.size() - deletedIndicesAllData.size() > 0;
            default:
                throw new IllegalStateException("Unexpected dataType");
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
        LocalDate endDay = parseByteArrayToLocaleDate(categories[5].getBytes(), categories[5].length());
        try {
            StatisticCall parsedStatisticCall = new StatisticCall(decodedISOCountries, decodedChartType.get(0), decodedCriteria, startDay, endDay);
            return Pair.create(parsedStatisticCall, categories[3].equals("1"));
        } catch (IllegalArgumentException e) {
            throw new DataException("start or endDay are corrupt");
        }
    }

    private List<Integer> getIndicesFromFavFile() throws IOException {
        //file is at most (MAX_SIZE_ENTRIES+1)*(new String(MAX_SIZE_ENTRIES).length()) big (currently 4004 bytes)
        //cannot cause OutOfMemoryError
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
        if (!deletedIndicesAllData.isEmpty()) {
            //delete indices marked as deleted
            final Iterator<Pair<StatisticCall, Boolean>> each = statisticCallData.iterator();
            int currentIteratorPosition = 0;
            for (int i = 0; i < deletedIndicesAllData.size(); i++, currentIteratorPosition++) {
                while (currentIteratorPosition != deletedIndicesAllData.get(i)) {
                    currentIteratorPosition += 1;
                    each.next();
                }
                each.next();
                each.remove();
            }
            //decrease index for each deleted index
            int deleted = 0;
            for (int i = 0; i < indicesOfFavouriteEntriesInAllData.size(); i++) {
                if (deletedIndicesAllData.contains(i)) deleted += 1;
                else if (deleted > 0)
                    indicesOfFavouriteEntriesInAllData.set(i, indicesOfFavouriteEntriesInAllData.get(i) - deleted);
            }
            currentPositionOnData -= deletedIndicesAllData.size();
        }
        if (!deletedIndicesFavData.isEmpty()) {
            final Iterator<Integer> each2 = indicesOfFavouriteEntriesInAllData.iterator();
            int currentIteratorPosition = 0;
            for (int i = 0; i < deletedIndicesFavData.size(); i++, currentIteratorPosition++) {
                while (currentIteratorPosition != deletedIndicesFavData.get(i)) {
                    currentIteratorPosition += 1;
                    each2.next();
                }
                each2.next();
                each2.remove();
            }
            currentPositionOnFavIndices -= deletedIndicesFavData.size();
        }
        deletedIndicesAllData.clear();
        deletedIndicesFavData.clear();
        amountOfNewItemsAddedInSession = 0;
        linesInCurrentFile = getLinesInFile();
        notifyChangeInFavData(false);
        notifyChangeInData(false);
    }

    private int favIndexToHistoryIndex(int favIndex) {
        return indicesOfFavouriteEntriesInAllData.get(favIndex);
    }

    //returns where the index is or where it should be if it is not in the favourite list
    private int historyIndexToFavIndex(int historyIndex) {
        return Collections.binarySearch(indicesOfFavouriteEntriesInAllData, historyIndex);
    }

    //returns the maximum size a String representing an Item can have (excludes lineSeparator)
    private int getMaxSizeForItem() {
        return (isoCountryEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * APIManager.MAX_COUNTRY_LIST_SIZE + chartTypeEnum64BitEncoder.getMaxPossibleEncodedStringSize() + (criteriaEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * Criteria.values().length + SIZE_CATEGORY_SEPARATOR + SIZE_FAVOURITE_BIT + SIZE_CATEGORY_SEPARATOR + MAX_SIZE_DATE_STRING + SIZE_CATEGORY_SEPARATOR + MAX_SIZE_DATE_STRING;
    }


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
