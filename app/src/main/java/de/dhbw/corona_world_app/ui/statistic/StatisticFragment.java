package de.dhbw.corona_world_app.ui.statistic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.tools.ErrorCode;
import de.dhbw.corona_world_app.ui.tools.ErrorDialog;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class StatisticFragment extends Fragment {

    private StatisticCallViewModel statisticCallViewModel;

    private static final String TAG = StatisticFragment.class.getSimpleName();

    LinearProgressIndicator progressBar;

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

        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        testDisplay = root.findViewById(R.id.statisticCallItemTextView);
        testProgressBar();
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

    //will be removed once Statistic is finished
    private void testProgressBar() {
        int milliSecondsToLoad = 3000;
        ThreadPoolHandler.getInstance().submit(new Callable<Void>() {
            @Override
            public Void call() throws InterruptedException {
                testDisplay.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(20);
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(40);
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(80);
                    }
                });
                Thread.sleep(milliSecondsToLoad);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(100);
                        progressBar.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        testDisplay.setVisibility(View.VISIBLE);
                    }
                });
                return null;
            }
        });
    }
}
