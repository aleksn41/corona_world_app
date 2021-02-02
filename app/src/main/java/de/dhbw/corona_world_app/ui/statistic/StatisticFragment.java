package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.api.TooManyRequestsException;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.ErrorCode;
import de.dhbw.corona_world_app.ui.tools.ErrorDialog;

import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class StatisticFragment extends Fragment {

    private StatisticCallViewModel statisticCallViewModel;

    private StatisticViewModel statisticViewModel;

    private static final String TAG = StatisticFragment.class.getSimpleName();

    private final ExecutorService service = ThreadPoolHandler.getInstance();

    LinearProgressIndicator progressBar;

    BarChart barChart;

    PieChart pieChart;

    LineChart lineChart;

    TextView testDisplay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticCallViewModel.class);

        if (statisticCallViewModel.isNotInit()) {
            try {
                statisticCallViewModel.init(requireActivity().getFilesDir(), ThreadPoolHandler.getInstance());
            } catch (IOException e) {
                Log.e(this.getClass().getName(), ErrorCode.CANNOT_READ_FILE.toString(), e);
                ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CANNOT_READ_FILE, null);
            }
        }

        // ChartValueSetGenerator provider = new ChartValueSetGenerator();
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        statisticViewModel = new ViewModelProvider(requireActivity()).get(StatisticViewModel.class);
        barChart = root.findViewById(R.id.bar_chart);
        pieChart = root.findViewById(R.id.pie_chart);
        lineChart = root.findViewById(R.id.line_chart);
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        assert bundle != null;
        StatisticCall statisticCall = StatisticFragmentArgs.fromBundle(bundle).getStatisticCall();

        setStyle(barChart, requireContext());
        setStyle(pieChart, statisticCall, requireContext());
        setStyle(lineChart, requireContext());

        service.execute(() -> {
            Thread currentThread = Thread.currentThread();
            AtomicBoolean retry = new AtomicBoolean(true);
            while (retry.get()) {
                if (getContext() != null) {
                    try {
                        switch (statisticCall.getChartType()) {
                            case BAR:
                                barChart.setVisibility(View.INVISIBLE);
                                statisticViewModel.getBarChart(statisticCall, barChart, getContext());
                                if (getActivity() != null) {
                                    requireActivity().runOnUiThread(() -> barChart.setVisibility(View.VISIBLE));
                                }
                                break;
                            case PIE:
                                pieChart.setVisibility(View.INVISIBLE);
                                statisticViewModel.getPieChart(statisticCall, pieChart, getContext());
                                if (getActivity() != null) {
                                    requireActivity().runOnUiThread(() -> pieChart.setVisibility(View.VISIBLE));
                                }
                                break;
                            case LINE:
                                lineChart.setVisibility(View.INVISIBLE);
                                statisticViewModel.getLineChart(statisticCall, lineChart, getContext());
                                if (getActivity() != null) {
                                    requireActivity().runOnUiThread(() -> lineChart.setVisibility(View.VISIBLE));
                                }
                                break;
                            default:
                                throw new IllegalStateException("A not yet implemented chart type was selected!");
                        }
                        retry.set(false);
                    } catch (ExecutionException | InterruptedException | IllegalArgumentException e) {
                        Log.e(TAG, "An error has occurred while creating the statistic!", e);
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CREATE_STATISTIC_FAILED, (dialog, which) -> {
                            retry.set(true);
                            synchronized (currentThread) {
                                currentThread.notify();
                            }
                        }, "Retry"));
                        try {
                            synchronized (currentThread) {
                                currentThread.wait();
                            }
                        } catch (InterruptedException interruptedException) {
                            Log.wtf(TAG, "It was tried to access waiting Thread!", interruptedException);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "An error has occurred while parsing api answer!", e);
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.UNEXPECTED_ANSWER, (dialog, which) -> {
                            retry.set(true);
                            synchronized (currentThread) {
                                currentThread.notify();
                            }
                        }, "Retry"));
                        try {
                            synchronized (currentThread) {
                                currentThread.wait();
                            }
                        } catch (InterruptedException interruptedException) {
                            Log.wtf(TAG, "It was tried to access waiting Thread!", interruptedException);
                        }
                    } catch (TooManyRequestsException e){
                        Log.e(TAG, "Too many requests were made!", e);
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.TOO_MANY_REQUESTS, (dialog, which) -> {
                            retry.set(true);
                            synchronized (currentThread) {
                                currentThread.notify();
                            }
                        }, "Retry"));
                        try {
                            synchronized (currentThread) {
                                currentThread.wait();
                            }
                        } catch (InterruptedException interruptedException) {
                            Log.wtf(TAG, "It was tried to access waiting Thread!", interruptedException);
                        }
                    }
                } else {
                    //this is if the user quickly changes fragment and the error dialog pops up anyway
                    retry.set(false);
                }
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            StatisticCall request = StatisticFragmentArgs.fromBundle(bundle).getStatisticCall();
            boolean isNewRequest = StatisticFragmentArgs.fromBundle(bundle).getIsNewRequest();
            if (isNewRequest) addToHistory(request);
        }
    }

    private void addToHistory(StatisticCall request) {
        statisticCallViewModel.addData(Collections.singletonList(request));
        statisticCallViewModel.saveAllData().whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                if (throwable != null) {
                    Throwable e = throwable.getCause();
                    if (e instanceof IOException) {
                        Log.e(this.getClass().getName(), ErrorCode.CANNOT_SAVE_FILE.toString(), throwable);
                        ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CANNOT_SAVE_FILE, null);
                    } else {
                        Log.wtf(this.getClass().getName(), ErrorCode.UNEXPECTED_ERROR.toString(), throwable);
                        ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.UNEXPECTED_ERROR, null);
                    }
                }
            }
        });
    }

    private void setStyle(LineChart chart, Context context) {
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
        chart.getLegend().setWordWrapEnabled(true);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setGranularity(1f);

        arr2.recycle();
    }

    private void setStyle(PieChart chart, StatisticCall statisticCall,Context context) {
        boolean countryList2D = statisticCall.getCountryList().size() > 1;
        boolean criteriaList2D = statisticCall.getCriteriaList().size() > 1;
        boolean dates2D = statisticCall.getStartDate() != null ? statisticCall.getEndDate() == null || !statisticCall.getStartDate().isEqual(statisticCall.getEndDate()) : statisticCall.getEndDate() != null;
        //this is just to get the background-color...
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.background_color, typedValue, true);
        TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.background_color});
        int backgroundColor = arr.getColor(0, -1);
        chart.setBackgroundColor(backgroundColor);
        arr.recycle();

        Description des = new Description();
        des.setText("");
        chart.setDescription(des);

        //again, just to get the text-color...
        TypedValue typedValue2 = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue2, true);
        TypedArray arr2 = context.obtainStyledAttributes(typedValue2.data, new int[]{android.R.attr.textColorPrimary});
        int textColor = arr2.getColor(0, -1);

        chart.getLegend().setTextColor(textColor);
        chart.setDrawEntryLabels(false);
        //chart.setDrawHoleEnabled(false);
        chart.setHoleColor(backgroundColor);
        chart.setDrawCenterText(true);
        chart.setCenterTextColor(textColor);
        String title = "";
        LocalDate startDate = statisticCall.getStartDate() != null ? statisticCall.getStartDate() : LocalDate.now();
        LocalDate endDate = statisticCall.getEndDate() != null ? statisticCall.getEndDate() : LocalDate.now();
        if(dates2D) title = "Average between the "+StatisticViewModel.getDateFormatted(startDate)+ " and the "+StatisticViewModel.getDateFormatted(endDate);
        chart.setCenterText(title);
        chart.getLegend().setWordWrapEnabled(true);

        arr2.recycle();
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
        chart.getLegend().setWordWrapEnabled(true);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setGranularity(1f);

        arr2.recycle();
    }

    //will be removed once Statistic is finished
    private void testProgressBar() {
        int milliSecondsToLoad = 10;
        ThreadPoolHandler.getInstance().submit((Callable<Void>) () -> {
            testDisplay.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            Thread.sleep(milliSecondsToLoad);
            requireActivity().runOnUiThread(() -> progressBar.setProgress(20));
            Thread.sleep(milliSecondsToLoad);
            requireActivity().runOnUiThread(() -> progressBar.setProgress(40));
            Thread.sleep(milliSecondsToLoad);
            requireActivity().runOnUiThread(() -> progressBar.setProgress(80));
            Thread.sleep(milliSecondsToLoad);
            requireActivity().runOnUiThread(() -> {
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
                testDisplay.setVisibility(View.GONE);
                //barChart.setVisibility(View.VISIBLE);
                //testDisplay.setVisibility(View.VISIBLE);
            });
            return null;
        });
    }
}
