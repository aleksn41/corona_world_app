package de.dhbw.corona_world_app.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

public class StatisticFragment extends Fragment {

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
        }
    }
}
