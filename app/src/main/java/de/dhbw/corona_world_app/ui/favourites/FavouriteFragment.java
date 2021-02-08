package de.dhbw.corona_world_app.ui.favourites;

import androidx.navigation.fragment.NavHostFragment;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.history.HistoryFragmentDirections;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;
import de.dhbw.corona_world_app.ui.tools.StatisticCallRecyclerViewFragment;

/**
 * This Fragment is used to display the Favourite {@link StatisticCall} made by the User
 * @author Aleksandr Stankoski
 */
public class FavouriteFragment extends StatisticCallRecyclerViewFragment {

    public FavouriteFragment() {
        super();
    }


    @Override
    public StatisticCallDataManager.DataType getDataType() {
        return StatisticCallDataManager.DataType.FAVOURITE_DATA;
    }

    @Override
    public void goToStatistic(StatisticCall request) {
        FavouriteFragmentDirections.ShowStatistic action = FavouriteFragmentDirections.showStatistic(request, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        navHostFragment.getNavController().navigate(action);
    }
}
