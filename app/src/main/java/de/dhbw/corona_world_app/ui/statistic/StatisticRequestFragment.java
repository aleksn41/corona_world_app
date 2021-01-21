package de.dhbw.corona_world_app.ui.statistic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.terminator.DefaultChipTerminatorHandler;
import com.hootsuite.nachos.tokenizer.ChipTokenizer;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;
import com.hootsuite.nachos.validator.NachoValidator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Country;
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
        Logger.disableLogging();
        if(Logger.getDebbuging()) {
            List<ISOCountry> clist = new ArrayList<>();
            clist.add(ISOCountry.Belize);
            List<Criteria> crlist = new ArrayList<>();
            //crlist.add(Criteria.HEALTHY);
            crlist.add(Criteria.INFECTED);
            crlist.add(Criteria.DEATHS);
            requestStatistic(new StatisticCall(clist, ChartType.BAR, crlist, LocalDate.now().minusDays(5), LocalDate.now()));
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
        //setup FloatingButton
        if (scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) == 0)
            floatingActionButton.extend();
        else floatingActionButton.shrink();
        //TODO visually show that limit is reached
        CustomNachoTextView isoCountryNachoTextView = root.findViewById(R.id.nachoIsoCountryTextView);
        MultiAutoCompleteTextViewAdapter<ISOCountry> isoCountryAdapter = new MultiAutoCompleteTextViewAdapter<>(getContext(), ISOCountry.class, APIManager.MAX_COUNTRY_LIST_SIZE,null);
        setupNachoTextView(ISOCountry.class, isoCountryNachoTextView, isoCountryAdapter);

        CustomNachoTextView criteriaNachoTextView = root.findViewById(R.id.nachoCriteriaTextView);
        MultiAutoCompleteTextViewAdapter<Criteria> criteriaAdapter = new MultiAutoCompleteTextViewAdapter<>(getContext(), Criteria.class, -1,null);
        setupNachoTextView(Criteria.class, criteriaNachoTextView, criteriaAdapter);

        CustomNachoTextView chartTypeNachoTextView = root.findViewById(R.id.nachoChartTypeTextView);
        MultiAutoCompleteTextViewAdapter<ChartType> chartTypeAdapter = new MultiAutoCompleteTextViewAdapter<ChartType>(getContext(), ChartType.class, 1,null){
            //special case where if condition applies, a bar chart cannot be shown
            @Override
            public void conditionApplies(boolean allowOnlyOneItem) {
                addToBlackList(ChartType.BAR);
                super.conditionApplies(allowOnlyOneItem);
            }
        };
        setupNachoTextView(ChartType.class, chartTypeNachoTextView, chartTypeAdapter);

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
            if(startDateChange!=null)startDateChange.onItemChange();
        }, year, month, day);

        endDatePicker = new DatePickerDialog(getContext(), R.style.SpinnerDatePickerStyle, (view, year1, month1, dayOfMonth) -> {
            end = LocalDate.of(year1, month1 + 1, dayOfMonth);
            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
            startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
            if(endDateChange!=null)endDateChange.onItemChange();
        }, year, month, day);

        //sets the min date to the beginning of Corona
        startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));

        //sets the max date to today
        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
        endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));

        //Default selection is now
        start=StatisticCall.NOW;
        startDateChooser.setText("Now");
        end=StatisticCall.NOW;
        endDateChooser.setText("Now");

        startDateChooser.setOnClickListener(v -> startDatePicker.show());
        endDateChooser.setOnClickListener(v -> endDatePicker.show());

        //setup RuleWatcher
        rule= new StatisticRequestRule(isoCountryAdapter, criteriaAdapter, chartTypeAdapter, new StatisticRequestRule.RuleDateRangeInterface() {
            boolean changed=false;

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
                startDateChange=listener;
            }

            @Override
            public void setOnEndDateChangeListener(StatisticRequestRule.OnItemsChangeListener listener) {
                   endDateChange=listener;
            }

            @Override
            public void conditionApplies(boolean startAndEndDateMustBeSame) {
                if(startAndEndDateMustBeSame) {
                    //If necessary change Dates to now if they are different
                    if (start != end){
                        throw new IllegalStateException("Unexpected State, start and end are different but must be same");
                    }
                    startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
                    startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
                    startDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            start = LocalDate.of(year, month + 1, dayOfMonth);
                            startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                            endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                            endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(start));
                            endDatePicker.getDatePicker().updateDate(year,month+1,dayOfMonth);
                            end=start;
                            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
                            endDatePicker.getDatePicker().setOnDateChangedListener(null);
                            if(startDateChange!=null)startDateChange.onItemChange();
                            if(endDateChange!=null)endDateChange.onItemChange();
                        }
                    });
                    changed=true;
                }else if(changed){
                    startDatePicker.getDatePicker().setOnDateChangedListener((view, year12, month12, dayOfMonth) -> {
                        start = LocalDate.of(year12, month12 + 1, dayOfMonth);
                        startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
                        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                        if(startDateChange!=null)startDateChange.onItemChange();
                    });
                    endDatePicker.getDatePicker().setOnDateChangedListener( (view, year1, month1, dayOfMonth) -> {
                        end = LocalDate.of(year1, month1 + 1, dayOfMonth);
                        endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
                        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
                        if(endDateChange!=null)endDateChange.onItemChange();
                    });
                    if(end!=null)startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
                    if(start!=null)endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
                    else endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(LocalDate.now()));
                    changed=false;
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

    private <T extends Enum<T>> void setupNachoTextView(Class<T> tClass, CustomNachoTextView textView, MultiAutoCompleteTextViewAdapter<T> adapter) {
        textView.enableEditChipOnTouch(true, true);

        textView.setOnChipClickListener((chip, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                adapter.unSelectItem(tClass.cast(chip.getData()));
            }
        });
        //need to get old OnItemClickListener for library to function properly
        AdapterView.OnItemClickListener oldListener = textView.getOnItemClickListener();

        textView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.selectItem(position);
            oldListener.onItemClick(parent, view, position, id);
        });

        textView.setChipTokenizer(new SpanChipTokenizer<ChipSpan>(getContext(), new ChipSpanChipCreator(), ChipSpan.class) {
            @Override
            public void deleteChip(Chip chip, Editable text) {
                adapter.unSelectItem(tClass.cast(chip.getData()));
                super.deleteChip(chip, text);
            }
        });

        textView.setChipTerminatorHandler(new DefaultChipTerminatorHandler() {
            @Override
            public int findAndHandleChipTerminators(@NonNull ChipTokenizer tokenizer, @NonNull Editable text, int start, int end, boolean isPasteEvent) {
                super.findAndHandleChipTerminators(tokenizer, text, start, end, isPasteEvent);
                boolean allChipsValid = textView.getAllChips().parallelStream().allMatch(chip -> chip.getData() != null);
                if (!allChipsValid) {
                    textView.performValidation();

                }
                return textView.getText().length();
            }
        });
        textView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        textView.setNachoValidator(new NachoValidator() {
            @Override
            public boolean isValid(@NonNull ChipTokenizer chipTokenizer, CharSequence text) {
                // The text is considered valid if there are no unterminated tokens (everything is a chip)
                List<Pair<Integer, Integer>> unterminatedTokens = chipTokenizer.findAllTokens(text);
                // All Chips are Enums
                List<Chip> chips = textView.getAllChips();
                return unterminatedTokens.isEmpty() && chips.parallelStream().allMatch(chip -> chip.getData() != null);
            }

            @Override
            public CharSequence fixText(@NonNull ChipTokenizer chipTokenizer, CharSequence invalidText) {
                SpannableStringBuilder newText = new SpannableStringBuilder(invalidText);
                chipTokenizer.terminateAllTokens(newText);
                Chip[] chips = chipTokenizer.findAllChips(0, newText.length(), newText);
                for (Chip chip : chips) {
                    if (chip.getData() == null) {
                        int start = chipTokenizer.findChipStart(chip, newText);
                        int end = chipTokenizer.findChipEnd(chip, newText);
                        newText.replace(start, end, "");
                    }
                }
                return newText;
            }
        });

        textView.setAdapter(adapter);
        textView.setThreshold(0);
    }
}