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
 * @author Aleksandr Stankoski
 */
public class StatisticCallRequestViewModel extends ViewModel {
    MutableLiveData<LinkedHashSet<ISOCountry>> selectedISOCountries;
    MutableLiveData<LinkedHashSet<Criteria>> selectedCriteriaCountries;
    MutableLiveData<LinkedHashSet<ChartType>> selectedChartTypeCountries;
    MutableLiveData<LocalDate> selectedStartDate;
    MutableLiveData<LocalDate> selectedEndDate;

    public StatisticCallRequestViewModel() {
        selectedISOCountries = new MutableLiveData<>();
        selectedISOCountries.setValue(new LinkedHashSet<>());
        selectedCriteriaCountries = new MutableLiveData<>();
        selectedCriteriaCountries.setValue(new LinkedHashSet<>());
        selectedChartTypeCountries = new MutableLiveData<>();
        selectedChartTypeCountries.setValue(new LinkedHashSet<>());
        selectedStartDate = new MutableLiveData<>();
        selectedEndDate = new MutableLiveData<>();
    }
}
