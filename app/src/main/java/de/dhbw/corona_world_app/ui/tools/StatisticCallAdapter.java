package de.dhbw.corona_world_app.ui.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;

class StatisticCallViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public StatisticCallViewHolder(View view) {
        super(view);
        textView =  view.findViewById(R.id.itemTextView);
    }

    public TextView getTextView() {
        return textView;
    }
}

//#TODO Change String to StatisticCall
public class StatisticCallAdapter extends ListAdapter<String,StatisticCallViewHolder> {

    public StatisticCallAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public StatisticCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new StatisticCallViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StatisticCallViewHolder holder, int position) {
        holder.getTextView().setText(getItem(position));
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

