package de.dhbw.corona_world_app.ui.statistic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;

import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

/**
 * This Class is used to disallow certain items to be picked in the {@link StatisticRequestFragment} when certain conditions are met
 *
 * @author Thomas Meier (implemented Rule)
 * @author Aleksandr Stankoski (designed Class)
 */
public class StatisticRequestRule {

    public Rule rule;
    //keep a reference to the adapters containing the information
    final RuleEnumAdapter<ISOCountry> isoCountryAdapter;
    final RuleEnumAdapter<ChartType> chartTypeAdapter;
    final RuleEnumAdapter<Criteria> criteriaAdapter;
    final RuleDateRangeInterface ruleDateRangeInterface;
    //check if the rule has already been applied
    boolean ruleApplied = false;
    final OnItemsChangeListener checkCondition = new OnItemsChangeListener() {
        @Override
        public void onItemChange() {
            if (rule.conditionSatisfied(isoCountryAdapter.getSelectedItemsSize(), criteriaAdapter.getSelectedItemsSize(), ruleDateRangeInterface.getStartDate(), ruleDateRangeInterface.getEndDate())) {
                applyRule(rule);
                ruleApplied = true;
            } else if (ruleApplied) {
                doNotApplyRule();
                ruleApplied = false;
                rule.startAndEndDateMustBeSame = false;
                rule.allowOnlyOneCriteria = false;
                rule.allowOnlyOneCountry = false;
                rule.doNotAllowBarChart = false;
            }
        }
    };

    private abstract static class Rule {
        //if this condition is satisfied, the Rule will apply
        abstract boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate);

        //if the condition is satisfied, apply these Rules
        boolean allowOnlyOneCountry;
        boolean allowOnlyOneCriteria;
        boolean startAndEndDateMustBeSame;
        boolean doNotAllowBarChart;
    }

    interface OnItemsChangeListener {
        void onItemChange();
    }

    //interface an Adapter needs to implement
    public interface RuleEnumAdapter<T extends Enum<T>> {
        void setOnItemsChangeListener(OnItemsChangeListener listener);

        int getSelectedItemsSize();

        List<T> getSelectedItems();

        void conditionApplies(boolean allowOnlyOneItem);

        void conditionDoesNotApply();

        void update();
    }

    //interface the DatePicker need to implement
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
            boolean conditionSatisfied(int selectedISOCountriesSize, int selectedCriteriaSize, @Nullable LocalDate selectedStartDate, @Nullable LocalDate selectedEndDate) {
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
        isoCountryAdapter.update();
        criteriaAdapter.update();
        chartTypeAdapter.update();
    }

    private void doNotApplyRule() {
        isoCountryAdapter.conditionDoesNotApply();
        criteriaAdapter.conditionDoesNotApply();
        chartTypeAdapter.conditionDoesNotApply();
        ruleDateRangeInterface.conditionDoesNotApply();
        isoCountryAdapter.update();
        criteriaAdapter.update();
        chartTypeAdapter.update();
    }
}
