package de.dhbw.corona_world_app.ui.tools;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import de.dhbw.corona_world_app.R;

//TODO Change String to StatisticCall
public class StatisticCallAdapter<T, VH extends RecyclerView.ViewHolder & StatisticCallViewHolderInterface<T>> extends ListAdapter<T, VH> {
    private final int itemLayoutID;
    private final Class<VH> vhClass;
    private boolean multiSelectForDeleteActivated=false;

    //used in order to figure out fast if an item is to be deleted or not
    private final HashSet<Integer> selectedItemsToDelete =new HashSet<>();

    private final ActionMode.Callback actionMode= new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenu().close();
            mode.getMenuInflater().inflate(R.menu.top_action_bar_delete_menu,menu);
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

    public StatisticCallAdapter(int itemLayoutID, Class<VH> classOfVH, StatisticCallAdapterItemOnActionCallback itemOnActionCallback, StatisticCallDeleteInterface deleteInterface) {
        super(new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.equals(newItem);
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.itemOnActionCallback=itemOnActionCallback;
        this.deleteInterface = deleteInterface;
        this.itemLayoutID = itemLayoutID;
        vhClass = classOfVH;
    }

    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemLayoutID, parent, false);
        //use reflection to instantiate ViewHolder
        VH t = null;
        try {
            t = vhClass.getConstructor(View.class).newInstance(view);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(this.getClass().getName(), Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
        //TODO cannot be null
        return Objects.requireNonNull(t);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
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

    private void selectItemToDelete(int itemID,@NonNull VH holder){
        Log.d(this.getClass().getName(),"selecting Item to be deleted");
        if(selectedItemsToDelete.contains(itemID)) {
            selectedItemsToDelete.remove(itemID);
            holder.itemView.setAlpha(1f);
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

