package de.dhbw.corona_world_app.ui.statistic;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.statistic.ChartValueSetGenerator;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class StatisticFragment extends Fragment {

    private StatisticCallViewModel statisticCallViewModel;

    private static final String TAG = StatisticFragment.class.getSimpleName();

    TextRoundCornerProgressBar progressBar;

    BarChart chart;

    TextView testDisplay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticCallViewModel.class);

        //TODO remove this
        if (!statisticCallViewModel.isInit()) {
            try {
                statisticCallViewModel.init(requireActivity().getFilesDir(), ThreadPoolHandler.getInstance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ChartValueSetGenerator provider = new ChartValueSetGenerator();
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        testDisplay = root.findViewById(R.id.statisticCallItemTextView);
        chart = (BarChart) root.findViewById(R.id.chart);
        final String[] dates = new String[] { "01.10.", "02.10.", "03.10.", "04.10.", "05.10.", "06.10." };
        XAxis xaxis = chart.getXAxis();
        ValueFormatter vf = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dates[(int) value];
            }
        };
        xaxis.setValueFormatter(vf);
        YAxis yaxisleft = chart.getAxisLeft();
        YAxis yaxisright = chart.getAxisRight();
        ValueFormatter vf2 = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Integer.toString((int) value);
            }
        };
        yaxisleft.setValueFormatter(vf2);
        yaxisright.setValueFormatter(vf2);
        try {
            testProgressBar();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        Description des = new Description();
        des.setText("");
        chart.setDescription(des);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setTextColor(Color.WHITE);
        List<Float> fl = Arrays.asList(20f, 20f, 30f, 40f, 10f, 80f);
        List<Float> f2 = Arrays.asList(10f, 10f, 25f, 35f, 5f, 67f);
        List<Float> f = Arrays.asList(30f, 80f, 60f, 50f, 70f, 60f);
        TypedArray colorsTyped = getActivity().getTheme().getResources().obtainTypedArray(R.array.chartColors);
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorsTyped.length(); i++) {
            colors.add(colorsTyped.getColor(i, 0));
        }
        colorsTyped.recycle();
        chart.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        chart.setDoubleTapToZoomEnabled(false);
        chart.setData(new BarData(provider.getBarChartDataSet(f, "Belize: Deaths", colors), provider.getBarChartDataSet(fl, "Belize: Recovered", colors), provider.getBarChartDataSet(f2, "Belize: Infected", colors)));
        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            StatisticCall request = StatisticFragmentArgs.fromBundle(bundle).getStatisticCall();
            boolean isNewRequest = StatisticFragmentArgs.fromBundle(bundle).getIsNewRequest();
            testDisplay.setText(request.toString());
            if (isNewRequest) addToHistory(request);
        }
    }

    private void addToHistory(StatisticCall request) {
        try {
            statisticCallViewModel.addData(Collections.singletonList(request));
            statisticCallViewModel.saveAllData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //will be removed once Statistic is finished
    private void testProgressBar() throws ExecutionException, InterruptedException {
        int milliSecondsToLoad = 10;
        ThreadPoolHandler.getInstance().submit(new Callable<Void>() {
            @Override
            public Void call() throws InterruptedException {
                chart.setVisibility(View.GONE);
                testDisplay.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgressText("loading Country Information");
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(20);
                        progressBar.setProgressText("parsing Information");
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(40);
                        progressBar.setProgressText("analyzing Information");
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(80);
                        progressBar.setProgressText("finalizing Statistic");
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(100);
                        progressBar.setProgressText("finished");
                        progressBar.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        progressBar.setProgressText("");
                        chart.setVisibility(View.VISIBLE);
                        //testDisplay.setVisibility(View.VISIBLE);
                    }
                });
                return null;
            }
        });
    }
}
