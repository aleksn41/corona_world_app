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

    RuleEnumAdapter<ISOCountry> isoCountryAdapter;
    RuleEnumAdapter<ChartType> chartTypeAdapter;
    RuleEnumAdapter<Criteria> criteriaAdapter;
    RuleDateRangeInterface ruleDateRangeInterface;
    boolean ruleApplied=false;
    OnItemsChangeListener checkCondition = new OnItemsChangeListener() {
        @Override
        public void onItemChange() {
            if (rule.conditionSatisfied(isoCountryAdapter.getSelectedItemsSize(), criteriaAdapter.getSelectedItemsSize(), chartTypeAdapter.getSelectedItemsSize() == 0 ? null : chartTypeAdapter.getSelectedItems().get(0), ruleDateRangeInterface.getStartDate(), ruleDateRangeInterface.getEndDate())) {
                applyRule(rule);
                ruleApplied=true;
            }else if(ruleApplied){
                doNotApplyRule();
                ruleApplied=false;
                rule.startAndEndDateMustBeSame = false;
                rule.allowOnlyOneCriteria = false;
                rule.allowOnlyOneCountry = false;
                rule.doNotAllowBarChart = false;
            }
        }
    };

    private abstract static class Rule {
        //if this condition is satisfied, the Rule will apply
        abstract boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable ChartType selectedChartType, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate);

        //if the condition is satisfied, apply these Rules
        boolean allowOnlyOneCountry;
        boolean allowOnlyOneCriteria;
        boolean startAndEndDateMustBeSame;
        boolean doNotAllowBarChart;
    }

    interface ChartTypeAllowedInterface {
        boolean isAllowed(ChartType chartType);
    }

    interface OnItemsChangeListener {
        void onItemChange();
    }

    public interface RuleEnumAdapter<T extends Enum<T>> {
        void setOnItemsChangeListener(OnItemsChangeListener listener);

        int getSelectedItemsSize();

        List<T> getSelectedItems();

        void conditionApplies(boolean allowOnlyOneItem);

        void conditionDoesNotApply();
    }

    public interface RuleDateRangeInterface {
        LocalDate getStartDate();

        LocalDate getEndDate();

        void setOnStartDateChangeListener(OnItemsChangeListener listener);

        void setOnEndDateChangeListener(OnItemsChangeListener listener);

        void conditionApplies(boolean startAndEndDateMustBeSame);

        void conditionDoesNotApply();
    }

    StatisticRequestRule(@NonNull RuleEnumAdapter<ISOCountry> isoCountryAdapter, @NonNull RuleEnumAdapter<Criteria> criteriaAdapter, @NonNull RuleEnumAdapter<ChartType> chartTypeAdapter, @NonNull RuleDateRangeInterface ruleDateRangeInterface) {
        init();
        this.isoCountryAdapter = isoCountryAdapter;
        this.criteriaAdapter = criteriaAdapter;
        this.chartTypeAdapter = chartTypeAdapter;
        this.ruleDateRangeInterface = ruleDateRangeInterface;

        this.isoCountryAdapter.setOnItemsChangeListener(checkCondition);
        this.criteriaAdapter.setOnItemsChangeListener(checkCondition);
        this.chartTypeAdapter.setOnItemsChangeListener(checkCondition);
        this.ruleDateRangeInterface.setOnStartDateChangeListener(checkCondition);
        this.ruleDateRangeInterface.setOnEndDateChangeListener(checkCondition);
    }

    private void init() {
        this.rule = new Rule() {
            @Override
            boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable ChartType selectedChartType, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate) {
                boolean countryList2D = selectedISOCountriesSize > 1;
                boolean criteriaList2D = selectedCriteriaSize > 1;
                boolean dates2D = selectedStartDate != null ? selectedEndDate == null || !selectedStartDate.isEqual(selectedEndDate) : selectedEndDate != null;
                if (countryList2D) {
                    if (criteriaList2D) {
                        rule.startAndEndDateMustBeSame = true;
                        return true;
                    }
                    if (dates2D) {
                        rule.allowOnlyOneCriteria = true;
                        return true;
                    }
                } else {
                    if (criteriaList2D) {
                        if (dates2D) {
                            rule.allowOnlyOneCountry = true;
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        rule.startAndEndDateMustBeSame = false;
        rule.allowOnlyOneCriteria = false;
        rule.allowOnlyOneCountry = false;
        rule.doNotAllowBarChart = false;
    }

    private void applyRule(Rule rule) {
        isoCountryAdapter.conditionApplies(rule.allowOnlyOneCountry);
        criteriaAdapter.conditionApplies(rule.allowOnlyOneCriteria);
        chartTypeAdapter.conditionApplies(rule.doNotAllowBarChart);
        ruleDateRangeInterface.conditionApplies(rule.startAndEndDateMustBeSame);
    }

    private void doNotApplyRule() {
        isoCountryAdapter.conditionDoesNotApply();
        criteriaAdapter.conditionDoesNotApply();
        chartTypeAdapter.conditionDoesNotApply();
        ruleDateRangeInterface.conditionDoesNotApply();
    }
}
