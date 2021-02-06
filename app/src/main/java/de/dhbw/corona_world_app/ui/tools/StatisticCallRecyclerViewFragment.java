package de.dhbw.corona_world_app.ui.tools;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public abstract class StatisticCallRecyclerViewFragment extends Fragment {
    protected RecyclerView statisticCallRecyclerView;
    protected StatisticCallAdapter statisticCallAdapter;
    protected RecyclerView.LayoutManager layoutManager;
    protected StatisticCallViewModel statisticCallViewModel;
    private ActionMode deleteMode;
    private Menu menu;
    private boolean customMenuShowing=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticCallViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistical_call_list, container, false);
        setHasOptionsMenu(true);
        if (statisticCallViewModel.isNotInit()) {
            Log.d(this.getClass().getName(), "init ViewModel");
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
        }, new StatisticCallActionModeInterface() {
            @Override
            public void enterDeleteMode(ActionMode.Callback callback) {
                Log.v(this.getClass().getName(), "entering Delete Mode");
                deleteMode = requireActivity().startActionMode(callback);
            }

            @Override
            public void deleteItems(Set<Integer> ItemIds) {
                Log.v(this.getClass().getName(), "deleting selected favourite Items");
                statisticCallViewModel.deleteItems(ItemIds, getDataType());
            }

            @Override
            public void favouriteItems(Set<Integer> ItemIds) {
                for (Integer itemId : ItemIds) {
                    statisticCallViewModel.toggleFav(itemId,getDataType());
                }
                if(getDataType()== StatisticCallDataManager.DataType.FAVOURITE_DATA){
                    deleteMode.finish();
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
                statisticCallAdapter.setBlackListedIndices(statisticCallViewModel.getBlackListedIndices(getDataType()));
                statisticCallAdapter.submitList(pairs);
                //update menu if there are no items/ if items have been added
                if(customMenuShowing&&pairs.size()-statisticCallAdapter.getBlacklistedItemsSize()<=0&&menu!=null) {
                    menu.clear();
                    requireActivity().getMenuInflater().inflate(R.menu.top_action_bar_menu,menu);
                    customMenuShowing=false;
                }else if (!customMenuShowing&&pairs.size()-statisticCallAdapter.getBlacklistedItemsSize()>0&&menu!=null){
                    menu.clear();
                    requireActivity().getMenuInflater().inflate(R.menu.top_action_bar_select_menu,menu);
                    customMenuShowing=true;
                }
                statisticCallAdapter.notifyDataSetChanged();
                Log.v(this.getClass().getName(), "updated List");
            }
        }, getDataType());
        statisticCallRecyclerView.setAdapter(statisticCallAdapter);
        Log.d(this.getClass().getName(), "finished RecycleView");

        Log.d(this.getClass().getName(), "start Custom OnCreateView Function");
        setupOnCreateViewAfterInitOfRecyclerView();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.menu=menu;
        if(statisticCallAdapter.getItemCount()-statisticCallAdapter.getBlacklistedItemsSize()>0){
            menu.clear();
            inflater.inflate(R.menu.top_action_bar_select_menu,menu);
        }else{
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.select_all) {
            statisticCallAdapter.selectAllItems();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        Log.d(this.getClass().getName() + "|" + getDataType(), "Pausing Fragment");
        if (deleteMode != null) {
            deleteMode.finish();
            deleteMode = null;
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(this.getClass().getName() + "|" + getDataType(), "Stopping Fragment");
        Log.d(this.getClass().getName() + "|" + getDataType(), "saving All Data");
        statisticCallViewModel.saveAllData().whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                if (throwable != null) {
                    Throwable e = throwable.getCause();
                    if (e instanceof IOException) {
                        Log.e(this.getClass().getName(), ErrorCode.CANNOT_SAVE_FILE.toString(), throwable);
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(requireContext(), ErrorCode.CANNOT_SAVE_FILE, null));
                    } else {
                        Log.wtf(this.getClass().getName(), ErrorCode.UNEXPECTED_ERROR.toString(), throwable);
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(requireContext(), ErrorCode.UNEXPECTED_ERROR, null));
                    }
                }
            }
        });
        super.onStop();
    }

    public abstract void setupOnCreateViewAfterInitOfRecyclerView();

    public abstract StatisticCallDataManager.DataType getDataType();

    public abstract ShowStatisticInterface getShowStatisticInterface();

    public abstract void initViewModelData(StatisticCallViewModel statisticCallViewModel);

    protected void tryRepairingData() {
        ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CANNOT_READ_FILE, (dialog, which) -> {
            //TODO implement check to see if Data can be recovered
            boolean canBeRecovered = false;
            if (canBeRecovered) {
                //recover Data
            } else {
                ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CANNOT_RESTORE_FILE, (dialog1, which1) -> {
                    try {
                        statisticCallViewModel.deleteAllItems();
                    } catch (IOException e) {
                        Log.e(this.getClass().getName(), ErrorCode.CANNOT_DELETE_FILE.toString(), e);
                        ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.UNEXPECTED_ERROR, null);
                    }
                }, "I understand");
            }
        });
    }
}
