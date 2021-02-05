package de.dhbw.corona_world_app.datastructure;

import androidx.annotation.NonNull;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;


import java.io.Serializable;

import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

public class StatisticCall implements Serializable {
    public static final LocalDate MIN_DATE = LocalDate.of(2020, 1, 22);
    public static DateTimeFormatter DATE_FORMAT= DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final LocalDate NOW=null;

    private List<ISOCountry> countryList;

    private ChartType chartType;

    private List<Criteria> criteriaList;

    private final LocalDate startDate;

    private final LocalDate endDate;

    public StatisticCall(@NonNull List<ISOCountry> countryList, @NonNull ChartType chartType, @NonNull List<Criteria> criteriaList, LocalDate startDate, LocalDate endDate) {
        if (startDate!=NOW&&startDate.isBefore(MIN_DATE))
            throw new IllegalArgumentException("Parameter \"startDate\"=" + startDate.toString() + " is too early! Expected Date is after 21.01.2020.");
        if (endDate!=NOW&&(startDate==NOW||endDate.isBefore(startDate)))
            throw new IllegalArgumentException("Parameter \"endDate\"=" + endDate.toString() + " is before parameter \"startDate\"!");

        this.countryList = countryList;
        this.chartType = chartType;
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

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType charttype) {
        this.chartType = charttype;
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

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getTitle(){
        if (startDate != NOW && (endDate == NOW || startDate.isBefore(endDate))) return "Statistic";
        else if(startDate == NOW) return "Statistic: " + getDateFormatted(LocalDate.now());
        else return "Statistic: " + getDateFormatted(this.startDate);
    }

    private String getDateFormatted(LocalDate date) {
        String year = Integer.toString(date.getYear());
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + year.substring(2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticCall that = (StatisticCall) o;
        return Objects.equals(countryList, that.countryList) &&
                chartType == that.chartType &&
                Objects.equals(criteriaList, that.criteriaList) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountryList(), chartType, getCriteriaList(), getStartDate(), getEndDate());
    }

    @Override
    public String toString() {
        return "StatisticCall{" +
                "countryList=" + countryList +
                ", chartType=" + chartType +
                ", criteriaList=" + criteriaList +
                ", startDate=" + (startDate==NOW?"Now":startDate.format(DATE_FORMAT)) +
                ", endDate=" + (endDate==NOW?"Now":endDate.format(DATE_FORMAT)) +
                '}';
    }
}