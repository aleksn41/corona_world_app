package de.dhbw.corona_world_app.ui.statistic.tools;

import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import de.dhbw.corona_world_app.R;

//TODO Change String to StatisticCall
public class StatisticCallAdapter extends ListAdapter<Pair<String,Boolean>, StatisticCallViewHolder> {
    private boolean multiSelectForDeleteActivated=false;

    //used in order to figure out fast if an item is to be deleted or not
    private final HashSet<Integer> selectedItemsToDelete =new HashSet<>();

    //need to hold a reference to the ActionMode in order to manually close it if 0 items are selected
    private ActionMode mActionMode;

    //used to load new Actionbar if item is selected for deleting
    private final ActionMode.Callback actionMode= new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.top_action_bar_delete_menu,menu);
            mActionMode=mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //delete All Items
            Log.d(this.getClass().getName(),"clicked on Item: "+item.toString());
            deleteSelectedItems();
            mode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelectForDeleteActivated=false;
            selectedItemsToDelete.clear();
            notifyDataSetChanged();
        }
    };


    //used to add an Callback when an certain Action is performed on an item
    private final StatisticCallAdapterItemOnActionCallback itemOnActionCallback;

    //used to Inform the List-Owner if an Item is supposed to be deleted
    private final StatisticCallDeleteInterface deleteInterface;

    public StatisticCallAdapter(StatisticCallAdapterItemOnActionCallback itemOnActionCallback, StatisticCallDeleteInterface deleteInterface) {
        super(new DiffUtil.ItemCallback<Pair<String,Boolean>>() {

            @Override
            public boolean areItemsTheSame(@NonNull Pair<String, Boolean> oldItem, @NonNull Pair<String, Boolean> newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Pair<String, Boolean> oldItem, @NonNull Pair<String, Boolean> newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.itemOnActionCallback=itemOnActionCallback;
        this.deleteInterface = deleteInterface;
    }

    @NotNull
    @Override
    public StatisticCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statistic_call_row_item, parent, false);
        return new StatisticCallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticCallViewHolder holder, int position) {
        //give the itemOnActionCallback Interface to the Item, such that it can implement its own logic
        holder.setItem(getItem(position),position,itemOnActionCallback);

        //tell Fragment and ViewModel that an Item should be marked for deletion and Deletion Mode should be activated if not already on
        holder.itemView.setOnLongClickListener(v -> {
            if(!multiSelectForDeleteActivated){
                deleteInterface.enterDeleteMode(actionMode);
                multiSelectForDeleteActivated=true;
                selectItemToDelete(position,holder);
            }
            return true;
        });
        //if the item is clicked, go to Statistic with the call TODO instead show pop up with more info and a confirmation he wants to see the statistic
        holder.itemView.setOnClickListener(v -> {
            if(multiSelectForDeleteActivated){
                selectItemToDelete(position,holder);
            }else{
                //TODO
                //goToStatistic(getItem(position))
            }
        });
    }

    private void selectItemToDelete(int itemID,@NonNull StatisticCallViewHolder holder){
        Log.d(this.getClass().getName(),"selecting Item to be deleted");
        if(selectedItemsToDelete.contains(itemID)) {
            selectedItemsToDelete.remove(itemID);
            holder.itemView.setAlpha(1f);
            if(selectedItemsToDelete.isEmpty())mActionMode.finish();
        }else{
            selectedItemsToDelete.add(itemID);
            holder.itemView.setAlpha(0.3f);
        }
    }

    private void deleteSelectedItems(){
        ArrayList<Integer> sortedResult=new ArrayList<>(selectedItemsToDelete);
        Collections.sort(sortedResult);
        deleteInterface.deleteItems(sortedResult);
    }
}

