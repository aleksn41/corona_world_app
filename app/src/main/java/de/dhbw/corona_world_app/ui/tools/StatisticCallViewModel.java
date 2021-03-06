package de.dhbw.corona_world_app.ui.tools;


import androidx.annotation.NonNull;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

/**
 * This ViewModel uses the Datamanager to save and give data to the {@link StatisticCallRecyclerViewFragment}
 *
 * @author Aleksandr Stankoski
 */
public class StatisticCallViewModel extends ViewModel {
    private StatisticCallDataManager dataManager;

    public void init(@NonNull File dataFile, @NonNull ExecutorService threadHandler) throws IOException, ExecutionException, InterruptedException, DataException {
        dataManager = new StatisticCallDataManager(threadHandler, dataFile);
        getMoreData(StatisticCallDataManager.DataType.FAVOURITE_DATA).get();
        getMoreData(StatisticCallDataManager.DataType.ALL_DATA).get();
    }

    public boolean isNotInit() {
        return dataManager == null;
    }

    public CompletableFuture<Void> getMoreData(StatisticCallDataManager.DataType dataType) {
        return dataManager.requestMoreData(dataType);
    }

    public void deleteItems(Set<Integer> indices, StatisticCallDataManager.DataType dataType) {
        dataManager.deleteData(indices, dataType);
    }

    public void deleteAllItems() throws IOException {
        dataManager.deleteAllData();
    }

    public void addData(List<StatisticCall> statisticCalls) {
        dataManager.addData(statisticCalls);
    }

    public boolean hasMoreData(StatisticCallDataManager.DataType dataType) {
        return dataManager.hasMoreData(dataType);
    }


    public void toggleFav(int position, StatisticCallDataManager.DataType dataType) {
        dataManager.toggleFav(position, dataType);
    }

    public List<Integer> getBlackListedIndices(StatisticCallDataManager.DataType dataType) {
        return dataManager.getBlackListedIndices(dataType);
    }

    public CompletableFuture<Void> saveAllData() {
        return dataManager.saveAllData();
    }

    public void observeData(LifecycleOwner owner, Observer<List<Pair<StatisticCall, Boolean>>> observer, StatisticCallDataManager.DataType dataType) {
        switch (dataType) {
            case ALL_DATA:
                dataManager.statisticCallAllData.observe(owner, observer);
                break;
            case FAVOURITE_DATA:
                dataManager.statisticCallFavouriteData.observe(owner, observer);
                break;
        }
    }

    /**
     * checks if the Data is not empty
     *
     * @param dataType the context of the Data
     * @return true if has more data, false if it does not
     */
    public boolean hasData(StatisticCallDataManager.DataType dataType) {
        return dataManager.hasData(dataType);
    }

}
