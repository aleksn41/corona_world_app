package de.dhbw.corona_world_app.ui.statistic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Objects;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

/**
 * This Fragment is used to choose and create a Statistic
 *
 * @author Aleksandr Stankoski
 */
public class StatisticRequestFragment extends Fragment {

    StatisticCallRequestViewModel statisticCallRequestViewModel;

    DatePickerDialog startDatePicker;
    DatePickerDialog endDatePicker;

    StatisticRequestRule rule;

    StatisticRequestRule.OnItemsChangeListener startDateChange;
    StatisticRequestRule.OnItemsChangeListener endDateChange;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticCallRequestViewModel = new ViewModelProvider(this).get(StatisticCallRequestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic_request, container, false);
        Log.v(this.getClass().getName(), "creating View");
        ExtendedFloatingActionButton floatingActionButton = root.findViewById(R.id.floating_action_button);

        //change floating button based on position in Scrollview
        ScrollView scrollView = root.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            boolean atEndOfView = scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollY) == 0;
            if (atEndOfView) {
                floatingActionButton.extend();
                Log.v(this.getClass().getName(), "reached bottom of Scrollview, extending Button");
            } else {
                floatingActionButton.shrink();
                Log.v(this.getClass().getName(), "not at bottom of Scrollview, shrinking Button");
            }
        });

        //init floatingActionButtonPosition once it is known if the scrollview is at the end when initialized
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                boolean atEndOfView = scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) == 0;
                if (atEndOfView) {
                    floatingActionButton.extend();
                } else floatingActionButton.shrink();
                Log.v(this.getClass().getName(), "finished setup floating Button");
            }
        });

        CustomAutoCompleteTextView isoCountryTextView = root.findViewById(R.id.isoCountryTextView);
        ChipGroup isoCountryChips = root.findViewById(R.id.isoCountryChips);
        AutoCompleteTextViewAdapter<ISOCountry> isoCountryAdapter = new AutoCompleteTextViewAdapter<>(getContext(), ISOCountry.class, APIManager.MAX_COUNTRY_LIST_SIZE, getLimitListener("Country", isoCountryTextView));

        //special rules, user cannot select these for a statistic as they are not supported by the API
        isoCountryAdapter.addToBlackList(ISOCountry.World);
        isoCountryAdapter.addToBlackList(ISOCountry.Aland_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.American_Samoa);
        isoCountryAdapter.addToBlackList(ISOCountry.Antarctica);
        isoCountryAdapter.addToBlackList(ISOCountry.Anguilla);
        isoCountryAdapter.addToBlackList(ISOCountry.Aruba);
        isoCountryAdapter.addToBlackList(ISOCountry.Bouvet_Island);
        isoCountryAdapter.addToBlackList(ISOCountry.British_Indian_Ocean_Territory);
        isoCountryAdapter.addToBlackList(ISOCountry.Christmas_Island);
        isoCountryAdapter.addToBlackList(ISOCountry.Cocos);
        isoCountryAdapter.addToBlackList(ISOCountry.Cook_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.French_Southern_Territories);
        isoCountryAdapter.addToBlackList(ISOCountry.Guam);
        isoCountryAdapter.addToBlackList(ISOCountry.Guernsey);
        isoCountryAdapter.addToBlackList(ISOCountry.Heard_Island_and_McDonald_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.Holy_See);
        isoCountryAdapter.addToBlackList(ISOCountry.Jersey);
        isoCountryAdapter.addToBlackList(ISOCountry.Kiribati);
        isoCountryAdapter.addToBlackList(ISOCountry.North_Korea);
        isoCountryAdapter.addToBlackList(ISOCountry.Nauru);
        isoCountryAdapter.addToBlackList(ISOCountry.Niue);
        isoCountryAdapter.addToBlackList(ISOCountry.Norfolk_Island);
        isoCountryAdapter.addToBlackList(ISOCountry.Northern_Mariana_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.Palau);
        isoCountryAdapter.addToBlackList(ISOCountry.Pitcairn);
        isoCountryAdapter.addToBlackList(ISOCountry.Puerto_Rico);
        isoCountryAdapter.addToBlackList(ISOCountry.Saint_Helena_Ascension_and_Tristan_da_Cunha);
        isoCountryAdapter.addToBlackList(ISOCountry.South_Georgia_and_the_South_Sandwich_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.Svalbard_and_Jan_Mayen);
        isoCountryAdapter.addToBlackList(ISOCountry.Tokelau);
        isoCountryAdapter.addToBlackList(ISOCountry.Tonga);
        isoCountryAdapter.addToBlackList(ISOCountry.Turkmenistan);
        isoCountryAdapter.addToBlackList(ISOCountry.Tuvalu);
        isoCountryAdapter.addToBlackList(ISOCountry.United_States_Minor_Outlying_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.US_Virgin_Islands);
        isoCountryAdapter.addToBlackList(ISOCountry.Republic_of_Kosovo);

        //When an item is selected, submit to ViewModel and change Chips
        isoCountryTextView.setOnItemClickListener((parent, view, position, id) -> {
            statisticCallRequestViewModel.selectedISOCountries.getValue().add(isoCountryAdapter.getItem(position));
            statisticCallRequestViewModel.selectedISOCountries.setValue(statisticCallRequestViewModel.selectedISOCountries.getValue());
        });
        statisticCallRequestViewModel.selectedISOCountries.observe(getViewLifecycleOwner(), isoCountries -> {
            Log.d(this.getClass().getName(), "selected IsoCountries has been updated");
            isoCountryAdapter.submitSelectedItems(isoCountries);
            isoCountryTextView.setText("");
            isoCountryChips.removeAllViews();
            for (ISOCountry isoCountry : isoCountries) {
                Chip itemChip = getChip(isoCountry);
                itemChip.setOnCloseIconClickListener(v -> {
                    statisticCallRequestViewModel.selectedISOCountries.getValue().remove(isoCountry);
                    statisticCallRequestViewModel.selectedISOCountries.setValue(statisticCallRequestViewModel.selectedISOCountries.getValue());
                });
                isoCountryChips.addView(itemChip);
            }
        });
        setupMultiAutoCompleteTextView(isoCountryTextView, isoCountryAdapter);

        CustomAutoCompleteTextView criteriaNachoTextView = root.findViewById(R.id.nachoCriteriaTextView);
        ChipGroup criteriaChips = root.findViewById(R.id.criteriaChips);
        AutoCompleteTextViewAdapter<Criteria> criteriaAdapter = new AutoCompleteTextViewAdapter<>(getContext(), Criteria.class, AutoCompleteTextViewAdapter.NO_LIMIT, null);

        criteriaNachoTextView.setOnItemClickListener((parent, view, position, id) -> {
            statisticCallRequestViewModel.selectedCriteriaCountries.getValue().add(criteriaAdapter.getItem(position));
            statisticCallRequestViewModel.selectedCriteriaCountries.setValue(statisticCallRequestViewModel.selectedCriteriaCountries.getValue());
        });
        statisticCallRequestViewModel.selectedCriteriaCountries.observe(getViewLifecycleOwner(), criteriaItems -> {
            Log.d(this.getClass().getName(), "selected Criteria has been updated");
            criteriaAdapter.submitSelectedItems(criteriaItems);
            criteriaNachoTextView.setText("");
            criteriaChips.removeAllViews();
            for (Criteria criteria : criteriaItems) {
                Chip itemChip = getChip(criteria);
                itemChip.setOnCloseIconClickListener(v -> {
                    statisticCallRequestViewModel.selectedCriteriaCountries.getValue().remove(criteria);
                    statisticCallRequestViewModel.selectedCriteriaCountries.setValue(statisticCallRequestViewModel.selectedCriteriaCountries.getValue());
                });
                criteriaChips.addView(itemChip);
            }
        });
        setupMultiAutoCompleteTextView(criteriaNachoTextView, criteriaAdapter);

        CustomAutoCompleteTextView chartTypeNachoTextView = root.findViewById(R.id.nachoChartTypeTextView);
        ChipGroup chartTypeChips = root.findViewById(R.id.chartTypeChips);
        AutoCompleteTextViewAdapter<ChartType> chartTypeAdapter = new AutoCompleteTextViewAdapter<ChartType>(getContext(), ChartType.class, 1, getLimitListener("Chart-Type", chartTypeNachoTextView)) {
            //special case where if condition applies, a bar chart cannot be shown
            @Override
            public void conditionApplies(boolean allowOnlyOneItem) {
                if (allowOnlyOneItem) addToBlackList(ChartType.BAR);
                super.conditionApplies(allowOnlyOneItem);
            }
        };

        chartTypeNachoTextView.setOnItemClickListener((parent, view, position, id) -> {
            statisticCallRequestViewModel.selectedChartTypeCountries.getValue().add(chartTypeAdapter.getItem(position));
            statisticCallRequestViewModel.selectedChartTypeCountries.setValue(statisticCallRequestViewModel.selectedChartTypeCountries.getValue());
        });
        statisticCallRequestViewModel.selectedChartTypeCountries.observe(getViewLifecycleOwner(), chartTypeItems -> {
            Log.d(this.getClass().getName(), "selected ChartType has been updated");
            chartTypeAdapter.submitSelectedItems(chartTypeItems);
            chartTypeNachoTextView.setText("");
            chartTypeChips.removeAllViews();
            for (ChartType chartType : chartTypeItems) {
                Chip itemChip = getChip(chartType);
                itemChip.setOnCloseIconClickListener(v -> {
                    statisticCallRequestViewModel.selectedChartTypeCountries.getValue().remove(chartType);
                    statisticCallRequestViewModel.selectedChartTypeCountries.setValue(statisticCallRequestViewModel.selectedChartTypeCountries.getValue());
                });
                chartTypeChips.addView(itemChip);
            }
        });
        setupMultiAutoCompleteTextView(chartTypeNachoTextView, chartTypeAdapter);

        //get current Date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Button startDateChooser = root.findViewById(R.id.startDateChooser);
        Button endDateChooser = root.findViewById(R.id.endDateChooser);

        startDatePicker = new DatePickerDialog(getContext(), R.style.SpinnerDatePickerStyle, null, year, month, day);
        statisticCallRequestViewModel.selectedStartDate.observe(getViewLifecycleOwner(), start -> {
            Log.d(this.getClass().getName(), "start Date changed");
            startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
            endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
            if (!endDateChooser.isEnabled()) endDateChooser.setEnabled(true);
            if (startDateChange != null) startDateChange.onItemChange();
        });
        endDatePicker = new DatePickerDialog(getContext(), R.style.SpinnerDatePickerStyle, null, year, month, day);
        statisticCallRequestViewModel.selectedEndDate.observe(getViewLifecycleOwner(), end -> {
            Log.d(this.getClass().getName(), "end Date changed");
            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
            startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
            if (endDateChange != null) endDateChange.onItemChange();
        });

        startDatePicker.setOnDateSetListener((view, year1, month1, dayOfMonth) -> statisticCallRequestViewModel.selectedStartDate.setValue(LocalDate.of(year1, month1 + 1, dayOfMonth)));

        endDatePicker.setOnDateSetListener((view, year12, month12, dayOfMonth) -> statisticCallRequestViewModel.selectedEndDate.setValue(LocalDate.of(year12, month12 + 1, dayOfMonth)));

        //sets the min date to the beginning of Corona
        startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));

        //sets the max date to today
        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
        endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));

        //Default selection is now
        startDateChooser.setText(getString(R.string.now));
        endDateChooser.setText(getString(R.string.now));

        //not allowed to change end Date until start date is changed
        endDateChooser.setEnabled(false);

        startDateChooser.setOnClickListener(v -> startDatePicker.show());
        endDateChooser.setOnClickListener(v -> endDatePicker.show());

        //setup RuleWatcher
        rule = new StatisticRequestRule(isoCountryAdapter, criteriaAdapter, chartTypeAdapter, new StatisticRequestRule.RuleDateRangeInterface() {
            boolean changed = false;

            @Override
            public LocalDate getStartDate() {
                return statisticCallRequestViewModel.selectedStartDate.getValue();
            }

            @Override
            public LocalDate getEndDate() {
                return statisticCallRequestViewModel.selectedEndDate.getValue();
            }

            @Override
            public void setOnStartDateChangeListener(StatisticRequestRule.OnItemsChangeListener listener) {
                startDateChange = listener;
            }

            @Override
            public void setOnEndDateChangeListener(StatisticRequestRule.OnItemsChangeListener listener) {
                endDateChange = listener;
            }

            //the EndDate picker is forced to be equal to the startDatePicker if the condition applies
            @Override
            public void conditionApplies(boolean startAndEndDateMustBeSame) {
                if (startAndEndDateMustBeSame && !changed) {
                    //end and start date must not be same
                    if (!Objects.equals(statisticCallRequestViewModel.selectedStartDate.getValue(), statisticCallRequestViewModel.selectedEndDate.getValue())) {
                        throw new IllegalStateException("Unexpected State, start and end are different but must be same");
                    }
                    statisticCallRequestViewModel.selectedEndDate.removeObservers(getViewLifecycleOwner());
                    endDateChooser.setEnabled(false);
                    startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
                    startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
                    statisticCallRequestViewModel.selectedStartDate.removeObservers(getViewLifecycleOwner());
                    statisticCallRequestViewModel.selectedStartDate.observe(getViewLifecycleOwner(), start -> {
                        Log.d(this.getClass().getName(), "start and end Date changed");
                        startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                        endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(start));
                        endDatePicker.getDatePicker().updateDate(start.getYear(), start.getMonthValue(), start.getDayOfMonth());
                        statisticCallRequestViewModel.selectedEndDate.setValue(start);
                        endDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                        if (startDateChange != null) startDateChange.onItemChange();
                        if (endDateChange != null) endDateChange.onItemChange();
                    });
                    changed = true;
                }
            }

            //reset to old condition before condition did apply
            @Override
            public void conditionDoesNotApply() {
                if (changed) {
                    statisticCallRequestViewModel.selectedStartDate.removeObservers(getViewLifecycleOwner());
                    statisticCallRequestViewModel.selectedStartDate.observe(getViewLifecycleOwner(), start -> {
                        Log.d(this.getClass().getName(), "start Date changed");
                        startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                        if (startDateChange != null) startDateChange.onItemChange();
                    });
                    statisticCallRequestViewModel.selectedEndDate.removeObservers(getViewLifecycleOwner());
                    statisticCallRequestViewModel.selectedEndDate.observe(getViewLifecycleOwner(), end -> {
                        Log.d(this.getClass().getName(), "end Date changed");
                        endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
                        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
                        if (endDateChange != null) endDateChange.onItemChange();
                    });

                    if (statisticCallRequestViewModel.selectedEndDate.getValue() != null)
                        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(statisticCallRequestViewModel.selectedEndDate.getValue()));
                    if (statisticCallRequestViewModel.selectedStartDate.getValue() != null)
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(statisticCallRequestViewModel.selectedStartDate.getValue()));
                    else
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(LocalDate.now()));
                    startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
                    endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
                    endDateChooser.setEnabled(true);
                    changed = false;
                }
            }
        });

        floatingActionButton.setOnClickListener(v -> {
            if (isoCountryAdapter.anySelected() && criteriaAdapter.anySelected() && chartTypeAdapter.anySelected()) {
                Log.i(this.getClass().getName(), "creating statistic");
                requestStatistic(new StatisticCall(isoCountryAdapter.getSelectedItems(), chartTypeAdapter.getSelectedItems().get(0), criteriaAdapter.getSelectedItems(), statisticCallRequestViewModel.selectedStartDate.getValue(), statisticCallRequestViewModel.selectedEndDate.getValue()));
            } else {
                Log.i(this.getClass().getName(), "invalid input for statistic");
                Toast.makeText(getContext(), "Please select everything before proceeding", Toast.LENGTH_SHORT).show();
                if (!isoCountryAdapter.anySelected())
                    isoCountryTextView.setError("no item selected");
                if (!criteriaAdapter.anySelected())
                    criteriaNachoTextView.setError("no item selected");
                if (!chartTypeAdapter.anySelected())
                    chartTypeNachoTextView.setError("no item selected");
            }
        });
        Log.v(this.getClass().getName(), "finished creating View");
        return root;
    }

    public void requestStatistic(StatisticCall request) {
        StatisticRequestFragmentDirections.CreateStatistic action = StatisticRequestFragmentDirections.createStatistic(request, true);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        navHostFragment.getNavController().navigate(action);
    }

    private long localDateToMilliSeconds(LocalDate date) {
        return date.atStartOfDay().toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now())) * 1000;
    }

    private <T extends Enum<T>> void setupMultiAutoCompleteTextView(AutoCompleteTextView textView, AutoCompleteTextViewAdapter<T> adapter) {
        //when the user presses enter, use top suggestion
        textView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textView.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        //only works for non virtual keyboards
        textView.setOnEditorActionListener((v, actionId, event) -> {
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (adapter.filteredItems.size() > 0) {
                        textView.setError(null);
                        if (!textView.isPopupShowing()) textView.showDropDown();
                        textView.setListSelection(0);
                        textView.onCommitCompletion(new CompletionInfo(0, 0, ""));
                        Log.v(this.getClass().getName(), "AutoCompleted TextView query");
                    } else {
                        showErrorWithCompletion(textView);
                        return false;
                    }
                } else return false;
            } else if (actionId == EditorInfo.IME_NULL) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (adapter.filteredItems.size() > 0) {
                        textView.setError(null);
                        if (!textView.isPopupShowing()) textView.showDropDown();
                        textView.setListSelection(0);
                        textView.onCommitCompletion(new CompletionInfo(0, 0, ""));
                        Log.v(this.getClass().getName(), "AutoCompleted TextView query");
                    } else {
                        showErrorWithCompletion(textView);
                        return true;
                    }
                } else return false;
            } else return false;
            return true;
        });
        textView.setAdapter(adapter);
    }

    private <T extends Enum<T>> Chip getChip(T item) {
        Chip chip = new Chip(requireContext());
        chip.setCloseIconVisible(true);
        if (item instanceof ISOCountry) {
            chip.setChipIconVisible(true);
            chip.setIconStartPadding(10f);
            chip.setChipIcon(ContextCompat.getDrawable(requireContext(), ((ISOCountry) item).getFlagDrawableID()));
        }
        chip.setText(item.toString());
        chip.setTextIsSelectable(false);
        return chip;
    }

    private void showErrorWithCompletion(TextView textView) {
        if (textView.getError() == null) textView.setError("No Matching Item");
    }

    private <T extends Enum<T>> AutoCompleteTextViewAdapter.LimitListener getLimitListener(String name, TextView textView) {
        return (limit) -> {
            if (getContext() != null) {
                Toast.makeText(getContext(), getString(R.string.limit_reached, limit, name), Toast.LENGTH_SHORT).show();
                requireActivity().runOnUiThread(() -> textView.setError("reached Limit"));
                Log.i(this.getClass().getName(), "reached limit with " + name);
            }
        };
    }
}
