package de.dhbw.corona_world_app.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import java.util.Arrays;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class StatisticFragment extends Fragment {

    private StatisticViewModel statisticViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticViewModel =
                new ViewModelProvider(this).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);

        ButtonSearchableDialogEnumChooser<ISOCountry> isoCountryButtonSearchableDialogEnumChooser= root.findViewById(R.id.isoCountryChooser);
        isoCountryButtonSearchableDialogEnumChooser.setItems(Arrays.asList(ISOCountry.values()));
        isoCountryButtonSearchableDialogEnumChooser.setLimit(5, limit -> Toast.makeText(getContext(),"Limit of "+limit+" Countries reached",Toast.LENGTH_SHORT).show());

        ButtonSearchableDialogEnumChooser<Criteria> criteriaButtonSearchableDialogEnumChooser= root.findViewById(R.id.criteriaChooser);
        criteriaButtonSearchableDialogEnumChooser.setItems(Arrays.asList(Criteria.values()));

        ButtonSearchableDialogEnumChooser<ChartType> chartTypeButtonSearchableDialogEnumChooser= root.findViewById(R.id.chartTypeChooser);
        chartTypeButtonSearchableDialogEnumChooser.setItems(Arrays.asList(ChartType.values()));
        chartTypeButtonSearchableDialogEnumChooser.setLimit(1, limit -> Toast.makeText(getContext(),"Limit of "+limit+" Chart-Type reached",Toast.LENGTH_SHORT).show());
        return root;
    }
}