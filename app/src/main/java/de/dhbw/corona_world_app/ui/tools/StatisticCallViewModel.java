package de.dhbw.corona_world_app.ui.tools;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticCallViewModel extends ViewModel {
    private File dataFile;
    private StatisticCallDataManager dataManager;

    public void init(@NonNull File dataFile,@NonNull ExecutorService threadHandler) throws IOException {
        this.dataFile = dataFile;
        dataManager = new StatisticCallDataManager(threadHandler, dataFile);
    }

    public boolean isInit(){
        return dataManager!=null;
    }

    public Future<Void> getMoreData() {
        return dataManager.requestMoreHistoryData();
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
        dataManager.toggleFav(position);
    }

    public void updateFavouriteMarks(){
        try {
            dataManager.updateFavouriteMark().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<List<Pair<StatisticCall, Boolean>>> getMutableData() {
        return dataManager.statisticCallData;
    }

}
