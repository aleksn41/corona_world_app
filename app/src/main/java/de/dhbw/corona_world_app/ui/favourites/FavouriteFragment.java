package de.dhbw.corona_world_app.ui.favourites;

import androidx.core.util.Pair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.StatisticCallRecyclerViewFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class FavouriteFragment extends StatisticCallRecyclerViewFragment {


    @Override
    public void setupOnCreateViewAfterInitOfRecyclerView() {

    }

    @Override
    public void initViewModelData(StatisticCallViewModel statisticCallViewModel) {
        List<Pair<StatisticCall, Boolean>> testData = new LinkedList<>();
        ISOCountry[] allCountries=ISOCountry.values();
        ChartType[] allChartTypes=ChartType.values();
        Criteria[] allCriteria=Criteria.values();
        for (int i = 0; i < 50; ++i) {
            testData.add(Pair.create(new StatisticCall(Collections.singletonList(allCountries[i%allCountries.length]),allChartTypes[i%allChartTypes.length],Collections.singletonList(allCriteria[i%allCriteria.length])), true));
        }
        statisticCallViewModel.mStatisticCallsAndMark.setValue(testData);
    }
}
