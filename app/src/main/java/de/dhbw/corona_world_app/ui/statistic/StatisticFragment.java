package de.dhbw.corona_world_app.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        EnumSpinnerAdapter<ISOCountry> isoCountryMultiSpinner= root.findViewById(R.id.isoCountrySpinner);
        EnumSpinnerAdapter<Criteria> criteriaMultiSpinner= root.findViewById(R.id.criteriaSpinner);
        EnumSpinnerAdapter<ChartType> chartTypeMultiSpinner= root.findViewById(R.id.chartTypeSpinner);
        // Pass true If you want searchView above the list. Otherwise false. default = true.

        // A text that will display in search hint.
        isoCountryMultiSpinner.setSearchHint("Select your mood");

        // Set text that will display when search result not found...
        isoCountryMultiSpinner.setHintText("xd");

        // If you will set the limit, this button will not display automatically.
        isoCountryMultiSpinner.setShowSelectAllButton(true);

        //A text that will display in clear text button
        isoCountryMultiSpinner.setClearText("Close & Clear");

        // Removed second parameter, position. Its not required now..
        // If you want to pass preselected items, you can do it while making listArray,
        // pass true in setSelected of any item that you want to preselect
        isoCountryMultiSpinner.setItems(Arrays.asList(ISOCountry.values()));

        isoCountryMultiSpinner.setLimit(5, (EnumSpinnerAdapter.LimitExceedListener<ISOCountry>) data -> Toast.makeText(getContext(),                "Limit exceed ", Toast.LENGTH_LONG).show());
        return root;
    }
}