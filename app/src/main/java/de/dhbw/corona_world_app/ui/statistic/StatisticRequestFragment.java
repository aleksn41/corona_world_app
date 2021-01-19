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
import java.util.List;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
        }, year, month, day);

        endDatePicker = new DatePickerDialog(getContext(), R.style.SpinnerDatePickerStyle, (view, year1, month1, dayOfMonth) -> {
            end = LocalDate.of(year1, month1 + 1, dayOfMonth);
            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
            startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
        }, year, month, day);

        //sets the min date to the beginning of Corona
        startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));

        //sets the max date to today
        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
        endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));

        startDateChooser.setOnClickListener(v -> startDatePicker.show());
        endDateChooser.setOnClickListener(v -> endDatePicker.show());

        floatingActionButton.setOnClickListener(v -> {
            if (isoCountryAdapter.anySelected() && criteriaAdapter.anySelected() && chartTypeAdapter.anySelected() && start != null) {
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