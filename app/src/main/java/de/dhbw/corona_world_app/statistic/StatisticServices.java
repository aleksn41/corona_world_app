package de.dhbw.corona_world_app.statistic;

import java.io.ObjectStreamException;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticServices {

    private ChartProvider chartProvider;

    public Object getStatistic(StatisticCall statisticCall){
        if(this.chartProvider==null){
            chartProvider = new ChartProvider();
        }
        switch(statisticCall.getChartType()){
            case BAR: return chartProvider.getBarChart();
            case PIE: return chartProvider.getPieChart();
            case LINE: return chartProvider.getLineChart();
            default: throw new IllegalArgumentException("This chart type is not accepted!");
        }
    }
}
