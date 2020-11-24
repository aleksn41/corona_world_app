package de.dhbw.corona_world_app.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.dhbw.corona_world_app.R;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

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
        mRecyclerView=root.findViewById(R.id.favRecyclerView);
        mLayoutManager=new LinearLayoutManager(getActivity());
        //TODO read about Saved Instances (sample app)
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mAdapter=new HistoryAdapter();
        historyViewModel.mFavourites.observe(getViewLifecycleOwner(), strings -> mAdapter.submitList(strings));
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

}