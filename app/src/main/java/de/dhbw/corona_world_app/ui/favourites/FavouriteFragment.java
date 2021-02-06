package de.dhbw.corona_world_app.ui.favourites;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.statistic.StatisticRequestFragmentDirections;
import de.dhbw.corona_world_app.ui.tools.ShowStatisticInterface;
import de.dhbw.corona_world_app.ui.tools.StatisticCallAdapterItemOnActionCallback;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;
import de.dhbw.corona_world_app.ui.tools.StatisticCallRecyclerViewFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class FavouriteFragment extends StatisticCallRecyclerViewFragment {

    private static final String TAG = FavouriteFragment.class.getSimpleName();

    public FavouriteFragment() {
        super();
    }

    @Override
    public void setupOnCreateViewAfterInitOfRecyclerView() {

    }

    @Override
    public StatisticCallDataManager.DataType getDataType() {
        return StatisticCallDataManager.DataType.FAVOURITE_DATA;
    }

    @Override
    public ShowStatisticInterface getShowStatisticInterface() {
        return request -> {
            FavouriteFragmentDirections.ShowStatistic action = FavouriteFragmentDirections.showStatistic(request,false);
            NavHostFragment navHostFragment =
                    (NavHostFragment) requireActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(action);
        };
    }
}
