package de.dhbw.corona_world_app.ui.statistic;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

/**
 * Used to Save the data in {@link StatisticRequestFragment} in order to allow rotation of the App
 *
 * @author Aleksandr Stankoski
 */
public class StatisticCallRequestViewModel extends ViewModel {
    final MutableLiveData<LinkedHashSet<ISOCountry>> selectedISOCountries;
    final MutableLiveData<LinkedHashSet<Criteria>> selectedCriteriaCountries;
    final MutableLiveData<LinkedHashSet<ChartType>> selectedChartTypeCountries;
    final MutableLiveData<LocalDate> selectedStartDate;
    final MutableLiveData<LocalDate> selectedEndDate;
    final MutableLiveData<Boolean> ruleAppliesForDatePicker;

    public StatisticCallRequestViewModel() {
        selectedISOCountries = new MutableLiveData<>();
        selectedISOCountries.setValue(new LinkedHashSet<>());
        selectedCriteriaCountries = new MutableLiveData<>();
        selectedCriteriaCountries.setValue(new LinkedHashSet<>());
        selectedChartTypeCountries = new MutableLiveData<>();
        selectedChartTypeCountries.setValue(new LinkedHashSet<>());
        selectedStartDate = new MutableLiveData<>();
        selectedEndDate = new MutableLiveData<>();
        ruleAppliesForDatePicker = new MutableLiveData<>();
        ruleAppliesForDatePicker.setValue(false);
    }
}
