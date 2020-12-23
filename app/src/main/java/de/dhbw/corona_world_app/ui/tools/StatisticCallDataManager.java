package de.dhbw.corona_world_app.ui.tools;

import androidx.core.util.Pair;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final char PADDING_CHAR='.';
    //TODO this is not accurate, fix later
    private static final int MAX_SIZE_ITEM=700;

    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> statisticCallData;
    private final boolean isFavourite;
    private Enum64BitEncoder<ISOCountry> isoCountryEnum64BitEncoder;
    private Enum64BitEncoder<Criteria> criteriaEnum64BitEncoder;
    private Enum64BitEncoder<ChartType> chartTypeEnum64BitEncoder;
    private final File fileWhereDataIsToBeSaved;
    private final ExecutorService executorService;
    private long currentPositionOnFile;
    public boolean readAllAvailableData = false;

    //TODO check if Data is corrupted
    public StatisticCallDataManager(@NonNull ExecutorService executorService, @NonNull File fileWhereDataIsToBeSaved, boolean isFavourite) throws IOException {
        if (fileWhereDataIsToBeSaved.isDirectory()) throw new IllegalArgumentException("can not save data in a directory");
        this.statisticCallData = new MutableLiveData<>();
        this.statisticCallData.setValue(new ArrayList<>());
        this.isFavourite = isFavourite;
        this.executorService = executorService;
        this.fileWhereDataIsToBeSaved = fileWhereDataIsToBeSaved;
        init();
    }

    private void init() throws IOException {
        readAllAvailableData = fileWhereDataIsToBeSaved.createNewFile() || fileWhereDataIsToBeSaved.length() == 0;
        currentPositionOnFile=readAllAvailableData?0:fileWhereDataIsToBeSaved.length()-(MAX_SIZE_ITEM+1)-1;
        isoCountryEnum64BitEncoder = new Enum64BitEncoder<>(ISOCountry.class);
        criteriaEnum64BitEncoder = new Enum64BitEncoder<>(Criteria.class);
        chartTypeEnum64BitEncoder = new Enum64BitEncoder<>(ChartType.class);
    }

    //TODO optimize for case if user goes out of tabs and reloads already loaded data
    //TODO save position
    //reading file in reverse
    public Future<Boolean> requestMoreData() {
        Log.v(this.getClass().getName(),"loading more Data");
        return executorService.submit(() -> {
            if (readAllAvailableData) return true;
            List<Pair<StatisticCall, Boolean>> itemsToAddToData = new ArrayList<>(LINES_READ_PER_REQUEST);
            byte[] buffer =new byte[MAX_SIZE_ITEM];
            //TODO not reading correctly (beginning is missing)
            try (RandomAccessFile r = new RandomAccessFile(fileWhereDataIsToBeSaved,"r")) {
                r.seek(currentPositionOnFile);
                for (int i = 0; i < LINES_READ_PER_REQUEST; i++) {
                    int success=r.read(buffer);
                    Log.d(this.getClass().getName(),"amount of bytes read: "+success);
                    if(success==-1){
                        readAllAvailableData=true;
                        break;
                    }
                    r.readByte();
                    itemsToAddToData.add(Pair.create(parseData(new String(buffer, StandardCharsets.UTF_8)), isFavourite));
                    currentPositionOnFile-=(MAX_SIZE_ITEM+1);
                    r.seek(currentPositionOnFile);
                }
            } catch (IOException|DataException e) {
                Log.e(this.getClass().getName(), Objects.requireNonNull(e.getMessage()));
                return false;
            }
            Objects.requireNonNull(statisticCallData.getValue()).addAll(itemsToAddToData);
            statisticCallData.postValue(statisticCallData.getValue());
            Log.v(this.getClass().getName(),"Data has been successfully loaded");
            return true;
        });
    }

    //TODO if an error occurs, undo everything
    //TODO check if space is available before and request if necessary
    //TODO if memory usage is too high remove padding (costs more time if no padding exists)
    //TODO only a maximum Number of MAX_SIZE entries
    //REQUIREMENT: first item is oldest
    public Future<Boolean> addData(List<StatisticCall> calls) {
        return executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try(FileWriter writer = new FileWriter(fileWhereDataIsToBeSaved, true)) {
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
                        stringToWrite.append(createPaddingString(MAX_SIZE_ITEM-stringToWrite.length()));
                        stringToWrite.append(System.lineSeparator());
                        writer.write(stringToWrite.toString());
                        stringToWrite.setLength(0);
                    }
                } catch (IOException e) {
                    Log.e(this.getClass().getName(), Objects.requireNonNull(e.getMessage()));
                    return false;
                }
                //TODO reverse
                Objects.requireNonNull(statisticCallData.getValue()).addAll(calls.parallelStream().map(x->new Pair<>(x,isFavourite)).collect(Collectors.toList()));
                statisticCallData.postValue(statisticCallData.getValue());
                return true;
            }
        });
    }

    //TODO implement ALGO: read all nonDeleted Files into new temp File and rename File (delete old one) (safest method)
    public boolean deleteData(List<Integer> indices) {
        return true;
    }

    private StatisticCall parseData(String s) throws DataException {
        String[] categories = s.split(Pattern.quote(String.valueOf(CATEGORY_SEPARATOR)));
        List<ISOCountry> decodedISOCountries = isoCountryEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[0].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<Criteria> decodedCriteria = criteriaEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[1].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        List<ChartType> decodedChartType = chartTypeEnum64BitEncoder.decodeListOfEnums(Arrays.asList(categories[2].split(Pattern.quote(String.valueOf(ITEM_SEPARATOR)))));
        return new StatisticCall(decodedISOCountries, decodedChartType.get(0), decodedCriteria);
    }

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
        char[] s=new char[len];
        for (int i = 0; i < len; i++) {
            s[i]=PADDING_CHAR;
        }
        return new String(s);
    }


}
