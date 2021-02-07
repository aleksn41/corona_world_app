package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;

/**
 * This Adapter is used to Filter and limit the amount of items displayed in the {@link android.widget.AutoCompleteTextView}
 * This Adapter also implements the {@link StatisticRequestRule.RuleEnumAdapter} in order to disallow certain items when a certain selection has been made
 * See {@link StatisticRequestRule} for more info on the Rule System
 *
 * @param <T> the enum used in the {@link android.widget.AutoCompleteTextView}
 * @author Aleksandr Stankoski
 */
public class AutoCompleteTextViewAdapter<T extends Enum<T>> extends BaseAdapter implements Filterable, StatisticRequestRule.RuleEnumAdapter<T> {
    private final Context context;
    HashSet<T> selectedItems;
    List<T> originalItems;
    List<T> filteredItems;
    HashSet<T> blackListItems;
    int originalLimit;
    int limit;
    public static final int NO_LIMIT = -1;
    LimitListener limitListener;
    StatisticRequestRule.OnItemsChangeListener itemsChangeListener;

    interface LimitListener {
        void onLimitReached(int limit);
    }

    public AutoCompleteTextViewAdapter(Context context, Class<T> tClass, int limit, LimitListener limitListener) {
        this.context = context;
        originalItems = Arrays.asList(tClass.getEnumConstants());
        selectedItems = new HashSet<>();
        filteredItems = originalItems;
        this.limit = limit;
        this.originalLimit = limit;
        this.limitListener = limitListener;
        this.blackListItems = new HashSet<>();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (limit != NO_LIMIT && selectedItems.size() == limit) {
                    results.values = new ArrayList<>();
                    if (limitListener != null) limitListener.onLimitReached(limit);
                } else {
                    List<T> tempFilteredItems;
                    if (constraint == null || constraint.length() == 0) {
                        tempFilteredItems = originalItems.parallelStream().filter(p -> !selectedItems.contains(p) && !blackListItems.contains(p)).collect(Collectors.toList());
                    } else
                        tempFilteredItems = originalItems.parallelStream().filter(p -> !selectedItems.contains(p) && containsIgnoreCase(p.toString(), String.valueOf(constraint)) && !blackListItems.contains(p)).collect(Collectors.toList());
                    results.values = tempFilteredItems;
                    results.count = tempFilteredItems.size();
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //this must be a List of type T
                filteredItems = (List<T>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }
        };
    }

    private boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, i, searchStr.length()))
                return true;
        }
        return false;
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public T getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.exposed_dropdown_list_item, parent, false);
        }
        T currentItem = getItem(position);
        TextView textView = convertView.findViewById(R.id.exposedTextView);
        textView.setText(currentItem.toString());
        return convertView;
    }

    public void submitSelectedItems(HashSet<T> items) {
        selectedItems = items;
        notifyDataSetChanged();
        itemsChangeListener.onItemChange();
    }

    public boolean anySelected() {
        return !selectedItems.isEmpty();
    }

    @Override
    public void setOnItemsChangeListener(StatisticRequestRule.OnItemsChangeListener listener) {
        this.itemsChangeListener = listener;
    }

    public List<T> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public int getSelectedItemsSize() {
        return selectedItems.size();
    }

    @Override
    public void conditionApplies(boolean allowOnlyOneItem) {
        if (allowOnlyOneItem) limit = 1;
        else limit = originalLimit;
    }

    @Override
    public void conditionDoesNotApply() {
        conditionApplies(false);
        blackListItems.clear();
    }

    public void addToBlackList(T item) {
        blackListItems.add(item);
    }
}