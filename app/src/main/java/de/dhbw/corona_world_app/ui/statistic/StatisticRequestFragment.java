package de.dhbw.corona_world_app.ui.statistic;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import de.dhbw.corona_world_app.datastructure.StatisticCall;

//TODO put data in View Model
public class StatisticRequestFragment extends Fragment {

    private StatisticViewModel statisticViewModel;

    //setup DatePicker
    private LocalDate start;
    private LocalDate end;

    DatePickerDialog startDatePicker;
    DatePickerDialog endDatePicker;

    StatisticRequestRule rule;

    StatisticRequestRule.OnItemsChangeListener startDateChange;
    StatisticRequestRule.OnItemsChangeListener endDateChange;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //TODO delete after debugging is done
        //Logger.disableLogging();
        if (Logger.getDebbuging()) {
            List<ISOCountry> clist = new ArrayList<>();
            clist.add(ISOCountry.Germany);
            //clist.add(ISOCountry.Belize);
            List<Criteria> crlist = new ArrayList<>();
            //crlist.add(Criteria.HEALTHY);
            crlist.add(Criteria.INFECTED);
            //crlist.add(Criteria.DEATHS);
            requestStatistic(new StatisticCall(clist, ChartType.BAR, crlist, LocalDate.now().minusDays(89), LocalDate.now()));
        }
        statisticViewModel =
                new ViewModelProvider(this).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic_request, container, false);

        ExtendedFloatingActionButton floatingActionButton = root.findViewById(R.id.floating_action_button);

        //change floating button based on position in Scrollview
        ScrollView scrollView = root.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            boolean atEndOfView = scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollY) == 0;
            if (atEndOfView) {
                floatingActionButton.extend();
            } else floatingActionButton.shrink();
        });
        //TODO change with post
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
            }
        });

        //TODO visually show that limit is reached
        CustomAutoCompleteTextView isoCountryNachoTextView = root.findViewById(R.id.nachoIsoCountryTextView);
        AutoCompleteTextViewAdapter<ISOCountry> isoCountryAdapter = new AutoCompleteTextViewAdapter<>(getContext(), ISOCountry.class, APIManager.MAX_COUNTRY_LIST_SIZE, null);
        setupMultiAutoCompleteTextView(isoCountryNachoTextView, isoCountryAdapter, root.findViewById(R.id.isoCountryChips));

        CustomAutoCompleteTextView criteriaNachoTextView = root.findViewById(R.id.nachoCriteriaTextView);
        AutoCompleteTextViewAdapter<Criteria> criteriaAdapter = new AutoCompleteTextViewAdapter<>(getContext(), Criteria.class, -1, null);
        setupMultiAutoCompleteTextView(criteriaNachoTextView, criteriaAdapter, root.findViewById(R.id.criteriaChips));

        CustomAutoCompleteTextView chartTypeNachoTextView = root.findViewById(R.id.nachoChartTypeTextView);
        AutoCompleteTextViewAdapter<ChartType> chartTypeAdapter = new AutoCompleteTextViewAdapter<ChartType>(getContext(), ChartType.class, 1, null) {
            //special case where if condition applies, a bar chart cannot be shown
            @Override
            public void conditionApplies(boolean allowOnlyOneItem) {
                if (allowOnlyOneItem) addToBlackList(ChartType.BAR);
                super.conditionApplies(allowOnlyOneItem);
            }
        };
        setupMultiAutoCompleteTextView(chartTypeNachoTextView, chartTypeAdapter, root.findViewById(R.id.chartTypeChips));

        //get current Date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Button startDateChooser = root.findViewById(R.id.startDateChooser);
        Button endDateChooser = root.findViewById(R.id.endDateChooser);

        startDatePicker = new DatePickerDialog(getContext(), R.style.SpinnerDatePickerStyle, (view, year12, month12, dayOfMonth) -> {
            start = LocalDate.of(year12, month12 + 1, dayOfMonth);
            startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
            endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
            if (startDateChange != null) startDateChange.onItemChange();
        }, year, month, day);

        endDatePicker = new DatePickerDialog(getContext(), R.style.SpinnerDatePickerStyle, (view, year1, month1, dayOfMonth) -> {
            end = LocalDate.of(year1, month1 + 1, dayOfMonth);
            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
            startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
            if (endDateChange != null) endDateChange.onItemChange();
        }, year, month, day);

        //sets the min date to the beginning of Corona
        startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));

        //sets the max date to today
        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
        endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));

        //Default selection is now
        start = StatisticCall.NOW;
        startDateChooser.setText("Now");
        end = StatisticCall.NOW;
        endDateChooser.setText("Now");

        startDateChooser.setOnClickListener(v -> startDatePicker.show());
        endDateChooser.setOnClickListener(v -> endDatePicker.show());

        //setup RuleWatcher
        rule = new StatisticRequestRule(isoCountryAdapter, criteriaAdapter, chartTypeAdapter, new StatisticRequestRule.RuleDateRangeInterface() {
            boolean changed = false;

            @Override
            public LocalDate getStartDate() {
                return start;
            }

            @Override
            public LocalDate getEndDate() {
                return end;
            }

            @Override
            public void setOnStartDateChangeListener(StatisticRequestRule.OnItemsChangeListener listener) {
                startDateChange = listener;
            }

            @Override
            public void setOnEndDateChangeListener(StatisticRequestRule.OnItemsChangeListener listener) {
                endDateChange = listener;
            }

            @Override
            public void conditionApplies(boolean startAndEndDateMustBeSame) {
                if (startAndEndDateMustBeSame && !changed) {
                    //end and start date must not be same
                    if (!Objects.equals(start, end)) {
                        throw new IllegalStateException("Unexpected State, start and end are different but must be same");
                    }
                    endDatePicker.setOnDateSetListener(null);
                    endDateChooser.setEnabled(false);
                    startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
                    startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
                    startDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            start = LocalDate.of(year, month + 1, dayOfMonth);
                            startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                            endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                            endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(start));
                            endDatePicker.getDatePicker().updateDate(year, month + 1, dayOfMonth);
                            end = start;
                            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
                            if (startDateChange != null) startDateChange.onItemChange();
                            if (endDateChange != null) endDateChange.onItemChange();
                        }
                    });
                    changed = true;
                }
            }

            @Override
            public void conditionDoesNotApply() {
                if (changed) {
                    startDatePicker.setOnDateSetListener((view, year12, month12, dayOfMonth) -> {
                        start = LocalDate.of(year12, month12 + 1, dayOfMonth);
                        startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                        if (startDateChange != null) startDateChange.onItemChange();
                    });
                    endDatePicker.setOnDateSetListener((view, year1, month1, dayOfMonth) -> {
                        end = LocalDate.of(year1, month1 + 1, dayOfMonth);
                        endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
                        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
                        if (endDateChange != null) endDateChange.onItemChange();
                    });

                    if (end != null)
                        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
                    if (start != null)
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
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
                requestStatistic(new StatisticCall(isoCountryAdapter.getSelectedItems(), chartTypeAdapter.getSelectedItems().get(0), criteriaAdapter.getSelectedItems(), start, end));
            } else {
                Toast.makeText(getContext(), "Please select everything before proceeding", Toast.LENGTH_SHORT).show();
            }
        });
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

    private <T extends Enum<T>> void setupMultiAutoCompleteTextView(AutoCompleteTextView textView, AutoCompleteTextViewAdapter<T> adapter, ChipGroup chipGroup) {
        textView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.selectItem(position);
            T selectedItem = adapter.getItem(position);
            textView.setText("");
            Chip itemChip = getChip(selectedItem);
            itemChip.setOnCloseIconClickListener(v -> {
                adapter.unSelectItem(selectedItem);
                chipGroup.removeView(v);
            });
            chipGroup.addView(itemChip);
        });
        //when the user presses enter, use top suggestion
        textView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textView.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        //only works for non virtual keyboards
        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (adapter.filteredItems.size() > 0) {
                            if (!textView.isPopupShowing()) textView.showDropDown();
                            textView.onCommitCompletion(new CompletionInfo(0, 0, ""));
                        } else {
                            showErrorWithCompletion(textView);
                            return false;
                        }
                    } else return false;
                } else if (actionId == EditorInfo.IME_NULL) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (adapter.filteredItems.size() > 0) {
                            if (!textView.isPopupShowing()) textView.showDropDown();
                            textView.onCommitCompletion(new CompletionInfo(0, 0, ""));
                        } else {
                            showErrorWithCompletion(textView);
                            return true;
                        }
                    } else return false;
                } else return false;
                return true;
            }
        });
        textView.setAdapter(adapter);
    }

    private <T extends Enum<T>> Chip getChip(T item) {
        Chip chip = new Chip(requireContext());
        chip.setCloseIconVisible(true);
        chip.setChipIconVisible(true);
        chip.setChipIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_black_24dp));
        chip.setText(item.toString());
        chip.setTextIsSelectable(false);
        return chip;
    }

    private void showErrorWithCompletion(TextView textView) {
        textView.setError("No Matching Item");
    }
}