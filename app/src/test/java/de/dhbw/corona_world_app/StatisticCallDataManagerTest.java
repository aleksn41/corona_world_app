package de.dhbw.corona_world_app;

import androidx.core.util.Pair;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder()
public class StatisticCallDataManagerTest {
    static StatisticCallDataManager test;

    static ExecutorService executorService;
    private static final int RANDOM_ITEMS_GENERATED = 10;
    static List<StatisticCall> testItems;
    static File f;
    private static final boolean FAVOURITE = false;

    private static final int MAX_SIZE_ITEM = 700;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void init() {
        System.out.println("@BeforeClass");
        executorService = Executors.newFixedThreadPool(7);
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
        //create Random Test Items
        testItems = new LinkedList<>();
        Random r = new Random();
        for (int i = 0; i < RANDOM_ITEMS_GENERATED; i++) {
            int isoCountryItemsSize = r.nextInt(ISOCountry.values().length - 1) + 1;
            int criteriaItemsSize = r.nextInt(Criteria.values().length - 1) + 1;
            ChartType temp3 = ChartType.values()[r.nextInt(ChartType.values().length)];
            List<ISOCountry> temp1 = new LinkedList<>(Arrays.asList(ISOCountry.values()).subList(0, isoCountryItemsSize));
            List<Criteria> temp2 = new LinkedList<>(Arrays.asList(Criteria.values()).subList(0, criteriaItemsSize));
            testItems.add(new StatisticCall(temp1, temp3, temp2));
        }
        System.out.println("@BeforeClass finish");
    }

    @Test
    public void writeData() throws IOException {
        test = new StatisticCallDataManager(executorService, f, FAVOURITE);
        System.out.println("@Test1");
        //listen to liveData, and look if the change is what it should do
        final boolean[] firstTime = {true};
        Observer<List<Pair<StatisticCall, Boolean>>> temp= pairs -> {
            if(!firstTime[0]) {
                assertEquals(pairs.size(), testItems.size());
                //check that every item is equal
                //new items are inserted in front of the live data in reverse Order (first item is the newest one)
                for (int i = 0; i < testItems.size(); i++) {
                    assertEquals(testItems.get(i), pairs.get(testItems.size()-i-1).first);
                }
            }
            else firstTime[0] =false;
        };
        test.statisticCallData.observeForever(temp);

        //writing new Data
        Future<Boolean> success = test.addData(testItems);
        try {
            if (!success.get()) fail("IO Exception");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        //checking if new Data is actually in File
        if (f.length() == (MAX_SIZE_ITEM + 1) * RANDOM_ITEMS_GENERATED) {
            fail("Either writing or padding of items has failed");
        }
        test.statisticCallData.removeObserver(temp);
        System.out.println("@Test1 finish");
    }

    @Test
    public void getData() throws IOException {
        System.out.println("@Test2");
        test=new StatisticCallDataManager(executorService,f,FAVOURITE);

        final boolean[] firstTime = {true};
        Observer<List<Pair<StatisticCall, Boolean>>> temp= pairs -> {
            if(!firstTime[0]) {
                assertEquals(pairs.size(), testItems.size());
                //check that every item is equal
                //new items are inserted in front of the live data in reverse Order (first item is the newest one)
                for (int i = 0; i < testItems.size(); i++) {
                    assertEquals(testItems.get(i), pairs.get(testItems.size()-i-1).first);
                }
            }
            else firstTime[0] =false;
        };
        test.statisticCallData.observeForever(temp);


        Future<Boolean> success = test.requestMoreData();
        try {
            if (!success.get()) fail("Data is corrupt");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        //check if decoded Data is equal to Original Data
        assertEquals(testItems.size(),test.statisticCallData.getValue().size());
        test.statisticCallData.removeObserver(temp);

        System.out.println("@Test2 finish");
    }

    @Test
    public void deleteEntries() {
        System.out.println("@Test3");
        fail();
        System.out.println("@Test3 finish");
    }

    @AfterClass
    public static void deleteFile() {
        System.out.println("@AfterClass");
        //if (!f.delete()) fail("could not delete File");
        System.out.println("@AfterClass finish");
    }

}
