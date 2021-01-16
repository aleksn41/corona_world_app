package de.dhbw.corona_world_app.statistic;

import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticServices {

    private ChartValueSetGenerator chartProvider;

    public Object getStatistic(StatisticCall statisticCall){
        if(this.chartProvider==null){
            chartProvider = new ChartValueSetGenerator();
        }
        switch(statisticCall.getChartType()){
            case BAR: break;
            case PIE: return chartProvider.getPieChart();
            case LINE: return chartProvider.getLineChart();
            default: throw new IllegalArgumentException("This chart type is not accepted!");
        }
        return null;
    }
}
