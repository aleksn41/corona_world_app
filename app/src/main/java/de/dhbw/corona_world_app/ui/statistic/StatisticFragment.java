package de.dhbw.corona_world_app.ui.statistic;

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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class StatisticFragment extends Fragment {

    private StatisticCallViewModel statisticCallViewModel;

    private static final String TAG = StatisticFragment.class.getSimpleName();

    TextRoundCornerProgressBar progressBar;

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


        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        testDisplay = root.findViewById(R.id.statisticCallItemTextView);
        try {
            testProgressBar();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        int milliSecondsToLoad = 3000;
        ThreadPoolHandler.getInstance().submit(new Callable<Void>() {
            @Override
            public Void call() throws InterruptedException {
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
                        testDisplay.setVisibility(View.VISIBLE);
                    }
                });
                return null;
            }
        });
    }
}
