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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.ui.history.HistoryFragment;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;

public class StatisticFragment extends Fragment {

    private static final String TAG = StatisticFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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

    //TODO change when only one Instance of manager exists
    private void addToHistory(StatisticCall request){
        try {
            StatisticCallDataManager manager= new StatisticCallDataManager(ThreadPoolHandler.getInstance(), requireActivity().getFilesDir());
            manager.addData(Collections.singletonList(request));
        } catch (IOException e) {
            Log.e(TAG,"could not load history File",e);
            e.printStackTrace();
        }
    }
}
