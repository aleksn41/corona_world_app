package de.dhbw.corona_world_app.ui.statistic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatisticViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StatisticViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Statistics");
    }

    public LiveData<String> getText() {
        return mText;
    }
}