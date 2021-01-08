package de.dhbw.corona_world_app.ui.tools;

import android.graphics.Color;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticCallViewHolder extends RecyclerView.ViewHolder implements StatisticCallViewHolderInterface<Pair<StatisticCall, Boolean>> {
    private final TextView textView;
    private final ImageView imageView;

    private static final char ITEM_SEPARATOR = ',';
    public StatisticCallViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.StatisticCallItemTextView);
        imageView = view.findViewById(R.id.statisticCallItemImageView);
    }

    public void setItem(Pair<StatisticCall, Boolean> item) {
        //TODO Change what Information appears in what order
        Objects.requireNonNull(item.first);
        Objects.requireNonNull(item.second);
        textView.setText(itemView.getContext().getString(R.string.statistic_call_info, listOfStringToString(item.first.getCountryList().parallelStream().map(Enum::toString).collect(Collectors.toList())), listOfStringToString(item.first.getCriteriaList().parallelStream().map(Enum::toString).collect(Collectors.toList())), item.first.getChartType(),item.first.getStartDate().format(StatisticCall.DATE_FORMAT),item.first.getEndDate()==null?"Now":item.first.getEndDate().format(StatisticCall.DATE_FORMAT)));
        //TODO Get Colors from Color Resource
        //TODO support Light mode
        imageView.setColorFilter(Color.parseColor(item.second ? "yellow" : "white"));
    }

    public ImageView getImageView() {
        return imageView;
    }

    private String listOfStringToString(List<String> list) {
        StringBuilder stringbuilder = new StringBuilder(list.size() * 4);
        for (int i = 0; i < list.size(); i++) {
            stringbuilder.append(list.get(i));
            stringbuilder.append(ITEM_SEPARATOR);
        }
        stringbuilder.setLength(stringbuilder.length() - 1);
        return stringbuilder.toString();
    }

}
