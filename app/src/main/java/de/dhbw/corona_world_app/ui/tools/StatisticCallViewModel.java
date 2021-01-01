package de.dhbw.corona_world_app.ui.tools;

import android.util.ArraySet;

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
    //TODO what about deletions?
    private final Set<Integer> indicesOfFavouriteChanged = new HashSet<>();

    public void init(File dataFile, ExecutorService threadHandler) throws IOException {
        this.dataFile = dataFile;
        dataManager = new StatisticCallDataManager(threadHandler, dataFile);
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
        if (indicesOfFavouriteChanged.contains(position))
            indicesOfFavouriteChanged.remove(position);
        else indicesOfFavouriteChanged.add(position);
        getMutableData().getValue().set(position, Pair.create(currentItem.first, !currentItem.second));
        getMutableData().postValue(getMutableData().getValue());
    }

    public void updateFavouriteMarks(){
        try {
            dataManager.updateFavouriteMark(indicesOfFavouriteChanged).get();
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
