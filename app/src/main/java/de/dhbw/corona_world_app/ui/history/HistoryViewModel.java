package de.dhbw.corona_world_app.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

public class HistoryViewModel extends ViewModel {

    //TODO: change to Statistic-Call when StatisticCall-Class exists
    public MutableLiveData<List<String>> mFavourites=new MutableLiveData<>();
    public HistoryViewModel() {
        List<String> testData=new LinkedList<>();
        testData.add("item 1");
        testData.add("item 2");
        mFavourites.setValue(testData);
    }


    public LiveData<List<String>> getFavourites(){
        return mFavourites;
    }
}