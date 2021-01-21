package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.TimeframedCountry;
import de.dhbw.corona_world_app.statistic.ChartValueSetGenerator;

import static java.time.temporal.ChronoUnit.DAYS;

public class StatisticViewModel extends ViewModel {

    private static final String TAG = StatisticViewModel.class.getName();

    private final Criteria[] criteriaOrder = new Criteria[]{Criteria.POPULATION, Criteria.HEALTHY, Criteria.INFECTED, Criteria.RECOVERED, Criteria.DEATHS, Criteria.IH_RATION, Criteria.ID_RATION};

    private ChartValueSetGenerator dataSetGenerator;

    private File pathToCacheDir;

    public StatisticViewModel() {

    }

    //TODO once api-branch is merged this can be implemented properly
    public BarChart getBarChart(StatisticCall statisticCall, BarChart chart, Context context) throws ExecutionException, InterruptedException, JSONException {
        Logger.logV(TAG, "Getting bar chart for " + statisticCall);
        if (dataSetGenerator == null) {
            dataSetGenerator = new ChartValueSetGenerator();
        }
        float barSpace = 0.02f;
        float barWidth = 0.45f;
        APIManager.enableCache();
        List<TimeframedCountry> apiGottenList;
        TypedArray colorsTyped = context.getTheme().getResources().obtainTypedArray(R.array.chartColors);
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorsTyped.length(); i++) {
            colors.add(colorsTyped.getColor(i, 0));
        }
        colorsTyped.recycle();
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = !statisticCall.getStartDate().isEqual(statisticCall.getEndDate());
        if (countryList2D && criteriaList2D && dates2D)
            throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");

        if (dates2D) {
            //this sets the steps (in days) and breaks down the data accordingly, so that the user is not showered with too much data
            int step = 0;
            int dayDifference = (int) DAYS.between(statisticCall.getStartDate(), statisticCall.getEndDate()) + 1;
            if (dayDifference <= 7) {
                step = 1;
            } else if (dayDifference <= 21) {
                step = 2;
            } else if (dayDifference <= 90) {
                step = 7;
            } else if (dayDifference <= 360) {
                step = 30;
            } else if (dayDifference <= 720) {
                step = 60;
            } else {
                step = 360;
            }

            BarData barData = new BarData();
            apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
            List<String> dates = new ArrayList<>();
            for (int i = 0; i < dayDifference; i += step) {
                String dateFormatted = getDateFormatted(apiGottenList.get(0).getDates()[i]);
                dates.add(dateFormatted);
            }

            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return dates.get((int) value);
                }
            });

            for (TimeframedCountry country : apiGottenList) {
                for (Criteria criteria : criteriaOrder) {
                    if(statisticCall.getCriteriaList().contains(criteria)) {
                        List<Float> data = getDataList(step, dayDifference, country, criteria);
                        barData.addDataSet(dataSetGenerator.getBarChartDataSet(data, country.getCountry().getDisplayName() + ": " + criteria.getDisplayName(), colors));
                    }
                }
            }

            chart.setData(barData);
            setStyle(chart, context);
            return chart;
        } else {

            if (countryList2D) {
                //is alone or with criteria
            }

            if (criteriaList2D) {
                //is alone...
            }


        }
        return null;
    }

    private String getDateFormatted(LocalDate date) {
        String year = Integer.toString(date.getYear());
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + year.substring(2);
    }

    private void setStyle(BarChart chart, Context context) {
        //this is just to get the background-color...
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.background_color, typedValue, true);
        TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.background_color});
        chart.setBackgroundColor(arr.getColor(0, -1));
        arr.recycle();

        chart.setDoubleTapToZoomEnabled(false);
        Description des = new Description();
        des.setText("");
        chart.setDescription(des);

        //again, just to get the text-color...
        TypedValue typedValue2 = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue2, true);
        TypedArray arr2 = context.obtainStyledAttributes(typedValue2.data, new int[]{android.R.attr.textColorPrimary});
        int textColor = arr2.getColor(0, -1);

        chart.getLegend().setTextColor(textColor);
        chart.getXAxis().setTextColor(textColor);
        chart.getAxisLeft().setTextColor(textColor);
        chart.getAxisRight().setTextColor(textColor);
        //chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setAxisMinimum(0f);

        arr2.recycle();
    }

    private List<Float> getDataList(int step, int stoppingCondition, TimeframedCountry country, Criteria criteria) {
        List<Float> data = new ArrayList<>();
        for (int i = 0; i < stoppingCondition; i += step) {
            switch (criteria) {
                case HEALTHY:
                    data.add((float) country.getPopulation() - country.getInfected()[i]);
                    break;
                case INFECTED:
                    data.add((float) country.getInfected()[i]);
                    break;
                case DEATHS:
                    data.add((float) country.getDeaths()[i]);
                    break;
                case RECOVERED:
                    data.add((float) country.getRecovered()[i]);
                    break;
                case ID_RATION:
                    data.add((float) country.getInfected()[i] / country.getDeaths()[i]);
                    break;
                case IH_RATION:
                    data.add((float) country.getPop_inf_ratio(i));
                    break;
                case POPULATION:
                    data.add((float) country.getPopulation());
                    break;
            }
        }
        return data;
    }

    public void setPathToCacheDir(File pathToCacheDir) {
        this.pathToCacheDir = pathToCacheDir;
    }
}