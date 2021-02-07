package de.dhbw.corona_world_app.ui.history;

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
import de.dhbw.corona_world_app.ui.favourites.FavouriteFragment;
import de.dhbw.corona_world_app.ui.favourites.FavouriteFragmentDirections;
import de.dhbw.corona_world_app.ui.tools.ShowStatisticInterface;
import de.dhbw.corona_world_app.ui.tools.StatisticCallAdapterItemOnActionCallback;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;
import de.dhbw.corona_world_app.ui.tools.StatisticCallRecyclerViewFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

/**
 * This Fragment is used to display the all {@link StatisticCall} made by the User
 * @author Aleksandr Stankoski
 */
public class HistoryFragment extends StatisticCallRecyclerViewFragment {


    public HistoryFragment() {
        super();
    }

    @Override
    public StatisticCallDataManager.DataType getDataType() {
        return StatisticCallDataManager.DataType.ALL_DATA;
    }
}