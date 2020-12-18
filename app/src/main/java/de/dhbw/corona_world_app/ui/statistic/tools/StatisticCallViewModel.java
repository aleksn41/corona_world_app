package de.dhbw.corona_world_app.ui.statistic.tools;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class StatisticCallViewModel extends ViewModel {
    //TODO: change to Statistic-Call when StatisticCall-Class exists
    public MutableLiveData<List<Pair<String, Boolean>>> mStatisticCallsAndMark = new MutableLiveData<>();

    public void toggleFavMark(int position) {
        Pair<String, Boolean> currentItem = Objects.requireNonNull(mStatisticCallsAndMark.getValue()).get(position);
        mStatisticCallsAndMark.getValue().set(position, Pair.create(currentItem.first, !currentItem.second));
        mStatisticCallsAndMark.postValue(mStatisticCallsAndMark.getValue());
    }

    public void deleteItems(ArrayList<Integer> Ids) {
        List<Pair<String, Boolean>> newList = new LinkedList<>();
        int length = Objects.requireNonNull(mStatisticCallsAndMark.getValue()).size();
        int counterOfItemsToDelete = 0;
        for (int i = 0; i < length; ++i) {
            if (counterOfItemsToDelete < Ids.size() && Ids.get(counterOfItemsToDelete) == i)
                ++counterOfItemsToDelete;
            else newList.add(mStatisticCallsAndMark.getValue().get(i));
        }
        mStatisticCallsAndMark.postValue(newList);
    }
}
