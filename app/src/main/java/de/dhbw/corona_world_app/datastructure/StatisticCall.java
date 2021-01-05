package de.dhbw.corona_world_app.datastructure;

import androidx.annotation.NonNull;

import java.time.LocalDate;

import java.util.List;
import java.util.Objects;

public class StatisticCall {
    public static final LocalDate MIN_DATE = LocalDate.of(2020,1,22);

    private List<ISOCountry> countryList;

    private ChartType charttype;

    private List<Criteria> criteriaList;

    private LocalDate startDate;

    private LocalDate endDate;

    public StatisticCall(@NonNull List<ISOCountry> countryList,@NonNull Charttype charttype,@NonNull List<Criteria> criteriaList, @NonNull LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(MIN_DATE)) throw new IllegalArgumentException("Parameter \"startDate\"=" + startDate.toString() + " is too early! Expected Date is after 21.01.2020.");
        if(endDate.isBefore(startDate)) throw new IllegalArgumentException("Parameter \"endDate\"=" + endDate.toString() + " is before parameter \"startDate\"!");

        this.countryList = countryList;
        this.charttype = charttype;
        this.criteriaList = criteriaList;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<ISOCountry> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<ISOCountry> countryList) {
        this.countryList = countryList;
    }

    public ChartType getCharttype() {
        return charttype;
    }

    public void setCharttype(ChartType charttype) {
        this.charttype = charttype;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NonNull LocalDate startDate) {
        if(startDate.isBefore(MIN_DATE)) throw new IllegalArgumentException("Parameter \"startDate\"=" + startDate.toString() + " is too early! Expected Date is after 21.01.2020.");
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if(endDate.isBefore(startDate)) throw new IllegalArgumentException("Parameter \"endDate\"=" + endDate.toString() + " is before parameter \"startDate\"!");
        this.endDate = endDate;

    }
}