package de.dhbw.corona_world_app.ui.favourites;

import android.util.Pair;

import java.util.LinkedList;
import java.util.List;

import de.dhbw.corona_world_app.ui.statistic.tools.StatisticCallRecyclerViewFragment;
import de.dhbw.corona_world_app.ui.statistic.tools.StatisticCallViewModel;

public class FavouriteFragment extends StatisticCallRecyclerViewFragment {


    @Override
    public void setupOnCreateViewAfterInitOfRecyclerView() {

    }

    @Override
    public void initViewModelData(StatisticCallViewModel statisticCallViewModel) {
        List<Pair<String, Boolean>> testData = new LinkedList<>();
        for (int i = 0; i < 50; ++i) {
            testData.add(Pair.create("Item " + i, true));
        }
        statisticCallViewModel.mStatisticCallsAndMark.setValue(testData);
    }
}
