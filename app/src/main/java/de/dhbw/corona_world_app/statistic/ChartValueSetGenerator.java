package de.dhbw.corona_world_app.statistic;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates the data sets that are used to display data in a statistic. It automatically selects different colors from the input List.
 *
 * @author Thomas Meier
 */
public class ChartValueSetGenerator {

    public BarDataSet getBarChartDataSet(@NonNull List<Float> data, @NonNull String label, @NonNull List<Integer> colors) {
        List<BarEntry> entries = new ArrayList<>();
        int xValue = 0;
        for (Float dataEntry : data) {
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

    public PieDataSet getPieChartDataSet(@NonNull List<Float> data, @NonNull List<String> entryNames, @NonNull String label, @NonNull List<Integer> colors) {
        List<PieEntry> entries = new ArrayList<>();
        float sum = 0;
        for (int i = 0; i < data.size(); i++) {
            sum += data.get(i);
        }
        for (int i = 0; i < data.size(); i++) {
            entries.add(new PieEntry(data.get(i) / sum, entryNames.get(i)));
        }
        PieDataSet set = new PieDataSet(entries, label);
        set.setColors(colors);
        set.setValueTextSize(15);
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return round(value * 100, 2) + "%";
            }
        });
        return set;
    }

    public LineDataSet getLineChartDataSet(@NonNull List<Float> data, @NonNull String label, @NonNull List<Integer> colors) {
        List<Entry> entries = new ArrayList<>();
        int xValue = 0;
        for (Float dataEntry : data) {
            entries.add(new BarEntry(xValue++, dataEntry));
        }
        LineDataSet set = new LineDataSet(entries, label);
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

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public Object getLineChart() {
        return null;
    }
}
