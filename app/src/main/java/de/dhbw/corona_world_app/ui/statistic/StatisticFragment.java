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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.history.HistoryFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewModel;

public class StatisticFragment extends Fragment {

    private StatisticCallViewModel statisticCallViewModel;

    private static final String TAG = StatisticFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticCallViewModel.class);

        //TODO remove this
        if(!statisticCallViewModel.isInit()) {
            try {
                statisticCallViewModel.init(requireActivity().getFilesDir(),ThreadPoolHandler.getInstance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        View root = inflater.inflate(R.layout.fragment_statistic, container, false);
        return root;
    }
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        if(bundle!=null){
            TextView testDisplay=view.findViewById(R.id.statisticCallItemTextView);
            StatisticCall request=StatisticFragmentArgs.fromBundle(bundle).getStatisticCall();
            testDisplay.setText(request.toString());
            addToHistory(request);
        }
    }

    //TODO fix: if user never enters history/fav this entry will not be saved permanently
    private void addToHistory(StatisticCall request){
        try {
            statisticCallViewModel.addData(Collections.singletonList(request));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
