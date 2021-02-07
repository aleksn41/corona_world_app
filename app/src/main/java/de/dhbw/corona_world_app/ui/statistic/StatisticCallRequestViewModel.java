package de.dhbw.corona_world_app.ui.statistic;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.material.chip.ChipGroup;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;
import de.dhbw.corona_world_app.ui.tools.Pair;
import de.dhbw.corona_world_app.ui.tools.StatisticCallDataManager;

/**
 * Used to Save the data in {@link StatisticRequestFragment} in order to allow rotation of the App
 * @author Aleksandr Stankoski
 */
public class StatisticCallRequestViewModel extends ViewModel {
    MutableLiveData<LinkedHashSet<ISOCountry>> selectedISOCountries;
    MutableLiveData<LinkedHashSet<Criteria>> selectedCriteriaCountries;
    MutableLiveData<LinkedHashSet<ChartType>> selectedChartTypeCountries;
    MutableLiveData<LocalDate> startDate;
    MutableLiveData<LocalDate> endDate;

    public StatisticCallRequestViewModel() {
        selectedISOCountries = new MutableLiveData<>();
        selectedISOCountries.setValue(new LinkedHashSet<>());
        selectedCriteriaCountries = new MutableLiveData<>();
        selectedCriteriaCountries.setValue(new LinkedHashSet<>());
        selectedChartTypeCountries = new MutableLiveData<>();
        selectedChartTypeCountries.setValue(new LinkedHashSet<>());
        startDate = new MutableLiveData<>();
        endDate = new MutableLiveData<>();
    }
}
