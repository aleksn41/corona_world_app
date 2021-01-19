package de.dhbw.corona_world_app.ui.statistic;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

//this is used in order to disallow certain Items in the UI of StatisticRequest when certain conditions are met
public class StatisticRequestRule {

    public Rule rule;

    private abstract static class Rule{
        //if this condition is satisfied, the Rule will apply
        abstract boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable ChartType selectedChartType, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate);
        //if the condition is satisfied, apply these Rules
        boolean allowOnlyOneCountry;
        boolean allowOnlyOneCriteria;
        boolean startAndEndDateMustBeSame;
        //allow certain ChartTypes
        abstract boolean chartTypeIsAllowed(ChartType chartType);

    }

    StatisticRequestRule(){
        init();
    }

    private void init(){
        //TODO implement this
        this.rule=new Rule() {
            @Override
            boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable ChartType selectedChartType, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate) {
                return false;
            }

            @Override
            boolean chartTypeIsAllowed(ChartType chartType) {
                return true;
            }
        };
        rule.allowOnlyOneCountry=false;
        rule.allowOnlyOneCriteria=false;
        rule.startAndEndDateMustBeSame=false;
    }
}
