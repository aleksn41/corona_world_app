package de.dhbw.corona_world_app.statistic;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChartDataProvider {

    private final List<Integer> colors = Arrays.asList(Color.WHITE, Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.RED, Color.YELLOW);

    public Object getBarChartDataSet(List<Double> data, int barColor){
        return null;
    }

    public Object getPieChart(){
        return null;
    }

    public Object getLineChart(){
        return null;
    }
}
