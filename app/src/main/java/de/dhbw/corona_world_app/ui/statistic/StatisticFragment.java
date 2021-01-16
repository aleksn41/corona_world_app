package de.dhbw.corona_world_app.ui.statistic;

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
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.statistic.ChartProvider;
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
        ChartProvider provider = new ChartProvider();
        List<BarEntry> entries1 = new ArrayList<>();
        entries1.add(new BarEntry(0f, 20f));
        entries1.add(new BarEntry(1f, 40f));
        entries1.add(new BarEntry(2f, 50f));
        entries1.add(new BarEntry(3f, 70f));
        entries1.add(new BarEntry(4f, 60f));
        entries1.add(new BarEntry(5f, 10f));
        BarDataSet set1 = new BarDataSet(entries1, "Belize: Recovered");
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        entries.add(new BarEntry(4f, 70f));
        entries.add(new BarEntry(5f, 60f));
        BarDataSet set = new BarDataSet(entries, "Belize: Deaths");
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(40f);
        ValueFormatter vf3 = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        };
        set.setValueFormatter(vf3);
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        testDisplay = root.findViewById(R.id.statisticCallItemTextView);
        chart = (BarChart) root.findViewById(R.id.chart);
        chart.setBackgroundColor(Color.WHITE);
        chart.setNoDataTextColor(Color.GREEN);
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
        chart.setData(new BarData(set));
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
