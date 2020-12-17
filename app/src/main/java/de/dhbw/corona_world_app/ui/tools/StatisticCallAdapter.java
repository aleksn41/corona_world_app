package de.dhbw.corona_world_app.ui.tools;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

//TODO Change String to StatisticCall
public class StatisticCallAdapter<T, VH extends RecyclerView.ViewHolder & StatisticCallViewHolderInterface<T>> extends ListAdapter<T, VH> {
    private final int itemLayoutID;
    private final Class<VH> vhClass;
    private final StatisticCallAdapterItemOnActionCallback itemOnActionCallback;

    public StatisticCallAdapter(int itemLayoutID, Class<VH> classOfVH,StatisticCallAdapterItemOnActionCallback itemOnActionCallback) {
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
        holder.setItem(getItem(position),position,itemOnActionCallback);
    }
}

