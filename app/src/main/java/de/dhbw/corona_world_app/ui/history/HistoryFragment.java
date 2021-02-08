package de.dhbw.corona_world_app.ui.history;

import androidx.navigation.fragment.NavHostFragment;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;
import de.dhbw.corona_world_app.ui.tools.StatisticCallRecyclerViewFragment;

/**
 * This Fragment is used to display the all {@link StatisticCall} made by the User
 *
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

    @Override
    public void goToStatistic(StatisticCall request) {
        HistoryFragmentDirections.ShowStatistic2 action = HistoryFragmentDirections.showStatistic2(request, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        navHostFragment.getNavController().navigate(action);
    }
}