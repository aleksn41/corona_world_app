package de.dhbw.corona_world_app.datastructure;

import java.util.List;

public class StatisticCall {
    private List<ISOCountry> countryList;

    private Charttype charttype;

    private List<Criteria> criteriaList;

    public StatisticCall(List<ISOCountry> countryList, Charttype charttype, List<Criteria> criteriaList) {
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

    public Charttype getCharttype() {
        return charttype;
    }

    public void setCharttype(Charttype charttype) {
        this.charttype = charttype;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }
}