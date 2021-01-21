package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.TimeframedCountry;
import de.dhbw.corona_world_app.statistic.ChartValueSetGenerator;

import static java.time.temporal.ChronoUnit.DAYS;

public class StatisticViewModel extends ViewModel {

    private static final String TAG = StatisticViewModel.class.getName();

    private ChartValueSetGenerator dataSetGenerator;

    private MutableLiveData<String> mText;

    private File pathToCacheDir;

    public StatisticViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Statistics");
    }

    //TODO once api-branch is merged this can be implemented properly
    public BarChart getBarChart(StatisticCall statisticCall, BarChart chart, Context context) throws ExecutionException, InterruptedException, JSONException {
        Logger.logV(TAG, "Getting bar chart for " + statisticCall);
        if(dataSetGenerator==null){
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
        if(countryList2D && criteriaList2D && dates2D) throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");
        if(dates2D){

            int step = 0;
            int dayDifference = (int) DAYS.between(statisticCall.getStartDate(), statisticCall.getEndDate());
            if(dayDifference <= 7){
                step = 1;
            } else if(dayDifference <= 21){
                step = 2;
            } else if(dayDifference <= 90){
                step = 7;
            } else if(dayDifference <= 360){
                step = 30;
            } else if(dayDifference <= 720){
                step = 60;
            } else {
                step = 360;
            }

            //multiple countries stuff:
            //if coupled with time -> grouped bars
            //if coupled with criteria -> grouped by country
            if(countryList2D) {
                //make api call with time constraint
                //BarDataSet data = dataSetGenerator.getBarChartDataSet();
            }

            if(criteriaList2D){
                BarData barData = new BarData();
                apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
                List<String> dates = new ArrayList<>();
                for(int i = 0; i < dayDifference; i += step){
                    dates.add(apiGottenList.get(0).getDates()[i].toString());
                }

                chart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return dates.get((int) value);
                    }
                });

                for (TimeframedCountry country: apiGottenList) {
                    for (Criteria criteria:statisticCall.getCriteriaList()) {
                        List<Float> data = getDataList(step, dayDifference, country, criteria);
                        barData.addDataSet(dataSetGenerator.getBarChartDataSet(data, country.getCountry().getDisplayName()+": "+criteria.getDisplayName(), colors));
                    }
                }

                chart.setData(barData);
                setStyle(chart, context);
                return chart;
                //multiple criteria stuff:
                //if coupled with time -> overlaying bars
                //if coupled with countries -> grouped by country
            }
        } else {

            if(countryList2D) {
                //is alone or with criteria
            }

            if(criteriaList2D){
                //is alone...
            }


        }

        //time stuff:
        //if more than 7 days -> avg of 2 days
        //if more than 3 weeks -> avg of weeks
        //if bigger than 3 months -> avg of months
        //optional: if bigger than 12 months -> avg of 2 months
        //optional: if bigger than 2 years -> avg per year
        return null;
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

        arr2.recycle();
    }

    private List<Float> getDataList(int step, int stoppingCondition, TimeframedCountry country, Criteria criteria) {
        List<Float> data = new ArrayList<>();
        for (int i = 0; i < stoppingCondition; i += step){
            switch (criteria){
                case HEALTHY: data.add((float) country.getPopulation()-country.getInfected()[i]); break;
                case INFECTED: data.add((float) country.getInfected()[i]); break;
                case DEATHS: data.add((float) country.getDeaths()[i]);
                case RECOVERED: data.add((float) country.getRecovered()[i]);
                case ID_RATION: data.add((float) country.getInfected()[i]/country.getDeaths()[i]);
                case IH_RATION: data.add((float) country.getPop_inf_ratio(i));
                case POPULATION: data.add((float) country.getPopulation());
            }
        }
        return data;
    }

    public void setPathToCacheDir(File pathToCacheDir){
        this.pathToCacheDir = pathToCacheDir;
    }
}