package de.dhbw.corona_world_app.ui.favourites;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ui.tools.Pair;
import de.dhbw.corona_world_app.ui.tools.StatisticCallAdapter;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDeleteInterface;

public class FavouriteFragment extends Fragment {

    private FavouriteViewModel favouriteViewModel;
    protected RecyclerView mFavouriteRecyclerView;
    protected StatisticCallAdapter<Pair<String,Boolean>,FavouriteItemViewHolder> mFavouriteAdapter;
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
        mFavouriteAdapter =new StatisticCallAdapter<>(R.layout.favourite_row_item, FavouriteItemViewHolder.class, itemID -> {
            favouriteViewModel.toggleFavMark(itemID);
            Log.d(this.getClass().getName(), "Item " + itemID + " changed");
        }, new StatisticCallDeleteInterface() {
            @Override
            public void enterDeleteMode(ActionMode.Callback callback) {
                Log.v(this.getClass().getName(),"entering Delete Mode for favourite Items");
                requireActivity().startActionMode(callback);
            }

            @Override
            public void deleteItems(ArrayList<Integer> ItemIds) {
                Log.v(this.getClass().getName(),"deleting selected favourite Items");
                favouriteViewModel.deleteItems(ItemIds);
            }
        });
        favouriteViewModel.mFavourites.observe(getViewLifecycleOwner(), pairs -> {
            mFavouriteAdapter.submitList(pairs);
            mFavouriteAdapter.notifyDataSetChanged();
            Log.v(this.getClass().getName(),"updated Favourite List");
        });

        mFavouriteRecyclerView.setAdapter(mFavouriteAdapter);

        return root;
    }
}
