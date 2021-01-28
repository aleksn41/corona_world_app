package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.content.res.TypedArray;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.TimeFramedCountry;
import de.dhbw.corona_world_app.statistic.ChartValueSetGenerator;

import static java.time.temporal.ChronoUnit.DAYS;

public class StatisticViewModel extends ViewModel {

    private static final String TAG = StatisticViewModel.class.getName();

    private final Criteria[] criteriaOrder = new Criteria[]{Criteria.POPULATION, Criteria.HEALTHY, Criteria.INFECTED, Criteria.RECOVERED, Criteria.DEATHS, Criteria.IH_RATION, Criteria.ID_RATION};

    private ChartValueSetGenerator dataSetGenerator;

    private File pathToCacheDir;

    public StatisticViewModel() {

    }

    public void init() {
        if (dataSetGenerator == null) {
            dataSetGenerator = new ChartValueSetGenerator();
        }
    }

    public void getBarChart(StatisticCall statisticCall, BarChart chart, Context context) throws ExecutionException, InterruptedException, JSONException {
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
        apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
        LocalDate startDate = statisticCall.getStartDate();
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

    private void formatXAxis(List<TimeFramedCountry> apiGottenList, int dayDifference, int step, XAxis xAxis) {
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < dayDifference; i += step) {
            String dateFormatted = getDateFormatted(apiGottenList.get(0).getDates()[i]);
            dates.add(dateFormatted);
        }

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dates.get((int) value);
            }
        });
    }

    public void getPieChart(StatisticCall statisticCall, PieChart chart, Context context) throws InterruptedException, ExecutionException, JSONException {
        init();
        List<TimeFramedCountry> apiGottenList;
        List<Integer> colors = getColors(context);
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = statisticCall.getStartDate() != null ? statisticCall.getEndDate() == null || !statisticCall.getStartDate().isEqual(statisticCall.getEndDate()) : statisticCall.getEndDate() != null;
        if (countryList2D && criteriaList2D && dates2D)
            throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");

        PieData pieData = new PieData();
        apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
        LocalDate startDate = statisticCall.getStartDate();
        LocalDate endDate = statisticCall.getEndDate();
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();

        if(dates2D){
            if(countryList2D){
                for (Criteria criteria: statisticCall.getCriteriaList()) {
                    List<Float> data = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    for (TimeFramedCountry country: apiGottenList) {
                        AverageValues averageValues = new AverageValues(country).invoke();
                        addDataToList(country, averageValues, data, criteria);
                        names.add(country.getCountry().getDisplayName()+ ": "+criteria.getDisplayName());
                    }
                    pieData.addDataSet(dataSetGenerator.getPieChartDataSet(data, names, "" , colors));
                }
            } else {
                for (TimeFramedCountry country : apiGottenList) {
                    if (country.getDates().length > 1 && country.getInfected().length > 1 && country.getDeaths().length > 1 && country.getRecovered().length > 1) {
                        AverageValues averageValues = new AverageValues(country).invoke();

                        List<Float> data = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        for (Criteria criteria : statisticCall.getCriteriaList()) {
                            addDataToList(country, averageValues, data, criteria);
                            names.add(country.getCountry().getDisplayName() + ": " + criteria.getDisplayName());
                        }
                        pieData.addDataSet(dataSetGenerator.getPieChartDataSet(data, names, "", colors));
                    } else {
                        throw new IllegalStateException("This state should not be reached! There is inconsistency with the size of the arrays in the TimeFramedCountry!");
                    }
                }
            }
        } else {

        }
        chart.setData(pieData);
    }

    private void addDataToList(TimeFramedCountry country, AverageValues averageValues, List<Float> data, Criteria criteria) {
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
        }
    }

    public void getLineChart(StatisticCall statisticCall, LineChart chart, Context context) throws InterruptedException, ExecutionException, JSONException {
        init();

        LineData lineData = new LineData();
        List<TimeFramedCountry> apiGottenList;
        List<Integer> colors = getColors(context);
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = statisticCall.getStartDate() != null ? statisticCall.getEndDate() == null || !statisticCall.getStartDate().isEqual(statisticCall.getEndDate()) : statisticCall.getEndDate() != null;
        if (countryList2D && criteriaList2D && dates2D)
            throw new IllegalArgumentException("Invalid combination of criteria, countries and time. Remember: Only TWO of those can have multiple values.");

        apiGottenList = APIManager.getData(statisticCall.getCountryList(), statisticCall.getCriteriaList(), statisticCall.getStartDate(), statisticCall.getEndDate());
        LocalDate startDate = statisticCall.getStartDate();
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

            for (Criteria criteria : criteriaOrder) {
                List<Float> countriesData = new ArrayList<>();
                for (TimeFramedCountry country : apiGottenList) {
                    //for constructing x-axis description

                    if (statisticCall.getCriteriaList().contains(criteria)) {
                        List<Float> data = getDataList(1, 1, country, criteria);
                        countriesData.add(data.get(0));

                    }
                }
                lineData.addDataSet(dataSetGenerator.getLineChartDataSet(countriesData, criteria.getDisplayName(), colors));
            }
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return countries.get((int) value);
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

    protected static String getDateFormatted(LocalDate date) {
        String year = Integer.toString(date.getYear());
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + year.substring(2);
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
        private double avgPopInfRatio;
        private double avgInfDeathRatio;

        public AverageValues(TimeFramedCountry country) {
            this.country = country;
            this.avgInfected = 0;
            this.avgDeaths = 0;
            this.avgRecovered = 0;
            this.avgPopInfRatio = 0;
            this.avgInfDeathRatio = 0;
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
            return this;
        }
    }
}