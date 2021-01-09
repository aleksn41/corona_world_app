package de.dhbw.corona_world_app.ui.statistic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticViewModel =
                new ViewModelProvider(this).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic_request, container, false);

        ButtonSearchableDialogEnumChooser<ISOCountry> isoCountryButtonSearchableDialogEnumChooser = root.findViewById(R.id.isoCountryChooser);
        isoCountryButtonSearchableDialogEnumChooser.setItems(Arrays.asList(ISOCountry.values()));
        isoCountryButtonSearchableDialogEnumChooser.setLimit(APIManager.MAX_COUNTRY_LIST_SIZE, limit -> Toast.makeText(getContext(), "Limit of " + limit + " Countries reached", Toast.LENGTH_SHORT).show());

        ButtonSearchableDialogEnumChooser<Criteria> criteriaButtonSearchableDialogEnumChooser = root.findViewById(R.id.criteriaChooser);
        criteriaButtonSearchableDialogEnumChooser.setItems(Arrays.asList(Criteria.values()));

        ButtonSearchableDialogEnumChooser<ChartType> chartTypeButtonSearchableDialogEnumChooser = root.findViewById(R.id.chartTypeChooser);
        chartTypeButtonSearchableDialogEnumChooser.setItems(Arrays.asList(ChartType.values()));
        chartTypeButtonSearchableDialogEnumChooser.setLimit(1, limit -> Toast.makeText(getContext(), "Limit of " + limit + " Chart-Type reached", Toast.LENGTH_SHORT).show());

        //get current Date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Button startDateChooser=root.findViewById(R.id.startDateChooser);
        Button endDateChooser=root.findViewById(R.id.endDateChooser);

        startDatePicker =new DatePickerDialog(getContext(),R.style.SpinnerDatePickerStyle, (view, year12, month12, dayOfMonth) -> {
            start =LocalDate.of(year12, month12+1,dayOfMonth);
            startDateChooser.setText(start.format(StatisticCall.DATE_FORMAT));
            endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(start));
        },year,month,day);

        endDatePicker=new DatePickerDialog(getContext(),R.style.SpinnerDatePickerStyle, (view, year1, month1, dayOfMonth) -> {
            end=LocalDate.of(year1, month1+1,dayOfMonth);
            endDateChooser.setText(end.format(StatisticCall.DATE_FORMAT));
            startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(end));
        },year,month,day);

        //sets the min date to the beginning of Corona
        startDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));
        endDatePicker.getDatePicker().setMinDate(localDateToMilliSeconds(StatisticCall.MIN_DATE));

        //sets the max date to today
        startDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));
        endDatePicker.getDatePicker().setMaxDate(localDateToMilliSeconds(LocalDate.now()));

        startDateChooser.setOnClickListener(v->startDatePicker.show());
        endDateChooser.setOnClickListener(v->endDatePicker.show());

        root.findViewById(R.id.imageButton).setOnClickListener(v -> {
            if (isoCountryButtonSearchableDialogEnumChooser.anyItemSelected() && criteriaButtonSearchableDialogEnumChooser.anyItemSelected() && chartTypeButtonSearchableDialogEnumChooser.anyItemSelected()&&start!=null) {
                requestStatistic(new StatisticCall(isoCountryButtonSearchableDialogEnumChooser.getSelectedItems(), chartTypeButtonSearchableDialogEnumChooser.getSelectedItems().get(0), criteriaButtonSearchableDialogEnumChooser.getSelectedItems(),start,end));
            } else {
                Toast.makeText(getContext(), "Please select everything before proceeding", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    public void requestStatistic(StatisticCall request) {
        StatisticRequestFragmentDirections.CreateStatistic action = StatisticRequestFragmentDirections.createStatistic(request);
        Navigation.findNavController(getView()).navigate(action);
    }

    private long localDateToMilliSeconds(LocalDate date){
        return date.atStartOfDay().toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()))*1000;
    }
}