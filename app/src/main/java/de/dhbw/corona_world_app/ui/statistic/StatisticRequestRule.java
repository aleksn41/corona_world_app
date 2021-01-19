package de.dhbw.corona_world_app.ui.statistic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

//this is used in order to disallow certain Items in the UI of StatisticRequest when certain conditions are met
public class StatisticRequestRule {

    public Rule rule;

    RuleISOCountryAdapter isoCountryAdapter;
    RuleChartTypeAdapter chartTypeAdapter;
    RuleCriteriaAdapter criteriaAdapter;
    RuleDateRangeInterface ruleDateRangeInterface;

    OnItemsChangeListener checkCondition=new OnItemsChangeListener() {
        @Override
        public void onItemChange() {
            if(rule.conditionSatisfied(isoCountryAdapter.getSelectedItems().size(),criteriaAdapter.getSelectedItems().size(),chartTypeAdapter.getSelectedItems().get(0),ruleDateRangeInterface.getStartDate(),ruleDateRangeInterface.getEndDate())){
                applyRule(rule);
            }
        }
    };

    private abstract static class Rule{
        //if this condition is satisfied, the Rule will apply
        abstract boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable ChartType selectedChartType, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate);
        //if the condition is satisfied, apply these Rules
        boolean allowOnlyOneCountry;
        boolean allowOnlyOneCriteria;
        boolean startAndEndDateMustBeSame;
        //allow certain ChartTypes
        ChartTypeAllowedInterface chartTypeAllowedInterface;
    }

    interface ChartTypeAllowedInterface {
        boolean isAllowed(ChartType chartType);
    }

    interface OnItemsChangeListener{
        void onItemChange();
    }

    private interface RuleEnumAdapter<T extends Enum<T>> {
        void setOnItemsChangeListener(OnItemsChangeListener listener);
        List<T> getSelectedItems();
    }

    public interface RuleISOCountryAdapter extends RuleEnumAdapter<ISOCountry> {
        void conditionApplies(boolean allowOnlyOneCountry);
    }

    public interface RuleChartTypeAdapter extends RuleEnumAdapter<ChartType> {
        void conditionApplies(ChartTypeAllowedInterface chartTypeAllowedInterface);
    }

    public interface RuleCriteriaAdapter extends RuleEnumAdapter<Criteria> {
        void conditionApplies(boolean allowOnlyOneCriteria);
    }

    public interface RuleDateRangeInterface{
        LocalDate getStartDate();
        LocalDate getEndDate();
        void setOnStartDateChangeListener(OnItemsChangeListener listener);
        void setOnEndDateChangeListener(OnItemsChangeListener listener);
        void conditionApplies(boolean startAndEndDateMustBeSame);
    }

    StatisticRequestRule(@NonNull RuleISOCountryAdapter isoCountryAdapter, @NonNull RuleCriteriaAdapter criteriaAdapter, @NonNull RuleChartTypeAdapter chartTypeAdapter,@NonNull RuleDateRangeInterface ruleDateRangeInterface){
        init();
        this.isoCountryAdapter=isoCountryAdapter;
        this.criteriaAdapter=criteriaAdapter;
        this.chartTypeAdapter=chartTypeAdapter;
        this.ruleDateRangeInterface=ruleDateRangeInterface;

        this.isoCountryAdapter.setOnItemsChangeListener(checkCondition);
        this.criteriaAdapter.setOnItemsChangeListener(checkCondition);
        this.chartTypeAdapter.setOnItemsChangeListener(checkCondition);
        this.ruleDateRangeInterface.setOnStartDateChangeListener(checkCondition);
        this.ruleDateRangeInterface.setOnEndDateChangeListener(checkCondition);
    }

    private void init(){
        //TODO implement this
        this.rule=new Rule() {
            @Override
            boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable ChartType selectedChartType, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate) {
                return false;
            }
        };
        rule.allowOnlyOneCountry=false;
        rule.allowOnlyOneCriteria=false;
        rule.startAndEndDateMustBeSame=false;
        rule.chartTypeAllowedInterface= new ChartTypeAllowedInterface() {
            @Override
            public boolean isAllowed(ChartType chartType) {
                return true;
            }
        };
    }
    private void applyRule(Rule rule){
        isoCountryAdapter.conditionApplies(rule.allowOnlyOneCountry);
        criteriaAdapter.conditionApplies(rule.allowOnlyOneCriteria);
        chartTypeAdapter.conditionApplies(rule.chartTypeAllowedInterface);
        ruleDateRangeInterface.conditionApplies(rule.startAndEndDateMustBeSame);
    }
}
