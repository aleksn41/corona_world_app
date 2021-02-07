package de.dhbw.corona_world_app.ui.tools;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.varunest.sparkbutton.SparkButton;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

/**
 * This {@link RecyclerView.ViewHolder} is used to Display the {@link StatisticCall} in a RecyclerView
 *
 * @author Aleksandr Stankoski
 */
public class StatisticCallViewHolder extends RecyclerView.ViewHolder implements StatisticCallViewHolderInterface<Pair<StatisticCall, Boolean>> {
    private final TextView textView;
    private final SparkButton sparkButton;

    private static final char ITEM_SEPARATOR = ',';

    public StatisticCallViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.StatisticCallItemTextView);
        sparkButton = view.findViewById(R.id.starButton);
    }

    public void setItem(Pair<StatisticCall, Boolean> item) {
        Objects.requireNonNull(item.first);
        Objects.requireNonNull(item.second);
        textView.setText(itemView.getContext().getString(R.string.statistic_call_info, listOfStringToString(item.first.getCountryList().parallelStream().map(Enum::toString).collect(Collectors.toList())), listOfStringToString(item.first.getCriteriaList().parallelStream().map(Enum::toString).collect(Collectors.toList())), item.first.getChartType(), item.first.getStartDate() == StatisticCall.NOW ? "Now" : item.first.getStartDate().format(StatisticCall.DATE_FORMAT), item.first.getEndDate() == StatisticCall.NOW ? "Now" : item.first.getEndDate().format(StatisticCall.DATE_FORMAT)));
        sparkButton.setChecked(item.second);
    }

    public SparkButton getSparkButton() {
        return sparkButton;
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
