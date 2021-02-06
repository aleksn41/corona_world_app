package de.dhbw.corona_world_app.ui.tools;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

import static android.view.View.GONE;


public class StatisticCallAdapter extends ListAdapter<Pair<StatisticCall,Boolean>, StatisticCallViewHolder> {
    private boolean multiSelectForDeleteActivated=false;

    //used in order to figure out fast if an item is to be deleted or not
    private final HashSet<Integer> selectedItems =new HashSet<>();

    //need to hold a reference to the ActionMode in order to manually close it if 0 items are selected
    private ActionMode mActionMode;

    //blacklist set by DataManager
    private List<Integer> blackListedIndices=new ArrayList<>();

    //used to load new Actionbar if item is selected for deleting
    private final ActionMode.Callback actionMode= new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.top_action_bar_delete_menu,menu);
            mActionMode=mode;
            multiSelectForDeleteActivated=true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId()==R.id.action_delete) {
                //delete All Items
                Log.d(this.getClass().getName(), "clicked on delete Item");
                deleteSelectedItems();
                mode.finish();
            }else if (item.getItemId()==R.id.action_favourite){
                toggleFavForSelected();
            }else if(item.getItemId()==R.id.select_all){
                selectAllItems();
            }else if(item.getItemId()==R.id.un_select_all){
                unSelectAllItems();
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.v(this.getClass().getName(),"exit delete Mode");
            multiSelectForDeleteActivated=false;
            selectedItems.clear();
            notifyDataSetChanged();
            mActionMode=null;
        }
    };


    //used to add an Callback when an certain Action is performed on an item
    private final StatisticCallAdapterItemOnActionCallback itemOnActionCallback;

    //used to Inform the List-Owner if an Item is supposed to be deleted
    private final StatisticCallActionModeInterface actionModeInterface;

    //used to add an Callback when the last item is loaded
    private final StatisticCallAdapterOnLastItemLoaded onLastItemLoaded;

    //used to show Statistic when user wants to see a certain Statistic
    private final ShowStatisticInterface showStatisticInterface;

    public StatisticCallAdapter(StatisticCallAdapterItemOnActionCallback itemOnActionCallback, StatisticCallActionModeInterface actionModeInterfaceInterface, StatisticCallAdapterOnLastItemLoaded onLastItemLoaded, ShowStatisticInterface showStatisticInterface) {
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
        this.actionModeInterface = actionModeInterfaceInterface;
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
        //if this index is blacklisted, do not show it (List contain method is overridden such that it uses binary search)
        if(blackListedIndices.contains(holder.getAdapterPosition())){
            holder.itemView.setVisibility(GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else if(holder.itemView.getVisibility()!=View.VISIBLE){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
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
                actionModeInterface.enterDeleteMode(actionMode);
                selectItem(holder.getAdapterPosition(),holder);
            }
            return true;
        });

        //if the item is clicked, go to Statistic with the call
        holder.itemView.setOnClickListener(v -> {
            if(multiSelectForDeleteActivated){
                selectItem(holder.getAdapterPosition(),holder);
            }else{
                //TODO  show pop up with more info and a confirmation he wants to see the statistic
                goToStatistic(getItem(holder.getAdapterPosition()).first);
            }
        });
        setMarkedItem(holder, selectedItems.contains(holder.getAdapterPosition()));
    }

    public void selectAllItems(){
        List<Integer> allNonBlacklistedIndices=new ArrayList<>(getItemCount());
        for (int i = 0; i < getItemCount(); i++) {
            if(!blackListedIndices.contains(i)){
                allNonBlacklistedIndices.add(i);
            }
        }
        selectedItems.addAll(allNonBlacklistedIndices);
        if(mActionMode==null)actionModeInterface.enterDeleteMode(actionMode);
        notifyDataSetChanged();
    }

    public void unSelectAllItems(){
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getBlacklistedItemsSize(){
        return blackListedIndices.size();
    }

    private void selectItem(int itemID, @NonNull StatisticCallViewHolder holder){
        Log.d(this.getClass().getName(),"selecting Item to be deleted");
        boolean alreadySelected= selectedItems.contains(itemID);
        if(alreadySelected) {
            selectedItems.remove(itemID);
            if(selectedItems.isEmpty())mActionMode.finish();
        }else{
            selectedItems.add(itemID);
        }
        setMarkedItem(holder,!alreadySelected);
        Log.d(this.getClass().getName(),"currentList of Items selected: "+ selectedItems.toString());
    }

    private void deleteSelectedItems(){
        actionModeInterface.deleteItems(selectedItems);
    }

    private void toggleFavForSelected(){
        actionModeInterface.favouriteItems(selectedItems);
    }

    private void setMarkedItem(StatisticCallViewHolder holder,boolean mark){
        holder.itemView.setAlpha(mark?0.3f:1f);
    }

    private void goToStatistic(StatisticCall request){
        showStatisticInterface.showStatisticCall(request);
    }

    public void setBlackListedIndices(List<Integer> blackListedIndices) {
        this.blackListedIndices = blackListedIndices;
    }
}

