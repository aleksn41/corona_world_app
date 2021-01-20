package de.dhbw.corona_world_app.statistic;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChartValueSetGenerator {

    public BarDataSet getBarChartDataSet(@NonNull List<Float> data, @NonNull String label, @NonNull List<Integer> colors){
        List<BarEntry> entries = new ArrayList<>();
        int xValue = 0;
        for (Float dataEntry: data) {
            entries.add(new BarEntry(xValue++, dataEntry));
        }
        BarDataSet set = new BarDataSet(entries, label);
        set.setColor(colors.get(0));
        colors.remove(0);
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        return set;
    }

    public Object getPieChart(){
        return null;
    }

    public Object getLineChart(){
        return null;
    }
}
