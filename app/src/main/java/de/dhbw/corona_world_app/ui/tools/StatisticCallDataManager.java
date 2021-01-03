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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.Enum64BitEncoder;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

//TODO change desgin to one file
public class StatisticCallDataManager {
    private static final char ITEM_SEPARATOR = ',';
    private static final char CATEGORY_SEPARATOR = '|';
    private static final int LINES_READ_PER_REQUEST = 40;
    private static final char PADDING_CHAR = '.';
    public static final String NAME_OF_HISTORY_FILE = "history.txt";
    public static final String NAME_OF_FAV_INDICES_FILE = "fav_indices.txt";
    private static final String NAME_OF_TEMP_FILE = "_temp";
    private static final int MAX_SIZE_ENTRIES = 1000;

    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallData;
    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallFavouriteData;

    //saving Session Data in order to Save in File Later
    private List<Integer> indicesToDeleteOfEntriesNotCreatedInThisSession = new ArrayList<>();
    private HashSet<Integer> indicesOfFavouriteChangedThisSession = new HashSet<>();
    private int amountOfNewItemsAddedInSession = 0;

    private List<Integer> indicesOfFavouriteEntries;
    private Enum64BitEncoder<ISOCountry> isoCountryEnum64BitEncoder;
    private Enum64BitEncoder<Criteria> criteriaEnum64BitEncoder;
    private Enum64BitEncoder<ChartType> chartTypeEnum64BitEncoder;
    private File fileWhereAllDataIsToBeSaved;
    private File fileWhereFavIndicesAreToBeSaved;
    private final ExecutorService executorService;
    private long currentPositionOnFile;
    private int currentPositionOnFavIndices;
    public boolean readAllAvailableData = false;
    public boolean readAllAvailableFavData = false;
    //technically a constant but dependent on another constant which could change, which is why its dynamically generated
    public int MAX_SIZE_ITEM;

    //TODO check if (fav) Data is corrupted
    public StatisticCallDataManager(@NonNull ExecutorService executorService, @NonNull File directoryOfSavedFile) throws IOException, DataException {
        if (!directoryOfSavedFile.isDirectory())
            throw new IllegalArgumentException("directoryOfSavedFile is not a directory");
        this.statisticCallData = new MutableLiveData<>();
        this.statisticCallData.setValue(new ArrayList<>());
        this.statisticCallFavouriteData = new MutableLiveData<>();
        this.statisticCallFavouriteData.setValue(new ArrayList<>());
        this.executorService = executorService;
        this.fileWhereAllDataIsToBeSaved = new File(directoryOfSavedFile, NAME_OF_HISTORY_FILE);
        this.fileWhereFavIndicesAreToBeSaved = new File(directoryOfSavedFile, NAME_OF_FAV_INDICES_FILE);
        init();
    }

    private void init() throws IOException, DataException {
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
        currentPositionOnFile = readAllAvailableData ? 0 : fileWhereAllDataIsToBeSaved.length() - (MAX_SIZE_ITEM + System.lineSeparator().length());
        currentPositionOnFavIndices = 0;
    }

    //TODO if user corrupts Data may cause an array out of bounds exception
    //TODO differentiate between fav and all
    //TODO if this method reads a fav entry remember it and read less if fav is requested
    //reading file in reverse

    /**
     * @return
     * @throws IOException
     * @throws DataException
     */
    public Future<Void> requestMoreHistoryData() {
        Log.v(this.getClass().getName(), "loading more Data");
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (readAllAvailableData) return null;
                List<Pair<StatisticCall, Boolean>> itemsToAddToData = new ArrayList<>(LINES_READ_PER_REQUEST);
                byte[] buffer = new byte[MAX_SIZE_ITEM + System.lineSeparator().length()];
                try (RandomAccessFile r = new RandomAccessFile(fileWhereAllDataIsToBeSaved, "r")) {
                    for (int i = 0; i < LINES_READ_PER_REQUEST; i++) {
                        if (currentPositionOnFile < 0) {
                            readAllAvailableData = true;
                            break;
                        }
                        r.seek(currentPositionOnFile);
                        int success = r.read(buffer);
                        Log.d(this.getClass().getName(), "amount of bytes read: " + success);
                        int positionOfStringWithoutPadding = getStartingPositionOfPadding(buffer, 0, MAX_SIZE_ITEM);
                        if (positionOfStringWithoutPadding == 0)
                            throw new DataException("Line in Data consists only of Padding");
                        itemsToAddToData.add(parseData(new String(buffer, 0, positionOfStringWithoutPadding, StandardCharsets.UTF_8)));
                        currentPositionOnFile -= (MAX_SIZE_ITEM + System.lineSeparator().length());
                    }
                }
                Objects.requireNonNull(statisticCallData.getValue()).addAll(itemsToAddToData);
                statisticCallData.postValue(statisticCallData.getValue());
                Log.v(this.getClass().getName(), "History Data has been successfully loaded");
                return null;
            }
        });
    }

    public Future<Void> requestMoreFavData() {
        Log.v(this.getClass().getName(), "loading more fav Data");
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (readAllAvailableFavData) return null;
                List<Pair<StatisticCall, Boolean>> itemsToAddToData = new ArrayList<>(LINES_READ_PER_REQUEST);
                byte[] buffer = new byte[MAX_SIZE_ITEM + System.lineSeparator().length()];
                try (RandomAccessFile r = new RandomAccessFile(fileWhereFavIndicesAreToBeSaved, "r")) {
                    for (int i = 0; i < LINES_READ_PER_REQUEST; i++) {
                        if (currentPositionOnFavIndices == indicesOfFavouriteEntries.size()) {
                            readAllAvailableFavData = true;
                            break;
                        }
                        r.seek(indicesOfFavouriteEntries.get(currentPositionOnFavIndices) * (MAX_SIZE_ITEM + System.lineSeparator().length()));
                        int success = r.read(buffer);
                        Log.d(this.getClass().getName(), "amount of bytes read: " + success);
                        int positionOfStringWithoutPadding = getStartingPositionOfPadding(buffer, 0, MAX_SIZE_ITEM);
                        if (positionOfStringWithoutPadding == 0)
                            throw new DataException("Line in Data consists only of Padding");
                        itemsToAddToData.add(parseData(new String(buffer, 0, positionOfStringWithoutPadding, StandardCharsets.UTF_8)));
                        currentPositionOnFavIndices += 1;
                    }
                }
                Objects.requireNonNull(statisticCallData.getValue()).addAll(itemsToAddToData);
                statisticCallData.postValue(statisticCallData.getValue());
                Log.v(this.getClass().getName(), "Fav Data has been successfully loaded");
                return null;
            }
        });
    }


    //TODO if an error occurs, undo everything
    //TODO check if space is available before and request if necessary
    //TODO if memory usage is too high remove padding (costs more time if no padding exists)
    //TODO only a maximum Number of MAX_SIZE entries
    //REQUIREMENT: first item is oldest

    /**
     * @param calls
     * @return
     * @throws IOException
     */
    public Future<Void> addData(List<StatisticCall> calls) {
        return executorService.submit(() -> {
            //mapping items and reversing List
            Objects.requireNonNull(statisticCallData.getValue()).addAll(calls.parallelStream().map(x -> new Pair<>(x, false)).collect(Collector.of(
                    ArrayDeque::new,
                    ArrayDeque::addFirst,
                    (d1, d2) -> {
                        d2.addAll(d1);
                        return d2;
                    })));
            statisticCallData.postValue(statisticCallData.getValue());
            amountOfNewItemsAddedInSession += calls.size();
            return null;
        });
    }

    //TODO recover data, if process was killed while Data has been changed (unlikely, only implemented if time is left)
    //TODO if slow maybe load lines that are already in ram instead of in file
    //TODO load in chunks if all Data cannot be loaded at once
    //TODO check if input is correct (if necessary)
    //TODO adjust current position after deleting
    //TODO what about favourites?
    //TODO are indices updated if addData is called?

    /**
     * @param indices
     * @return
     * @throws IOException
     */
    public Future<Void> deleteData(Set<Integer> indices) {
        return executorService.submit(() -> {
            statisticCallData.postValue(IntStream.range(0, Objects.requireNonNull(statisticCallData.getValue()).size()).parallel().boxed().filter(i->!indices.contains(i)).map(i -> statisticCallData.getValue().get(i)).collect(Collectors.toList()));
            //update fav indices
            indicesOfFavouriteEntries= indicesOfFavouriteEntries.parallelStream().filter(integer -> !indices.contains(integer)).collect(Collectors.toList());
            //remember items that are supposed to be deleted from storage to delete them later in saveData()
            indicesToDeleteOfEntriesNotCreatedInThisSession.addAll(indices.parallelStream().filter(i -> i >= amountOfNewItemsAddedInSession).collect(Collectors.toList()));
            return null;
        });
    }

    public Future<Void> deleteAllData() {
        return executorService.submit(() -> {
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
            return null;
        });
    }

    public void toggleFav(int index) {
        Pair<StatisticCall, Boolean> currentItem = Objects.requireNonNull(statisticCallData.getValue()).get(index);
        Objects.requireNonNull(currentItem.second);
        //only need to save this information for items not added this session
        if (index >= amountOfNewItemsAddedInSession) {
            if (indicesOfFavouriteChangedThisSession.contains(index))
                indicesOfFavouriteChangedThisSession.remove(index);
            else indicesOfFavouriteChangedThisSession.add(index);
        }
        statisticCallData.getValue().set(index, Pair.create(currentItem.first, !currentItem.second));
        statisticCallData.setValue(statisticCallData.getValue());
    }


    public Future<Void> saveAllData() {
        return executorService.submit(() -> {
            deleteMarkedIndicesNotCreatedInSession();
            saveNewSessionData();
            updateFavIndicesFile();
            resetSession();
            return null;
        });
    }

    private void deleteMarkedIndicesNotCreatedInSession() throws ExecutionException, InterruptedException, IOException {
        int linesInCurrentFile = (int) fileWhereAllDataIsToBeSaved.length() / (MAX_SIZE_ITEM + System.lineSeparator().length());
        if (indicesToDeleteOfEntriesNotCreatedInThisSession.size() == linesInCurrentFile)
            deleteAllData().get();
        File temp = new File(fileWhereAllDataIsToBeSaved + NAME_OF_TEMP_FILE);
        if (!temp.createNewFile()) {
            if (!temp.delete()) throw new IOException("could not create nor delete Temp File");
            if (!temp.createNewFile()) throw new IOException("this should not be possible");
        }
        try (RandomAccessFile reader = new RandomAccessFile(fileWhereAllDataIsToBeSaved, "r")) {
            byte[] dataOfNewFile = new byte[(MAX_SIZE_ITEM + System.lineSeparator().length()) * (linesInCurrentFile - indicesToDeleteOfEntriesNotCreatedInThisSession.size())];
            int skipped = 0;
            for (int i = 0; i < linesInCurrentFile; i++) {
                if (indicesToDeleteOfEntriesNotCreatedInThisSession.contains(linesInCurrentFile - i - 1)) {
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
            fileWhereAllDataIsToBeSaved = temp;
        }
    }

    //TODO only if not deleted
    private void updateFavouriteMarkOfEntriesNotCreatedInThisSession(byte[] newFile) {
        for (Integer integer : indicesOfFavouriteChangedThisSession) {
            int positionOfCurrentEntry = integer * (MAX_SIZE_ITEM + System.lineSeparator().length());
            int positionOfFavouriteByte = getStartingPositionOfPadding(newFile, positionOfCurrentEntry, MAX_SIZE_ITEM) - 1;
            //changes byte from '0' to '1' and '1' to '0'
            newFile[positionOfCurrentEntry + positionOfFavouriteByte] ^= 1;
        }
    }

    private void updateFavIndicesFile() throws IOException {
        //if old data needs to be changed create new File and overwrite old
        if(indicesOfFavouriteChangedThisSession.size()>0){
            //change old Data
            File temp = new File(fileWhereFavIndicesAreToBeSaved + NAME_OF_TEMP_FILE);
            if (!temp.createNewFile()) {
                if (!temp.delete()) throw new IOException("could not create nor delete Temp File");
                if (!temp.createNewFile()) throw new IOException("this should not be possible");
            }
            Files.write(temp.toPath(),listOfStringToString(indicesOfFavouriteEntries.parallelStream().map(Object::toString).collect(Collectors.toList())).getBytes(),StandardOpenOption.WRITE);
            if (!fileWhereFavIndicesAreToBeSaved.delete()) {
                temp.delete();
                throw new IOException("could not delete old File");
            }
            if (!temp.renameTo(fileWhereFavIndicesAreToBeSaved)) {
                //TODO restore File if this happens
                throw new IOException("could not rename temp File");
            }
            fileWhereFavIndicesAreToBeSaved = temp;
        }
        //else append to old file
        //TODO remove itemSeparator at end of File
        else {
            Files.write(fileWhereFavIndicesAreToBeSaved.toPath(),listOfStringToString(indicesOfFavouriteEntries.parallelStream().filter(i->i<amountOfNewItemsAddedInSession).map(Object::toString).collect(Collectors.toList())).getBytes(),StandardOpenOption.APPEND);
        }
    }

    private void saveNewSessionData() throws IOException {
        try (FileWriter writer = new FileWriter(fileWhereAllDataIsToBeSaved, true)) {
            //TODO replace with constants
            StringBuilder stringToWrite = new StringBuilder(MAX_SIZE_ITEM + System.lineSeparator().length());
            List<String> temp;
            for (int i = 0; i < amountOfNewItemsAddedInSession; i++) {

                temp = isoCountryEnum64BitEncoder.encodeListOfEnums(statisticCallData.getValue().get(i).first.getCountryList());
                stringToWrite.append(listOfStringToString(temp));
                stringToWrite.append(CATEGORY_SEPARATOR);

                temp = criteriaEnum64BitEncoder.encodeListOfEnums(statisticCallData.getValue().get(i).first.getCriteriaList());
                stringToWrite.append(listOfStringToString(temp));
                stringToWrite.append(CATEGORY_SEPARATOR);

                stringToWrite.append(chartTypeEnum64BitEncoder.encodeListOfEnums(Collections.singletonList(statisticCallData.getValue().get(i).first.getCharttype())).get(0));
                stringToWrite.append(CATEGORY_SEPARATOR);

                //new Data cannot be favourite (0=false)
                stringToWrite.append(statisticCallData.getValue().get(i).second ? '1' : '0');

                stringToWrite.append(createPaddingString(MAX_SIZE_ITEM - stringToWrite.length()));
                stringToWrite.append(System.lineSeparator());
                writer.write(stringToWrite.toString());
                stringToWrite.setLength(0);
            }
        }
    }


    private Pair<StatisticCall, Boolean> parseData(String s) throws DataException {
        String[] categories = s.split(Pattern.quote(String.valueOf(CATEGORY_SEPARATOR)));
        if (categories.length != 4) throw new DataException("Data is corrupt");
        List<ISOCountry> decodedISOCountries = isoCountryEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[0].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<Criteria> decodedCriteria = criteriaEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[1].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<ChartType> decodedChartType = chartTypeEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[2].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        if (!categories[3].equals("0") && !categories[3].equals("1"))
            throw new DataException("Data is corrupt");
        return Pair.create(new StatisticCall(decodedISOCountries, decodedChartType.get(0), decodedCriteria), categories[3].equals("1"));
    }

    //TODO check for corruption
    //TODO get in reverse Order
    //user can mark all entries as fav
    private List<Integer> getIndicesFromFavFile() throws IOException {
        //file is at most (MAX_SIZE_ENTRIES+1)*(new String(MAX_SIZE_ENTRIES).length()) big (currently 4004 bytes)
        byte[] file = Files.readAllBytes(fileWhereFavIndicesAreToBeSaved.toPath());
        List<Integer> result = new ArrayList<>(file.length / 2);
        int indexOfLastNum = 0;
        for (int i = 0; i < file.length; i++) {
            if (file[i] == ITEM_SEPARATOR) {
                result.add(parseByteArrayToInt(file, indexOfLastNum, i - indexOfLastNum));
                indexOfLastNum = i + 1;
            }
        }
        return result;
    }

    private void resetSession() {
        indicesToDeleteOfEntriesNotCreatedInThisSession.clear();
        indicesOfFavouriteChangedThisSession.clear();
        amountOfNewItemsAddedInSession = 0;
    }

    //returns the maximum size a String representing an Item can have (excludes lineSeparator)
    private int getMaxSizeForItem() {
        return (isoCountryEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * APIManager.MAX_COUNTRY_LIST_SIZE + chartTypeEnum64BitEncoder.getMaxPossibleEncodedStringSize() + (criteriaEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * Criteria.values().length + 1 + 1;
    }

    //TODO used in multiple classes maybe make static?
    private String listOfStringToString(List<String> list) {
        StringBuilder stringbuilder = new StringBuilder(list.size() * 4);
        for (int i = 0; i < list.size(); i++) {
            stringbuilder.append(list.get(i));
            stringbuilder.append(ITEM_SEPARATOR);
        }
        stringbuilder.setLength(stringbuilder.length() - 1);
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
        return 0;
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
