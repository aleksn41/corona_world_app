package de.dhbw.corona_world_app.ui.favourites;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

public class FavouriteViewModel extends ViewModel {
    //TODO: change to Statistic-Call when StatisticCall-Class exists
    public MutableLiveData<List<String>> mFavourites=new MutableLiveData<>();
    public FavouriteViewModel() {
        List<String> testData=new LinkedList<>();
        for(int i=0;i<50;++i){
            testData.add("Item "+i);
        }
        mFavourites.setValue(testData);
    }
}
