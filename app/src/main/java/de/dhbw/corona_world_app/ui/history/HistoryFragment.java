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

public class HistoryFragment extends StatisticCallRecyclerViewFragment {

    private static final String TAG = FavouriteFragment.class.getSimpleName();

    public HistoryFragment() {
        super();
    }

    @Override
    public void setupOnCreateViewAfterInitOfRecyclerView() {

    }

    @Override
    public StatisticCallDataManager.DataType getDataType() {
        return StatisticCallDataManager.DataType.ALL_DATA;
    }

    @Override
    public ShowStatisticInterface getShowStatisticInterface() {
        return request -> {
            HistoryFragmentDirections.ShowStatistic2 action = HistoryFragmentDirections.showStatistic2(request,false);
            NavHostFragment navHostFragment =
                    (NavHostFragment) requireActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(action);
        };
    }

    //TODO change this
    @Override
    public void initViewModelData(StatisticCallViewModel statisticCallViewModel) {
        try {
            statisticCallViewModel.init(requireActivity().getFilesDir(), ThreadPoolHandler.getInstance());
        } catch (IOException e) {
            Log.e(TAG,"could not load or create File",e);
            //TODO inform user
        }
        try {
            Future<Void> future=statisticCallViewModel.getMoreData(StatisticCallDataManager.DataType.ALL_DATA);
            Future<Void> future1=statisticCallViewModel.getMoreData(StatisticCallDataManager.DataType.FAVOURITE_DATA);
            future.get();
            future1.get();
        } catch (ExecutionException e) {
            Throwable error=e.getCause();
            Log.e(TAG,"error getting new Data",e);
            if(error instanceof IOException){
                //check if error is undoable
            }else if(error instanceof DataException){
                //inform User that data is corrupt and must be remade
            }
        }catch (InterruptedException e){
            Log.e(TAG,"Thread has been interrupted",e);
            //future.cancel(true);
        }
    }
}