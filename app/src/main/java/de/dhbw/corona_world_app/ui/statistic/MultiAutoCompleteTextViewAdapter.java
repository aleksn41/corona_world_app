package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.database.DataSetObserver;
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

public class MultiAutoCompleteTextViewAdapter<T extends Enum<T>> extends BaseAdapter implements Filterable {
    private final Context context;
    HashSet<T> selectedItems;
    List<T> originalItems;
    List<T> filteredItems;
    int limit;

    public MultiAutoCompleteTextViewAdapter(Context context, Class<T> tClass,int limit) {
        this.context=context;
        originalItems = Arrays.asList(tClass.getEnumConstants());
        selectedItems=new HashSet<>();
        filteredItems= originalItems;
        this.limit=limit;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(limit!=-1&&selectedItems.size()==limit){
                    results.values=new ArrayList<>();
                    return results;
                }
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalItems;
                    results.count = originalItems.size();
                } else {
                    List<T> tempFilteredItems = originalItems.parallelStream().filter(p -> !selectedItems.contains(p)&&containsIgnoreCase(p.toString(), String.valueOf(constraint))).collect(Collectors.toList());
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
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.exposed_dropdown_list_item,parent,false);
        }
        T currentItem=getItem(position);
        TextView textView=convertView.findViewById(R.id.exposedTextView);
        textView.setText(currentItem.toString());
        return convertView;
    }

    public void selectItem(int position){
        T currentItem=getItem(position);
        selectedItems.add(currentItem);
    }

    public void unSelectItem(T item){
        selectedItems.remove(item);
    }

    public boolean anySelected(){
        return !selectedItems.isEmpty();
    }

    public List<T> getSelectedItems(){
        return new ArrayList<>(selectedItems);
    }

    public T getFirstFilteredItem(){
        if(filteredItems.size()>0)return filteredItems.get(0);
        else return null;
    }
}
