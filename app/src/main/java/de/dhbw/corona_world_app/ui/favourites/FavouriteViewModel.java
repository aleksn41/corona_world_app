package de.dhbw.corona_world_app.ui.favourites;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.dhbw.corona_world_app.ui.tools.Pair;

public class FavouriteViewModel extends ViewModel {
    //TODO: change to Statistic-Call when StatisticCall-Class exists
    public MutableLiveData<List<Pair<String, Boolean>>> mFavourites = new MutableLiveData<>();

    public FavouriteViewModel() {
        List<Pair<String, Boolean>> testData = new LinkedList<>();
        for (int i = 0; i < 50; ++i) {
            testData.add(Pair.makePair("Item " + i, true));
        }
        mFavourites.setValue(testData);
    }

    public void toggleFavMark(int position) {
        Pair<String, Boolean> currentItem = Objects.requireNonNull(mFavourites.getValue()).get(position);
        mFavourites.getValue().set(position, Pair.makePair(currentItem.first, !currentItem.second));
        mFavourites.postValue(mFavourites.getValue());
    }

    public void deleteItems(ArrayList<Integer> Ids) {
        List<Pair<String, Boolean>> newList = new LinkedList<>();
        int length = Objects.requireNonNull(mFavourites.getValue()).size();
        int counterOfItemsToDelete=0;
        for (int i = 0; i < length; ++i) {
            if(counterOfItemsToDelete<Ids.size()&&Ids.get(counterOfItemsToDelete)==i)++counterOfItemsToDelete;
            else newList.add(mFavourites.getValue().get(i));
        }
        mFavourites.postValue(newList);
    }
}
