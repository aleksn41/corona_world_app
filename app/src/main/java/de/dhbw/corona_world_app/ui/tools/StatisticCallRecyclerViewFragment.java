package de.dhbw.corona_world_app.ui.tools;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import de.dhbw.corona_world_app.R;

public abstract class StatisticCallRecyclerViewFragment extends Fragment {
    protected StatisticCallDataManager statisticCallDataManager;
    protected RecyclerView statisticCallRecyclerView;
    protected StatisticCallAdapter statisticCallAdapter;
    protected RecyclerView.LayoutManager layoutManager;
    private StatisticCallViewModel statisticCallViewModel;
    private ActionMode deleteMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticCallViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistical_call_list, container, false);

        if(!statisticCallViewModel.isInit()){
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
                statisticCallViewModel.toggleFavMark(itemID);
                Log.d(this.getClass().getName(),"toggled Item number "+itemID);
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
                statisticCallViewModel.deleteItems(ItemIds);
            }
        });
        statisticCallViewModel.getMutableData().observe(getViewLifecycleOwner(), pairs -> {
            statisticCallAdapter.submitList(pairs);
            statisticCallAdapter.notifyDataSetChanged();
            Log.v(this.getClass().getName(), "updated List");
        });
        statisticCallRecyclerView.setAdapter(statisticCallAdapter);
        Log.d(this.getClass().getName(), "finished RecycleView");

        Log.d(this.getClass().getName(), "start Custom OnCreateView Function");
        setupOnCreateViewAfterInitOfRecyclerView();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(this.getClass().getName(), "Pausing Fragment");
        if (deleteMode != null) {
            deleteMode.finish();
            deleteMode = null;
        }
        statisticCallViewModel.updateFavouriteMarks();
    }

    public abstract void setupOnCreateViewAfterInitOfRecyclerView();

    public abstract void initViewModelData(StatisticCallViewModel statisticCallViewModel);
}
