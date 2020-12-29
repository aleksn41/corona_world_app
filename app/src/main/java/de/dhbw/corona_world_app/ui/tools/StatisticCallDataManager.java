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

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.Enum64BitEncoder;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticCallDataManager {
    private static final char ITEM_SEPARATOR = ',';
    private static final char CATEGORY_SEPARATOR = '|';
    private static final int LINES_READ_PER_REQUEST = 40;
    private static final char PADDING_CHAR = '.';
    private static final String NAME_OF_TEMP_FILE = "_temp";

    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallData;
    private final boolean isFavourite;
    private Enum64BitEncoder<ISOCountry> isoCountryEnum64BitEncoder;
    private Enum64BitEncoder<Criteria> criteriaEnum64BitEncoder;
    private Enum64BitEncoder<ChartType> chartTypeEnum64BitEncoder;
    private File fileWhereDataIsToBeSaved;
    private final ExecutorService executorService;
    private long currentPositionOnFile;
    public boolean readAllAvailableData = false;
    //technically a constant but dependent on another constant which could change, which is why its dynamically generated
    public int MAX_SIZE_ITEM;

    //TODO check if Data is corrupted
    public StatisticCallDataManager(@NonNull ExecutorService executorService, @NonNull File fileWhereDataIsToBeSaved, boolean isFavourite) throws IOException, DataException {
        if (fileWhereDataIsToBeSaved.isDirectory())
            throw new IllegalArgumentException("can not save data in a directory");
        this.statisticCallData = new MutableLiveData<>();
        this.statisticCallData.setValue(new ArrayList<>());
        this.isFavourite = isFavourite;
        this.executorService = executorService;
        this.fileWhereDataIsToBeSaved = fileWhereDataIsToBeSaved;
        init();
    }

    private void init() throws IOException, DataException {
        isoCountryEnum64BitEncoder = new Enum64BitEncoder<>(ISOCountry.class);
        criteriaEnum64BitEncoder = new Enum64BitEncoder<>(Criteria.class);
        chartTypeEnum64BitEncoder = new Enum64BitEncoder<>(ChartType.class);
        MAX_SIZE_ITEM = getMaxSizeForItem();
        readAllAvailableData = fileWhereDataIsToBeSaved.createNewFile() || fileWhereDataIsToBeSaved.length() == 0;

        if (fileWhereDataIsToBeSaved.length() % (MAX_SIZE_ITEM + System.lineSeparator().length()) != 0)
            throw new DataException("File does not have expected Format");
        currentPositionOnFile = readAllAvailableData ? 0 : fileWhereDataIsToBeSaved.length() - (MAX_SIZE_ITEM + System.lineSeparator().length());
    }

    //TODO optimize for case if user goes out of tabs and reloads already loaded data
    //TODO if padding has more Characters than the information most of the time reverse Algorithm that finds the beginning of the padding
    //TODO if user corrupts Data may cause an array out of bounds exception
    //reading file in reverse

    /**
     * @return
     * @throws IOException
     * @throws DataException
     */
    public Future<Void> requestMoreData() {
        Log.v(this.getClass().getName(), "loading more Data");
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (readAllAvailableData) return null;
                List<Pair<StatisticCall, Boolean>> itemsToAddToData = new ArrayList<>(LINES_READ_PER_REQUEST);
                byte[] buffer = new byte[MAX_SIZE_ITEM + System.lineSeparator().length()];
                try (RandomAccessFile r = new RandomAccessFile(fileWhereDataIsToBeSaved, "r")) {
                    for (int i = 0; i < LINES_READ_PER_REQUEST; i++) {
                        if (currentPositionOnFile < 0) {
                            readAllAvailableData = true;
                            break;
                        }
                        r.seek(currentPositionOnFile);
                        int success = r.read(buffer);
                        Log.d(this.getClass().getName(), "amount of bytes read: " + success);
                        int positionOfStringWithoutPadding = getStartingPositionOfPadding(buffer);
                        if (positionOfStringWithoutPadding == 0)
                            throw new DataException("Line in Data consists only of Padding");
                        itemsToAddToData.add(Pair.create(parseData(new String(buffer, 0, positionOfStringWithoutPadding, StandardCharsets.UTF_8)), isFavourite));
                        currentPositionOnFile -= (MAX_SIZE_ITEM + System.lineSeparator().length());
                    }
                }
                Objects.requireNonNull(statisticCallData.getValue()).addAll(itemsToAddToData);
                statisticCallData.postValue(statisticCallData.getValue());
                Log.v(this.getClass().getName(), "Data has been successfully loaded");
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
        return executorService.submit((Callable<Void>) () -> {
            try (FileWriter writer = new FileWriter(fileWhereDataIsToBeSaved, true)) {
                //TODO replace with constants
                StringBuilder stringToWrite = new StringBuilder(2 + (4 * 10) + (2 * 4) + 1);
                List<String> temp;
                for (int i = 0; i < calls.size(); i++) {

                    temp = isoCountryEnum64BitEncoder.encodeListOfEnums(calls.get(i).getCountryList());
                    stringToWrite.append(listOfStringToString(temp));
                    stringToWrite.append(CATEGORY_SEPARATOR);

                    temp = criteriaEnum64BitEncoder.encodeListOfEnums(calls.get(i).getCriteriaList());
                    stringToWrite.append(listOfStringToString(temp));
                    stringToWrite.append(CATEGORY_SEPARATOR);

                    stringToWrite.append(chartTypeEnum64BitEncoder.encodeListOfEnums(Collections.singletonList(calls.get(i).getCharttype())).get(0));
                    stringToWrite.append(createPaddingString(MAX_SIZE_ITEM - stringToWrite.length()));
                    stringToWrite.append(System.lineSeparator());
                    writer.write(stringToWrite.toString());
                    stringToWrite.setLength(0);
                }
            }
            //mapping items and reversing List
            Objects.requireNonNull(statisticCallData.getValue()).addAll(calls.parallelStream().map(x -> new Pair<>(x, isFavourite)).collect(Collector.of(
                    ArrayDeque::new,
                    ArrayDeque::addFirst,
                    (d1, d2) -> {
                        d2.addAll(d1);
                        return d2;
                    })));
            statisticCallData.postValue(statisticCallData.getValue());
            return null;
        });
    }

    //TODO recover data, if process was killed while Data has been changed (unlikely, only implemented if time is left)
    //TODO if slow maybe load lines that are already in ram instead of in file
    //TODO load in chunks if all Data cannot be loaded at once
    //TODO check if input is correct (if necessary)

    /**
     * @param indices
     * @return
     * @throws IOException
     */
    public Future<Void> deleteData(Set<Integer> indices) {
        return executorService.submit((Callable<Void>) () -> {
            int linesInCurrentFile = (int) fileWhereDataIsToBeSaved.length() / (MAX_SIZE_ITEM + System.lineSeparator().length());
            if (indices.size() == linesInCurrentFile) deleteAllData().get();
            File temp = new File(fileWhereDataIsToBeSaved + NAME_OF_TEMP_FILE);
            if (!temp.createNewFile()) {
                if (!temp.delete()) throw new IOException("could not create nor delete Temp File");
                if (!temp.createNewFile()) throw new IOException("this should not be possible");
            }
            try (RandomAccessFile reader = new RandomAccessFile(fileWhereDataIsToBeSaved, "r")) {
                byte[] dataOfNewFile = new byte[(MAX_SIZE_ITEM + System.lineSeparator().length()) * (linesInCurrentFile - indices.size())];
                int skipped = 0;
                for (int i = 0; i < linesInCurrentFile; i++) {
                    if (indices.contains(linesInCurrentFile - i - 1)) {
                        skipped += 1;
                    } else {
                        reader.seek((MAX_SIZE_ITEM + System.lineSeparator().length()) * (i));
                        reader.read(dataOfNewFile, (MAX_SIZE_ITEM + System.lineSeparator().length()) * (i - skipped), MAX_SIZE_ITEM + System.lineSeparator().length());
                    }
                }
                reader.close();
                Files.write(temp.toPath(), dataOfNewFile, StandardOpenOption.WRITE);
                if (!fileWhereDataIsToBeSaved.delete()) {
                    temp.delete();
                    throw new IOException("could not delete old File");
                }
                if (!temp.renameTo(fileWhereDataIsToBeSaved)) {
                    //TODO restore File if this happens
                    throw new IOException("could not rename temp File");
                }
                fileWhereDataIsToBeSaved = temp;
            }
            //remove deleted entries from MutableLiveData and update it
            statisticCallData.postValue(IntStream.range(0, Objects.requireNonNull(statisticCallData.getValue()).size()).parallel().boxed().filter(o -> !indices.contains(o)).map(i -> statisticCallData.getValue().get(i)).collect(Collectors.toList()));
            return null;
        });
    }

    public Future<Void> deleteAllData() {
        return executorService.submit(() -> {
            //delete data of File
            try (FileChannel channel = new FileOutputStream(fileWhereDataIsToBeSaved, true).getChannel()) {
                channel.truncate(0);
            }
            //delete Live Data
            statisticCallData.postValue(new ArrayList<>());
            return null;
        });
    }

    private StatisticCall parseData(String s) throws DataException {
        String[] categories = s.split(Pattern.quote(String.valueOf(CATEGORY_SEPARATOR)));
        List<ISOCountry> decodedISOCountries = isoCountryEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[0].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<Criteria> decodedCriteria = criteriaEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[1].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<ChartType> decodedChartType = chartTypeEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[2].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        return new StatisticCall(decodedISOCountries, decodedChartType.get(0), decodedCriteria);
    }

    //returns the maximum size a String representing an Item can have (excludes lineSeparator)
    private int getMaxSizeForItem() {
        return (isoCountryEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * APIManager.MAX_COUNTRY_LIST_SIZE + chartTypeEnum64BitEncoder.getMaxPossibleEncodedStringSize() + (criteriaEnum64BitEncoder.getMaxPossibleEncodedStringSize() + 1) * Criteria.values().length;
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

    private int getStartingPositionOfPadding(byte[] buffer) {
        for (int i = buffer.length - 1 - System.lineSeparator().length(); i >= 0; --i) {
            if (buffer[i] != PADDING_CHAR) return i + 1;
        }
        return 0;
    }
}
