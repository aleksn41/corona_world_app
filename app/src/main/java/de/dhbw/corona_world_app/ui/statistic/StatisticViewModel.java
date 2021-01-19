package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;

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
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.statistic.ChartValueSetGenerator;

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
    public BarChart getBarChart(StatisticCall statisticCall, BarChart chart, Context context) throws ExecutionException, InterruptedException {
        Logger.logV(TAG, "Getting bar chart for " + statisticCall);
        if(dataSetGenerator==null){
            dataSetGenerator = new ChartValueSetGenerator();
        }
        float barSpace = 0.02f;
        float barWidth = 0.45f;
        APIManager.enableCache();
        List<Country> apiGottenList;
        TypedArray colorsTyped = context.getTheme().getResources().obtainTypedArray(R.array.chartColors);
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorsTyped.length(); i++) {
            colors.add(colorsTyped.getColor(i, 0));
        }
        colorsTyped.recycle();
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = !statisticCall.getStartDate().isEqual(statisticCall.getEndDate());
        apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), new LocalDateTime[]{LocalDateTime.now(), LocalDateTime.now()});
        if(countryList2D && criteriaList2D && dates2D) throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");
        if(countryList2D){
            //multiple countries stuff:
            //if coupled with time -> grouped bars
            //if coupled with criteria -> grouped by country
            if(dates2D) {
                //make api call with time constraint
                //BarDataSet data = dataSetGenerator.getBarChartDataSet();
            }

            if(criteriaList2D){
                //multiple criteria stuff:
                //if coupled with time -> overlaying bars
                //if coupled with countries -> grouped by country
            }
        } else {

            if(dates2D) {
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

    public void setPathToCacheDir(File pathToCacheDir){
        this.pathToCacheDir = pathToCacheDir;
    }
}