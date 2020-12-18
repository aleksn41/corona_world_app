package de.dhbw.corona_world_app.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ui.tools.StatisticCallAdapter;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    protected RecyclerView mHistoryRecyclerView;
    protected StatisticCallAdapter<String,HistoryItemViewHolder> mStatisticCallAdapter;
    protected RecyclerView.LayoutManager mHistoryLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: get Data from local File Storage
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        mHistoryRecyclerView =root.findViewById(R.id.historyRecyclerView);
        mHistoryLayoutManager =new LinearLayoutManager(getActivity());
        //TODO read about Saved Instances (sample app)
        //setup Favourite List
        
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryRecyclerView.scrollToPosition(0);
        mStatisticCallAdapter =new StatisticCallAdapter<>(R.layout.history_row_item,HistoryItemViewHolder.class,null, null );
        historyViewModel.mHistory.observe(getViewLifecycleOwner(), strings -> mStatisticCallAdapter.submitList(strings));
        mHistoryRecyclerView.setAdapter(mStatisticCallAdapter);

        return root;
    }

}