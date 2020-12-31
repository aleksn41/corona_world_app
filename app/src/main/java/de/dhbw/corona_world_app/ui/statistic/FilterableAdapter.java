package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;

import static de.dhbw.corona_world_app.ui.statistic.FilterableAdapter.*;

public class FilterableAdapter<T> extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private int limit = -1;
    private List<T> originalItems; // Original Values
    private List<T> filteredItems;
    private HashSet<T> itemsSelected;
    //if new items are set, apply last Filter Constraint to the new Data
    private CharSequence lastFilterConstraint;
    private LimitExceedListener<T> limitExceedListener;

    public interface LimitExceedListener<T> {
        void onLimitReached(int limit);
    }

    FilterableAdapter() {
        this.originalItems = new ArrayList<>();
        this.itemsSelected = new HashSet<>();
        filteredItems = this.originalItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multispinner_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final T data = filteredItems.get(position);
        boolean currentChecked = itemsSelected.contains(data);
        holder.textView.setText(data.toString());
        holder.checkBox.setChecked(currentChecked);

        holder.checkBox.setOnClickListener(v -> {
            if (itemsSelected.contains(data)) {
                itemsSelected.remove(data);
                setMarkedItem(holder, false);
            } else {
                if(itemsSelected.size()==limit){
                    limitExceedListener.onLimitReached(getLimit());
                    //revert click
                    ((CheckBox)v).toggle();
                }
                else{
                    itemsSelected.add(data);
                    setMarkedItem(holder, true);
                }

            }
        });

        setMarkedItem(holder, currentChecked);
    }

    private void setMarkedItem(ViewHolder holder, boolean mark) {
        holder.itemView.setAlpha(mark ? 0.3f : 1f);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public List<T> getAllItems() {
        return originalItems;
    }

    public Set<T> getSelectedItems() {
        return itemsSelected;
    }

    public int getLimit() {
        return limit;
    }

    public boolean anySelected() {
        return !itemsSelected.isEmpty();
    }

    public void setItems(List<T> items) {
        originalItems = items;
        if (lastFilterConstraint != null) getFilter().filter(lastFilterConstraint);
        else filteredItems = originalItems;
        notifyDataSetChanged();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setLimitExceedListener(LimitExceedListener<T> limitExceedListener) {
        this.limitExceedListener = limitExceedListener;
    }

    public void selectAllItems() {
        this.itemsSelected = new HashSet<>(originalItems);
        notifyDataSetChanged();
    }

    public void unSelectAllItems() {
        this.itemsSelected.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //save constraint
                lastFilterConstraint = constraint;
                //this must be a List of type T
                filteredItems = (List<T>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalItems;
                    results.count = originalItems.size();
                } else {
                    List<T> tempFilteredItems = originalItems.parallelStream().filter(p -> containsIgnoreCase(p.toString(), String.valueOf(constraint))).collect(Collectors.toList());
                    results.values = tempFilteredItems;
                    results.count = tempFilteredItems.size();
                }
                return results;
            }
        };
    }

    public boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.alertTextView);
            checkBox = itemView.findViewById(R.id.alertCheckbox);
        }
    }
}