package de.dhbw.corona_world_app.ui.favourites;

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

public class FavouriteFragment extends Fragment {

    private FavouriteViewModel favouriteViewModel;
    protected RecyclerView mFavouriteRecyclerView;
    protected StatisticCallAdapter mFavouriteAdapter;
    protected RecyclerView.LayoutManager mFavouriteLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: get Data from local File Storage
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouriteViewModel =
                new ViewModelProvider(this).get(FavouriteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);
        mFavouriteRecyclerView =root.findViewById(R.id.favRecyclerView);
        mFavouriteLayoutManager =new LinearLayoutManager(getActivity());
        //TODO read about Saved Instances (sample app)
        //setup Favourite List

        mFavouriteRecyclerView.setLayoutManager(mFavouriteLayoutManager);
        mFavouriteRecyclerView.scrollToPosition(0);
        mFavouriteAdapter =new StatisticCallAdapter(R.layout.favourite_row_item);
        favouriteViewModel.mFavourites.observe(getViewLifecycleOwner(), strings -> mFavouriteAdapter.submitList(strings));
        mFavouriteRecyclerView.setAdapter(mFavouriteAdapter);

        return root;
    }

}
