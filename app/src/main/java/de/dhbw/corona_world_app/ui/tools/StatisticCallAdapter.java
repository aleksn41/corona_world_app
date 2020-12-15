package de.dhbw.corona_world_app.ui.tools;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ui.history.HistoryViewModel;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

//TODO Change String to StatisticCall
public class StatisticCallAdapter<VH extends RecyclerView.ViewHolder&StatisticCallViewHolderInterface> extends ListAdapter<String,VH> {
    private final int itemLayoutID;
    private final Class<VH> vhClass;
    public StatisticCallAdapter(int itemLayoutID,Class<VH> classOfVH) {
        super(DIFF_CALLBACK);
        this.itemLayoutID=itemLayoutID;
        vhClass=classOfVH;
    }


    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemLayoutID, parent, false);
        //use reflection to instantiate ViewHolder
        VH t = null;
        try {
            t=vhClass.getConstructor(View.class).newInstance(view);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException| IllegalAccessException e) {
            Log.e(this.getClass().getName(), Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
        //TODO cannot be null
        return Objects.requireNonNull(t);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        //holder.getTextView().setText(getItem(position));
        holder.setItem(getItem(position));
    }

    public static final DiffUtil.ItemCallback<String> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull String oldItem, @NonNull String newItem) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldItem.equals(newItem);
                }
            };

}

