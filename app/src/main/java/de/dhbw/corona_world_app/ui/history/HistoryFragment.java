package de.dhbw.corona_world_app.ui.history;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.ui.favourites.FavouriteFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallRecyclerViewFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class HistoryFragment extends StatisticCallRecyclerViewFragment {


    private static final String HISTORY_FILE_NAME = "history.txt";
    private static final boolean IS_FAVOURITE = false;

    private static final String TAG = FavouriteFragment.class.getSimpleName();

    public HistoryFragment() {
        super();
    }

    @Override
    public void setupOnCreateViewAfterInitOfRecyclerView() {

    }

    @Override
    public void initViewModelData(StatisticCallViewModel statisticCallViewModel) {
        try {
            statisticCallViewModel.init(new File(requireActivity().getFilesDir(), HISTORY_FILE_NAME), ThreadPoolHandler.getInstance(),IS_FAVOURITE);
        } catch (IOException e) {
            Log.e(TAG,"could not load or create File",e);
            //TODO inform user
        }
        Future<Void> future=statisticCallViewModel.getMoreData();
        try {
            future.get();
        } catch (ExecutionException e) {
            Throwable error=e.getCause();
            if(error instanceof IOException){
                //check if error is undoable
            }else if(error instanceof DataException){
                //inform User that data is corrupt and must be remade
            }
        }catch (InterruptedException e){
            Log.e(TAG,"Thread has been interrupted",e);
            future.cancel(false);
        }
    }
}