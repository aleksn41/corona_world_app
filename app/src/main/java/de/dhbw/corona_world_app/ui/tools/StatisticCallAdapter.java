package de.dhbw.corona_world_app.ui.tools;

import android.util.Log;
import androidx.core.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.statistic.StatisticRequestFragmentDirections;

//TODO instead of Pairs use an extra Hashset for the selected Items
public class StatisticCallAdapter extends ListAdapter<Pair<StatisticCall,Boolean>, StatisticCallViewHolder> {
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
            Log.d(this.getClass().getName(),"clicked on delete Item");
            deleteSelectedItems();
            mode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.v(this.getClass().getName(),"exit delete Mode");
            multiSelectForDeleteActivated=false;
            selectedItemsToDelete.clear();
            notifyDataSetChanged();
        }
    };


    //used to add an Callback when an certain Action is performed on an item
    private final StatisticCallAdapterItemOnActionCallback itemOnActionCallback;

    //used to Inform the List-Owner if an Item is supposed to be deleted
    private final StatisticCallDeleteInterface deleteInterface;

    //used to add an Callback when the last item is loaded
    private final StatisticCallAdapterOnLastItemLoaded onLastItemLoaded;

    //used to show Statistic when user wants to see a certain Statistic
    private final ShowStatisticInterface showStatisticInterface;

    public StatisticCallAdapter(StatisticCallAdapterItemOnActionCallback itemOnActionCallback, StatisticCallDeleteInterface deleteInterface,StatisticCallAdapterOnLastItemLoaded onLastItemLoaded,ShowStatisticInterface showStatisticInterface) {
        super(new DiffUtil.ItemCallback<Pair<StatisticCall,Boolean>>() {

            @Override
            public boolean areItemsTheSame(@NonNull Pair<StatisticCall, Boolean> oldItem, @NonNull Pair<StatisticCall, Boolean> newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Pair<StatisticCall, Boolean> oldItem, @NonNull Pair<StatisticCall, Boolean> newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.itemOnActionCallback=itemOnActionCallback;
        this.deleteInterface = deleteInterface;
        this.onLastItemLoaded=onLastItemLoaded;
        this.showStatisticInterface=showStatisticInterface;
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
        holder.setItem(getItem(holder.getAdapterPosition()));
        //setActionCallback if available
        if(itemOnActionCallback!=null)holder.getSparkButton().setOnClickListener(v -> itemOnActionCallback.callback(holder.getAdapterPosition()));
        //check if its the last item
        if(holder.getAdapterPosition()==getItemCount()-1){
            onLastItemLoaded.onLastItemLoaded();
        }
        //tell Fragment and ViewModel that an Item should be marked for deletion and Deletion Mode should be activated if not already on
        holder.itemView.setOnLongClickListener(v -> {
            if(!multiSelectForDeleteActivated){
                deleteInterface.enterDeleteMode(actionMode);
                multiSelectForDeleteActivated=true;
                selectItemToDelete(holder.getAdapterPosition(),holder);
            }
            return true;
        });

        //if the item is clicked, go to Statistic with the call
        holder.itemView.setOnClickListener(v -> {
            if(multiSelectForDeleteActivated){
                selectItemToDelete(holder.getAdapterPosition(),holder);
            }else{
                //TODO  show pop up with more info and a confirmation he wants to see the statistic
                goToStatistic(getItem(holder.getAdapterPosition()).first);
            }
        });
        setMarkedItem(holder,selectedItemsToDelete.contains(holder.getAdapterPosition()));
    }

    private void selectItemToDelete(int itemID,@NonNull StatisticCallViewHolder holder){
        Log.d(this.getClass().getName(),"selecting Item to be deleted");
        boolean alreadyInDeleted=selectedItemsToDelete.contains(itemID);
        if(alreadyInDeleted) {
            selectedItemsToDelete.remove(itemID);
            if(selectedItemsToDelete.isEmpty())mActionMode.finish();
        }else{
            selectedItemsToDelete.add(itemID);
        }
        setMarkedItem(holder,!alreadyInDeleted);
        Log.d(this.getClass().getName(),"currentList of Items selected: "+selectedItemsToDelete.toString());
    }

    private void deleteSelectedItems(){
        deleteInterface.deleteItems(selectedItemsToDelete);
    }

    private void setMarkedItem(StatisticCallViewHolder holder,boolean mark){
        holder.itemView.setAlpha(mark?0.3f:1f);
    }

    private void goToStatistic(StatisticCall request){
        showStatisticInterface.showStatisticCall(request);
    }
}

