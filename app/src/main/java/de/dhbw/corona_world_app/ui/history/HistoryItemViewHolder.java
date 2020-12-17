package de.dhbw.corona_world_app.ui.history;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ui.tools.StatisticCallAdapterItemOnActionCallback;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewHolderInterface;

public class HistoryItemViewHolder extends RecyclerView.ViewHolder implements StatisticCallViewHolderInterface<String> {
    private final TextView textView;

    public HistoryItemViewHolder(View view) {
        super(view);
        textView =  view.findViewById(R.id.historyItemTextView);
    }


    @Override
    public void setItem(String Item, int ItemPosition, StatisticCallAdapterItemOnActionCallback onActionCallback) {
        textView.setText(Item);
    }
}
