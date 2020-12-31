package de.dhbw.corona_world_app.datastructure;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

//TODO if statistic call is to slow implement Parcelable
public class StatisticCall implements Serializable {
    private List<ISOCountry> countryList;

    private ChartType charttype;

    private List<Criteria> criteriaList;

    public StatisticCall(@NonNull List<ISOCountry> countryList,@NonNull ChartType charttype,@NonNull List<Criteria> criteriaList) {
        this.countryList = countryList;
        this.charttype = charttype;
        this.criteriaList = criteriaList;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticCall that = (StatisticCall) o;
        return getCountryList().equals(that.getCountryList()) &&
                getCharttype() == that.getCharttype() &&
                getCriteriaList().equals(that.getCriteriaList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountryList(), getCharttype(), getCriteriaList());
    }

    @Override
    public String toString() {
        return "StatisticCall{" +
                "countryList=" + countryList +
                ", charttype=" + charttype +
                ", criteriaList=" + criteriaList +
                '}';
    }
}