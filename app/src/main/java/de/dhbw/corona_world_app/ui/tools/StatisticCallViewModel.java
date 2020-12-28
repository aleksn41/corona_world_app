package de.dhbw.corona_world_app.ui.tools;

import android.util.Log;

import androidx.core.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticCallViewModel extends ViewModel {
    private File dataFile;
    private StatisticCallDataManager dataManager;
    private ExecutorService threadHandler;
    private boolean isFavourite;


    public void init(File dataFile, ExecutorService threadHandler, boolean isFavourite) throws IOException {
        this.dataFile=dataFile;
        this.threadHandler=threadHandler;
        this.isFavourite=isFavourite;
        dataManager=new StatisticCallDataManager(threadHandler,dataFile,isFavourite);
    }

    public Future<Void> getMoreData() {
        return dataManager.requestMoreData();
    }

    public boolean isMoreDataAvailable() {
        return !dataManager.readAllAvailableData;
    }


    public Future<Void> deleteItems(Set<Integer> indices) {
        return dataManager.deleteData(indices);
    }

    public Future<Void> deleteAllItems() {
        return dataManager.deleteAllData();
    }

    public Future<Void> addData(List<StatisticCall> statisticCalls) {
        return dataManager.addData(statisticCalls);
    }

    public void toggleFavMark(int position) {
        Pair<StatisticCall, Boolean> currentItem = Objects.requireNonNull(getMutableData().getValue()).get(position);
        Objects.requireNonNull(currentItem.second);
        getMutableData().getValue().set(position, Pair.create(currentItem.first, !currentItem.second));
        getMutableData().postValue(getMutableData().getValue());
    }

    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> getMutableData() {
        return dataManager.statisticCallData;
    }

}
