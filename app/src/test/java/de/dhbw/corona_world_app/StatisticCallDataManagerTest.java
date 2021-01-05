package de.dhbw.corona_world_app;

import androidx.core.util.Pair;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StatisticCallDataManagerTest {
    //variables needed to Test Class
    StatisticCallDataManager test;
    static ExecutorService executorService;
    static List<StatisticCall> testItems;
    File f;

    //Constants needed to test Class (test.MAX_SIZE_ITEM must be the same as in the original class, cannot be accessed as it is private)
    private static final int RANDOM_ITEMS_GENERATED = 10;
    private static final boolean FAVOURITE = false;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    //init Objects every Tests needs
    @BeforeClass
    public static void initItemsEveryTestsNeeds() {
        executorService = Executors.newFixedThreadPool(7);
        //create Random Test Items
        testItems = new LinkedList<>();
        Random r = new Random();
        for (int i = 0; i < RANDOM_ITEMS_GENERATED; i++) {
            int isoCountryItemsSize = r.nextInt(APIManager.MAX_COUNTRY_LIST_SIZE-1) + 1;
            int criteriaItemsSize = r.nextInt(Criteria.values().length - 1) + 1;
            ChartType temp3 = ChartType.values()[r.nextInt(ChartType.values().length)];
            List<ISOCountry> temp1 = new LinkedList<>(Arrays.asList(ISOCountry.values()).subList(0, isoCountryItemsSize));
            List<Criteria> temp2 = new LinkedList<>(Arrays.asList(Criteria.values()).subList(0, criteriaItemsSize));
            testItems.add(new StatisticCall(temp1, temp3, temp2));
        }
    }

    //create new DataManager and new File each Test
    @Before
    public void setupBeforeTest() throws IOException {
        //create new Temp File
        f = new File(System.getProperty("java.io.tmpdir") + "test");
        try {
            if (!f.createNewFile()) {
                if (!f.delete()) fail("cannot create or delete Temp-File");
                if (!f.createNewFile()) fail("this should not be possible");
            }
        } catch (IOException e) {
            fail("Cannot create File in Temp directory");
        }
        test=new StatisticCallDataManager(executorService, f, FAVOURITE);
    }

    @Test
    public void addData_isCorrect() throws ExecutionException, InterruptedException {
        //listen to liveData, and look if the change is what it should do
        //need first time variable as Observer is triggered when its added
        final boolean[] firstTime = {true};
        Observer<List<Pair<StatisticCall, Boolean>>> temp = pairs -> {
            if (!firstTime[0]) {
                //data in TestItems and DataManager must have same size
                assertEquals(pairs.size(), testItems.size());
                //check that every item is equal
                //new items are inserted in front of the live data in reverse Order (first item is the newest one)
                for (int i = 0; i < testItems.size(); i++) {
                    assertEquals(testItems.get(i), pairs.get(testItems.size() - i - 1).first);
                }
            } else firstTime[0] = false;
        };
        test.statisticCallData.observeForever(temp);

        //adding new Data
        test.addData(testItems).get();
        //checking if new Data is also written to File
        if (f.length() == (test.MAX_SIZE_ITEM + 1) * RANDOM_ITEMS_GENERATED) {
            fail("Either writing or padding of items has failed");
        }
        test.statisticCallData.removeObserver(temp);
    }

    @Test
    public void requestMoreData_isCorrect() throws ExecutionException, InterruptedException {
        //if there is no Data, add testItems
        if(test.statisticCallData.getValue().size()==0){
            test.addData(testItems).get();
        }
        //listen to liveData, and look if the change is what it should do
        //need first time variable as Observer is triggered when its added
        final boolean[] firstTime = {true};
        Observer<List<Pair<StatisticCall, Boolean>>> temp = pairs -> {
            if (!firstTime[0]) {
                //data in TestItems and DataManager must have same size
                assertEquals(testItems.size(),pairs.size());
                //check that every item is equal
                //new items are inserted in front of the live data in reverse Order (first item is the newest one)
                for (int i = 0; i < testItems.size(); i++) {
                    assertEquals(testItems.get(i), pairs.get(testItems.size() - i - 1).first);
                }
            } else firstTime[0] = false;
        };
        test.statisticCallData.observeForever(temp);


        test.requestMoreData().get();
        //check if decoded Data is equal to lines in Files
        assertEquals(testItems.size(), f.length()/(test.MAX_SIZE_ITEM+System.lineSeparator().length()));
        test.statisticCallData.removeObserver(temp);
    }

    @Test
    public void deleteData_isCorrect() throws ExecutionException, InterruptedException, IOException {
        //getData into DataManager
        test.requestMoreData().get();
        //if there is no Data, add testItems
        if(test.statisticCallData.getValue().size()==0){
            test.addData(testItems).get();
        }
        //save indices of items that are supposed to be deleted
        Set<Integer> indicesDeleted = new HashSet<>();
        //observe List in order to see if the deleted Items actually got deleted
        //need first time variable as Observer is triggered when its added
        boolean[] firstTime = {true};
        Observer<List<Pair<StatisticCall, Boolean>>> temp = pairs -> {
            if (!firstTime[0]) {
                //check if the new Data has correct size
                assertEquals(testItems.size()-indicesDeleted.size(),pairs.size());
                //check that every item that is not deleted is equal
                //testItems list is reverse to liveData (see addData)
                int offset = 0;
                for (int i = 0; i < testItems.size(); i++) {
                    if (indicesDeleted.contains(i)) {
                        offset += 1;
                    }
                    else assertEquals(testItems.get(testItems.size()-1-i), pairs.get( i - offset).first);
                }
            } else firstTime[0] = false;
        };
        test.statisticCallData.observeForever(temp);

        //generate items to delete
        Random r = new Random();
        int itemsToDelete = r.nextInt(testItems.size() - 1) + 1;
        for (int i = 0; i < itemsToDelete; i++) {
            int random = r.nextInt(testItems.size());
            while (indicesDeleted.contains(random)) random = r.nextInt(testItems.size());
            indicesDeleted.add(random);
        }

        //delete Data
        test.deleteData(indicesDeleted).get();
        test.statisticCallData.removeObserver(temp);

        //creating new DataManager in order to clear LiveData
        test=new StatisticCallDataManager(executorService,f,FAVOURITE);
        //read Data and see if indices have successfully been deleted
        firstTime[0]=true;
        test.statisticCallData.observeForever(temp);
        test.requestMoreData().get();
    }

    @Test
    public void deleteAllData_isCorrect() throws ExecutionException, InterruptedException {
        //getData
        test.requestMoreData().get();
        //if there is no Data, add testItems
        if(test.statisticCallData.getValue().size()==0){
            test.addData(testItems).get();
        }
        //check if all Items are deleted if List updates
        final boolean[] firstTime = {true};
        Observer<List<Pair<StatisticCall, Boolean>>> temp = pairs -> {
            if (!firstTime[0]) {
                assertEquals(0, pairs.size());
            } else firstTime[0] = false;
        };
        test.statisticCallData.observeForever(temp);

        test.deleteAllData().get();
        //check if file exists and has length 0
        assertTrue(f.isFile());
        assertEquals(0,f.length());
    }

    @After
    public void deleteFile() {
        if (!f.delete()) fail("could not delete File");
    }

}
