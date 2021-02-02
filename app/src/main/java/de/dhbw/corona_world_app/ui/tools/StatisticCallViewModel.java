package de.dhbw.corona_world_app.ui.tools;

import androidx.annotation.NonNull;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticCallViewModel extends ViewModel {
    private File dataFile;
    private StatisticCallDataManager dataManager;

    public void init(@NonNull File dataFile, @NonNull ExecutorService threadHandler) throws IOException {
        this.dataFile = dataFile;
        dataManager = new StatisticCallDataManager(threadHandler, dataFile);

    }
    public boolean isNotInit() {
        return dataManager == null;
    }

    public Future<Void> getMoreData(StatisticCallDataManager.DataType dataType) throws ExecutionException, InterruptedException {
        return dataManager.requestMoreData(dataType);
    }

    public void deleteItems(Set<Integer> indices, StatisticCallDataManager.DataType dataType){
        dataManager.deleteData(indices,dataType);
    }

    public void deleteAllItems() throws IOException {
        dataManager.deleteAllData();
    }

    public void addData(List<StatisticCall> statisticCalls) {
        dataManager.addData(statisticCalls);
    }

    public boolean hasMoreData(StatisticCallDataManager.DataType dataType){
        return dataManager.hasMoreData(dataType);
    }


    public void toggleFav(int position, StatisticCallDataManager.DataType dataType) {
        dataManager.toggleFav(position,dataType);
    }

    public List<Integer> getBlackListedIndices(StatisticCallDataManager.DataType dataType){
        return dataManager.getBlackListedIndices(dataType);
    }

    public CompletableFuture<Void> saveAllData() {
        return dataManager.saveAllData();
    }

    public void observeData(LifecycleOwner owner, Observer<List<Pair<StatisticCall, Boolean>>> observer, StatisticCallDataManager.DataType dataType) {
        switch (dataType){
            case ALL_DATA:
                dataManager.statisticCallAllData.observe(owner,observer);
                break;
            case FAVOURITE_DATA:
                dataManager.statisticCallFavouriteData.observe(owner, observer);
                break;
        }
    }

}
