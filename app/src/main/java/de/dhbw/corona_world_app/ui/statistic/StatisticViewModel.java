package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.api.TooManyRequestsException;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.DataException;
import de.dhbw.corona_world_app.datastructure.Displayable;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.TimeFramedCountry;
import de.dhbw.corona_world_app.statistic.ChartValueSetGenerator;
import de.dhbw.corona_world_app.statistic.StatisticCacheObject;

import static java.time.temporal.ChronoUnit.DAYS;

public class StatisticViewModel extends ViewModel {

    private static final String TAG = StatisticViewModel.class.getName();

    private final String CACHE_FILENAME = "/statistics.ser";

    private List<Criteria> criteriaOrder;

    private ChartValueSetGenerator dataSetGenerator;

    private File pathToCacheDir;

    public StatisticViewModel() {

    }

    public void init() {
        if (dataSetGenerator == null) {
            dataSetGenerator = new ChartValueSetGenerator();
        }
        criteriaOrder = new LinkedList<>();
        criteriaOrder.addAll(Arrays.asList(Criteria.POPULATION, Criteria.HEALTHY, Criteria.INFECTED, Criteria.ACTIVE, Criteria.RECOVERED, Criteria.DEATHS, Criteria.IH_RATION, Criteria.ID_RATION));
    }

    public void deleteCache(){
        Log.v(TAG, "Deleting cache...");
        File file = new File(pathToCacheDir + CACHE_FILENAME);

        if(file.delete())
        {
            Log.v(TAG, "Cache was successfully deleted.");
        }
        else
        {
            throw new DataException("Local cache could not be deleted!");
        }
    }

    @SuppressWarnings("unchecked")
    public void cacheStatisticCall(StatisticCall statisticCall, List<TimeFramedCountry> readiedData) throws IOException, ClassNotFoundException {
        Log.v(TAG, "Caching " + statisticCall + "...");
        if (this.pathToCacheDir == null)
            throw new IllegalStateException("Path to cache directory is not set!");

        File cacheFile = new File(pathToCacheDir + CACHE_FILENAME);
        List<StatisticCacheObject> cacheList = null;

        StatisticCacheObject cacheToAdd = new StatisticCacheObject(LocalDateTime.now(), statisticCall, readiedData);

        if (cacheFile.exists() && !cacheFile.isDirectory()) {
            try (FileInputStream fileIn = new FileInputStream(cacheFile)) {
                ObjectInputStream in = new ObjectInputStream(fileIn);
                cacheList = (List<StatisticCacheObject>) in.readObject();
            }
            if (cacheList == null)
                throw new IllegalStateException("Cache file exists but could not be read!");
            cacheList = cacheList.stream().filter(co -> !co.getStatisticCall().equals(cacheToAdd.getStatisticCall())).collect(Collectors.toList());
        }


        if(cacheList == null){
            cacheList = new ArrayList<>();
        } else if (cacheList.size() >= APIManager.MAX_CACHED_STATISTIC_CALLS) {
            cacheList.remove(0);
        }

        cacheList.add(cacheToAdd);

        try (FileOutputStream fileOut = new FileOutputStream(cacheFile)) {
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(cacheList);
            out.close();
            Log.v(TAG, "Successfully cached " + statisticCall);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TimeFramedCountry> getCachedDataIfContains(StatisticCall statisticCall) throws IOException, ClassNotFoundException {
        File cacheFile = new File(pathToCacheDir + CACHE_FILENAME);
        List<StatisticCacheObject> cachedList;

        StatisticCacheObject toReturn = null;

        if (cacheFile.exists() && !cacheFile.isDirectory()) {
            try (FileInputStream fileIn = new FileInputStream(cacheFile)) {
                ObjectInputStream in = new ObjectInputStream(fileIn);
                cachedList = (List<StatisticCacheObject>) in.readObject();
            }
            if (cachedList == null)
                throw new IllegalStateException("Cache file exists but could not be read!");
            cachedList = cachedList.stream().filter(co -> (co.getStatisticCall().getStartDate()!=null && co.getStatisticCall().getEndDate() != null) || co.getCreationTime().isAfter(LocalDateTime.now().minusMinutes(APIManager.MAX_LIVE_STATISTICS_CACHE_AGE))).collect(Collectors.toList());

            for (StatisticCacheObject cacheObject : cachedList) {
                if (cacheObject.getStatisticCall().equals(statisticCall)) {
                    toReturn = cacheObject;
                }
            }
        }

        if(toReturn==null) return null;
        return toReturn.getData();
    }

    public void getBarChart(StatisticCall statisticCall, BarChart chart, Context context) throws ExecutionException, InterruptedException, JSONException, TooManyRequestsException, IOException, ClassNotFoundException {
        Logger.logV(TAG, "Getting bar chart for " + statisticCall);
        init();
        List<TimeFramedCountry> apiGottenList;
        List<Integer> colors = getColors(context);
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = statisticCall.getStartDate() != null ? statisticCall.getEndDate() == null || !statisticCall.getStartDate().isEqual(statisticCall.getEndDate()) : statisticCall.getEndDate() != null;
        if (countryList2D && criteriaList2D && dates2D)
            throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");

        BarData barData = new BarData();
        apiGottenList = getCachedDataIfContains(statisticCall);
        if(apiGottenList == null) {
            apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
            cacheStatisticCall(statisticCall, apiGottenList);
        }
        LocalDate startDate = statisticCall.getStartDate();
        LocalDate endDate = statisticCall.getEndDate();
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();

        int activeavg = 0;
        int recoveredavg = 0;
        int deathsavg = 0;
        for (TimeFramedCountry country : apiGottenList) {
            activeavg += getIntArrayAvg(country.getActive());
            recoveredavg += getIntArrayAvg(country.getRecovered());
            deathsavg += getIntArrayAvg(country.getDeaths());
        }
        if (activeavg > recoveredavg) {
            if (activeavg < deathsavg) {
                criteriaOrder.remove(Criteria.DEATHS);
                criteriaOrder.add(3, Criteria.DEATHS);
            }
        } else {
            if (activeavg < deathsavg) {
                criteriaOrder.remove(Criteria.ACTIVE);
                criteriaOrder.add(5, Criteria.ACTIVE);
            } else {
                criteriaOrder.remove(Criteria.ACTIVE);
                criteriaOrder.add(4, Criteria.ACTIVE);
            }
        }
        if (dates2D) {
            //this sets the steps (in days) and breaks down the data accordingly, so that the user is not showered with too much data
            int dayDifference = (int) DAYS.between(startDate, endDate) + (endDate.equals(LocalDate.now()) ? 0 : 1);
            int step = getStep(dayDifference);

            formatXAxis(apiGottenList, dayDifference, step, chart.getXAxis());
            Collections.sort(apiGottenList);
            for (TimeFramedCountry country : apiGottenList) {
                for (Criteria criteria : criteriaOrder) {
                    if (statisticCall.getCriteriaList().contains(criteria)) {
                        List<Float> data = getDataList(step, dayDifference, country, criteria);
                        barData.addDataSet(dataSetGenerator.getBarChartDataSet(data, country.getCountry().getDisplayName() + ": " + criteria.getDisplayName(), colors));
                    }
                }
            }

        } else {

            List<String> countries = new ArrayList<>();
            for (TimeFramedCountry country : apiGottenList) {
                countries.add(country.getCountry().getISOCode());
            }

            for (Criteria criteria : criteriaOrder) {
                List<Float> countriesData = new ArrayList<>();
                Collections.sort(apiGottenList);
                for (TimeFramedCountry country : apiGottenList) {
                    if (statisticCall.getCriteriaList().contains(criteria)) {
                        List<Float> data = getDataList(1, 1, country, criteria);
                        countriesData.add(data.get(0));

                    }
                }
                barData.addDataSet(dataSetGenerator.getBarChartDataSet(countriesData, criteria.getDisplayName(), colors));
            }
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return countries.get((int) value);
                }
            });
        }

        chart.setData(barData);
    }

    private int getIntArrayAvg(int[] array) {
        int avg = 0;
        for (int value : array) {
            avg += value;
        }
        return avg / array.length;
    }

    private void formatXAxis(List<TimeFramedCountry> apiGottenList, int dayDifference, int step, XAxis xAxis) {
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < dayDifference; i += step) {
            String dateFormatted = getDateFormatted(apiGottenList.get(0).getDates()[i]);
            dates.add(dateFormatted);
        }

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0) {
                    if (dates.size() > (int) value) {
                        return dates.get((int) value);
                    } else return "";
                } else {
                    return "";
                }
            }
        });
    }

    public void getPieChart(StatisticCall statisticCall, PieChart chart, Context context) throws InterruptedException, ExecutionException, JSONException, TooManyRequestsException, IOException, ClassNotFoundException {
        Logger.logV(TAG, "Getting pie chart for " + statisticCall);
        init();
        List<TimeFramedCountry> apiGottenList;
        List<Integer> colors = getColors(context);
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = statisticCall.getStartDate() != null ? statisticCall.getEndDate() == null || !statisticCall.getStartDate().isEqual(statisticCall.getEndDate()) : statisticCall.getEndDate() != null;
        if (countryList2D && criteriaList2D && dates2D)
            throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");

        PieData pieData = new PieData();
        apiGottenList = getCachedDataIfContains(statisticCall);
        if(apiGottenList == null) {
            apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
            cacheStatisticCall(statisticCall, apiGottenList);
        }
        if (dates2D) {
            if (countryList2D) {
                for (Criteria criteria : statisticCall.getCriteriaList()) {
                    List<Float> data = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    for (TimeFramedCountry country : apiGottenList) {
                        if (country.getDates().length > 1 && country.getInfected().length > 1 && country.getDeaths().length > 1 && country.getRecovered().length > 1) {
                            AverageValues averageValues = new AverageValues(country).invoke();

                            addAverageDataToList(country, averageValues, data, criteria);
                            names.add(country.getCountry().getDisplayName() + ": " + criteria.getDisplayName());
                        } else {
                            throw new IllegalStateException("This state should not be reached! There is inconsistency with the size of the arrays in the TimeFramedCountry!");
                        }
                    }
                    pieData.addDataSet(dataSetGenerator.getPieChartDataSet(data, names, "", colors));
                }
            } else {
                for (TimeFramedCountry country : apiGottenList) {
                    if (country.getDates().length > 1 && country.getInfected().length > 1 && country.getDeaths().length > 1 && country.getRecovered().length > 1) {
                        AverageValues averageValues = new AverageValues(country).invoke();

                        List<Float> data = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        for (Criteria criteria : statisticCall.getCriteriaList()) {
                            addAverageDataToList(country, averageValues, data, criteria);
                            names.add(country.getCountry().getDisplayName() + ": " + criteria.getDisplayName());
                        }
                        pieData.addDataSet(dataSetGenerator.getPieChartDataSet(data, names, "", colors));
                    } else {
                        throw new IllegalStateException("This state should not be reached! There is inconsistency with the size of the arrays in the TimeFramedCountry!");
                    }
                }
            }
        } else {
            List<Float> data = new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (Criteria criteria : statisticCall.getCriteriaList()) {
                for (TimeFramedCountry country : apiGottenList) {
                    data.addAll(getDataList(1, 1, country, criteria));
                    names.add(country.getCountry().getDisplayName() + ": " + criteria.getDisplayName());
                }
            }
            pieData.addDataSet(dataSetGenerator.getPieChartDataSet(data, names, "", colors));
        }
        chart.setData(pieData);
    }

    public void getLineChart(StatisticCall statisticCall, LineChart chart, Context context) throws InterruptedException, ExecutionException, JSONException, TooManyRequestsException, IOException, ClassNotFoundException {
        Logger.logV(TAG, "Getting line chart for " + statisticCall);
        init();

        LineData lineData = new LineData();
        List<TimeFramedCountry> apiGottenList;
        List<Integer> colors = getColors(context);
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = statisticCall.getStartDate() != null ? statisticCall.getEndDate() == null || !statisticCall.getStartDate().isEqual(statisticCall.getEndDate()) : statisticCall.getEndDate() != null;
        if (countryList2D && criteriaList2D && dates2D)
            throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");

        apiGottenList = getCachedDataIfContains(statisticCall);
        if(apiGottenList == null) {
            apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
            cacheStatisticCall(statisticCall, apiGottenList);
        }        LocalDate startDate = statisticCall.getStartDate();
        LocalDate endDate = statisticCall.getEndDate();
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();

        if (dates2D) {
            //this sets the steps (in days) and breaks down the data accordingly, so that the user is not showered with too much data
            int dayDifference = (int) DAYS.between(startDate, endDate) + (endDate.equals(LocalDate.now()) ? 0 : 1);
            int step = getStep(dayDifference);

            formatXAxis(apiGottenList, dayDifference, step, chart.getXAxis());

            for (TimeFramedCountry country : apiGottenList) {
                for (Criteria criteria : criteriaOrder) {
                    if (statisticCall.getCriteriaList().contains(criteria)) {
                        List<Float> data = getDataList(step, dayDifference, country, criteria);
                        lineData.addDataSet(dataSetGenerator.getLineChartDataSet(data, country.getCountry().getDisplayName() + ": " + criteria.getDisplayName(), colors));
                    }
                }
            }
        } else {
            List<String> countries = new ArrayList<>();
            for (TimeFramedCountry country : apiGottenList) {
                countries.add(country.getCountry().getISOCode());
            }

            for (Criteria criteria : statisticCall.getCriteriaList()) {
                List<Float> countriesData = new ArrayList<>();
                for (TimeFramedCountry country : apiGottenList) {
                    //for constructing x-axis description
                    List<Float> data = getDataList(1, 1, country, criteria);
                    countriesData.add(data.get(0));
                }
                lineData.addDataSet(dataSetGenerator.getLineChartDataSet(countriesData, criteria.getDisplayName(), colors));
            }
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    if (value >= 0) {
                        if (countries.size() > (int) value) {
                            return countries.get((int) value);
                        } else return "";
                    } else {
                        return "";
                    }
                }
            });
        }


        chart.setData(lineData);
    }

    @NotNull
    private List<Integer> getColors(Context context) {
        TypedArray colorsTyped = context.getTheme().getResources().obtainTypedArray(R.array.chartColors);
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorsTyped.length(); i++) {
            colors.add(colorsTyped.getColor(i, 0));
        }
        colorsTyped.recycle();
        return colors;
    }

    protected static String getDateFormatted(@NotNull LocalDate date) {
        String year = Integer.toString(date.getYear());
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + year.substring(2);
    }

    private void addAverageDataToList(TimeFramedCountry country, AverageValues averageValues, List<Float> data, Criteria criteria) {
        switch (criteria) {
            case HEALTHY:
                data.add((float) country.getPopulation() - averageValues.getAvgInfected());
                break;
            case INFECTED:
                data.add((float) averageValues.getAvgInfected());
                break;
            case DEATHS:
                data.add((float) averageValues.getAvgDeaths());
                break;
            case RECOVERED:
                data.add((float) averageValues.getAvgRecovered());
                break;
            case ID_RATION:
                data.add((float) averageValues.getAvgInfDeathRatio());
                break;
            case IH_RATION:
                data.add((float) averageValues.getAvgPopInfRatio());
                break;
            case POPULATION:
                data.add((float) country.getPopulation());
                break;
            case ACTIVE:
                data.add((float) averageValues.getAvgActive());
                break;
        }
    }

    private int getStep(int dayDifference) {
        int step;
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
        return step;
    }

    private List<Float> getDataList(int step, int stoppingCondition, TimeFramedCountry country, Criteria criteria) {
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
                case ACTIVE:
                    data.add((float) country.getActive()[i]);
                    break;
            }
        }
        return data;
    }

    public void setPathToCacheDir(File pathToCacheDir) {
        this.pathToCacheDir = pathToCacheDir;
    }

    private static class AverageValues {
        private final TimeFramedCountry country;
        private int avgInfected;
        private int avgDeaths;
        private int avgRecovered;
        private int avgActive;
        private double avgPopInfRatio;
        private double avgInfDeathRatio;

        public AverageValues(TimeFramedCountry country) {
            this.country = country;
            this.avgInfected = 0;
            this.avgDeaths = 0;
            this.avgRecovered = 0;
            this.avgPopInfRatio = 0;
            this.avgInfDeathRatio = 0;
            this.avgActive = 0;
        }

        public int getAvgInfected() {
            return avgInfected;
        }

        public int getAvgDeaths() {
            return avgDeaths;
        }

        public int getAvgRecovered() {
            return avgRecovered;
        }

        public int getAvgActive() {
            return avgActive;
        }

        public double getAvgPopInfRatio() {
            return avgPopInfRatio;
        }

        public double getAvgInfDeathRatio() {
            return avgInfDeathRatio;
        }

        public AverageValues invoke() {
            for (int i = 0; i < country.getDates().length; i++) {
                avgPopInfRatio += country.getPop_inf_ratio(i);
            }
            avgPopInfRatio = avgPopInfRatio / country.getDates().length;

            for (int i = 0; i < country.getInfected().length; i++) {
                avgInfected += country.getInfected()[i];
            }
            avgInfected = avgInfected / country.getInfected().length;

            for (int i = 0; i < country.getDeaths().length; i++) {
                avgDeaths += country.getDeaths()[i];
            }
            avgDeaths = avgDeaths / country.getDeaths().length;

            for (int i = 0; i < country.getRecovered().length; i++) {
                avgRecovered += country.getRecovered()[i];
            }
            avgRecovered = avgRecovered / country.getRecovered().length;

            for (int i = 0; i < country.getDates().length; i++) {
                avgInfDeathRatio = (double) country.getDeaths()[i] / country.getInfected()[i];
            }
            avgInfDeathRatio = avgInfDeathRatio / country.getDates().length;

            for (int i = 0; i < country.getActive().length; i++) {
                avgActive += country.getActive()[i];
            }
            avgActive = avgActive / country.getActive().length;
            return this;
        }
    }
}