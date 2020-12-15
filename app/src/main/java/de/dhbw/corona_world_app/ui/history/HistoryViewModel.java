package de.dhbw.corona_world_app.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

public class HistoryViewModel extends ViewModel {

    //TODO: change to Statistic-Call when StatisticCall-Class exists
    public MutableLiveData<List<String>> mHistory=new MutableLiveData<>();
    public HistoryViewModel() {
        List<String> testData=new LinkedList<>();
        for(int i=0;i<50;++i){
            testData.add("Item "+i);
        }
        mHistory.setValue(testData);
    }

}