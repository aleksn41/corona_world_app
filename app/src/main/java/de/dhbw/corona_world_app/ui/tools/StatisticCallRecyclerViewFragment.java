package de.dhbw.corona_world_app.ui.tools;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public abstract class StatisticCallRecyclerViewFragment extends Fragment {
    protected RecyclerView statisticCallRecyclerView;
    protected StatisticCallAdapter statisticCallAdapter;
    protected RecyclerView.LayoutManager layoutManager;
    protected StatisticCallViewModel statisticCallViewModel;
    private ActionMode deleteMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticCallViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistical_call_list, container, false);
        if(statisticCallViewModel.isNotInit()){
            Log.d(this.getClass().getName(),"init ViewModel");
            initViewModelData(statisticCallViewModel);
        }
        Log.d(this.getClass().getName(), "initiate RecycleView");
        statisticCallRecyclerView = root.findViewById(R.id.statisticCallRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        statisticCallRecyclerView.setLayoutManager(layoutManager);
        statisticCallRecyclerView.scrollToPosition(0);
        statisticCallAdapter = new StatisticCallAdapter(new StatisticCallAdapterItemOnActionCallback() {
            @Override
            public void callback(int itemID) {
                statisticCallViewModel.toggleFav(itemID, getDataType());
                Log.d(this.getClass().getName(), "toggled Item number " + itemID);
            }
        }, new StatisticCallDeleteInterface() {
            @Override
            public void enterDeleteMode(ActionMode.Callback callback) {
                Log.v(this.getClass().getName(), "entering Delete Mode");
                deleteMode = requireActivity().startActionMode(callback);
            }

            @Override
            public void deleteItems(Set<Integer> ItemIds) {
                Log.v(this.getClass().getName(), "deleting selected favourite Items");
                try {
                    statisticCallViewModel.deleteItems(ItemIds, getDataType()).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(this.getClass().getName(), "items could not be deleted", e);
                    //this should not happen
                    ErrorDialog.createBasicErrorDialog(getContext(),"An critical Error has occurred","It seems that something unexpected happened, please restart the App and if that does not help reinstall the App",null);
                }
            }
        }, new StatisticCallAdapterOnLastItemLoaded() {
            @Override
            public void onLastItemLoaded() {
                if (statisticCallViewModel.hasMoreData(getDataType())) {
                    try {
                        Log.d(this.getClass().getName(), "last item reached, loading more Data of " + getDataType());
                        statisticCallViewModel.getMoreData(getDataType());
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(this.getClass().getName(), "error reading more Data", e);
                        tryRepairingData();
                    }
                }
            }
        }, getShowStatisticInterface());
        statisticCallViewModel.observeData(getViewLifecycleOwner(), new Observer<List<Pair<StatisticCall, Boolean>>>() {
            @Override
            public void onChanged(List<Pair<StatisticCall, Boolean>> pairs) {
                statisticCallAdapter.submitList(pairs);
                statisticCallAdapter.notifyDataSetChanged();
                Log.v(this.getClass().getName(), "updated List");
            }
        },getDataType());
        statisticCallRecyclerView.setAdapter(statisticCallAdapter);
        Log.d(this.getClass().getName(), "finished RecycleView");

        Log.d(this.getClass().getName(), "start Custom OnCreateView Function");
        setupOnCreateViewAfterInitOfRecyclerView();
        return root;
    }

    @Override
    public void onPause() {
        Log.d(this.getClass().getName()+"|"+getDataType(), "Pausing Fragment");
        if (deleteMode != null) {
            deleteMode.finish();
            deleteMode = null;
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(this.getClass().getName()+"|"+getDataType(),"Stopping Fragment");
        try {
            statisticCallViewModel.saveAllData();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(this.getClass().getName(),"could not save Data",e);
            Toast.makeText(getContext(), "Could not save Data of this Session, please restart the Application if you want your Session to be saved", Toast.LENGTH_LONG).show();
        }
        super.onStop();
    }

    public abstract void setupOnCreateViewAfterInitOfRecyclerView();

    public abstract StatisticCallDataManager.DataType getDataType();

    public abstract ShowStatisticInterface getShowStatisticInterface();

    public abstract void initViewModelData(StatisticCallViewModel statisticCallViewModel);

    protected void tryRepairingData(){
        ErrorDialog.createBasicErrorDialog(getContext(), "Your Data seems to be corrupt", "We were not able to save new Data, we will try to fix this Problem", (dialog, which) -> {
            //TODO implement check to see if Data can be recovered
            boolean canBeRecovered=false;
            if(canBeRecovered){
                //recover Data
            }else{
                ErrorDialog.createBasicErrorDialog(getContext(), "We were not able to recover your Data", "Your History and your Favourites must be deleted for this app to function properly, we are so sorry", (dialog1, which1) -> {
                    try {
                        statisticCallViewModel.deleteAllItems().get();
                    } catch (ExecutionException | InterruptedException e1) {
                        Log.e(this.getClass().getName()+"|"+getDataType(),"Not able to delete corrupt Data", e1);
                        ErrorDialog.createBasicErrorDialog(getContext(),"There has been an error deleting your Data","Something has gone terribly wrong, please reinstall the app and try again",null);
                    }
                },"I understand");
            }
        });
    }
}
