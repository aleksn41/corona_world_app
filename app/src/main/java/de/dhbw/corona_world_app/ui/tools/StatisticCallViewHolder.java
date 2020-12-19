package de.dhbw.corona_world_app.ui.tools;

import android.graphics.Color;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticCallViewHolder extends RecyclerView.ViewHolder implements StatisticCallViewHolderInterface<Pair<StatisticCall, Boolean>> {
    private final TextView textView;
    private final ImageView imageView;

    public StatisticCallViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.StatisticCallItemTextView);
        imageView = view.findViewById(R.id.statisticCallItemImageView);
    }

    public void setItem(Pair<StatisticCall, Boolean> item, StatisticCallAdapterItemOnActionCallback onActionCallback) {
        //TODO Change what Information appears in what order
        textView.setText(itemView.getContext().getString(R.string.statistic_call_info, item.first.getCountryList().get(0).toString(), item.first.getCriteriaList().get(0).toString(), item.first.getCharttype().toString()));
        //TODO Get Colors from Color Resource
        //TODO support Light mode
        imageView.setColorFilter(Color.parseColor(item.second ? "yellow" : "white"));
        if (onActionCallback != null)
            imageView.setOnClickListener(v -> onActionCallback.callback(getAdapterPosition()));
    }

}
