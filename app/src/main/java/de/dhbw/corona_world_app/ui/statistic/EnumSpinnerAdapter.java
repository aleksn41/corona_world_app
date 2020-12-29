package de.dhbw.corona_world_app.ui.statistic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;

public class EnumSpinnerAdapter<T extends Enum<T>> extends androidx.appcompat.widget.AppCompatButton implements DialogInterface.OnCancelListener {

    private  AlertDialog chooser;

    private boolean highlightSelected = false;
    private int highlightColor = ContextCompat.getColor(getContext(), R.color.list_selected);
    private int textColor = Color.GRAY;
    private int limit = -1;
    private int selected = 0;
    private String spinnerTitle = "";
    private String searchHint = "Type to search";
    private String clearText = "Clear All";
    private List<T> originalItems=new ArrayList<>();
    private List<T> filteredItems;
    private boolean isShowSelectAllButton = false;

    private HashSet<T> itemsSelected=new HashSet<>();

    private MultiSpinnerListener<T> listener;
    private LimitExceedListener<T> limitListener;

    private FilterableAdapter adapter;

    private static final char ITEM_SEPARATOR = ',';

    public EnumSpinnerAdapter(Context context) {
        super(context);
        initDialog(originalItems);
    }

    public EnumSpinnerAdapter(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.EnumSpinnerAdapter);
        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.EnumSpinnerAdapter_hintText) {
                this.setHintText(a.getString(attr));
                spinnerTitle = this.getHintText();
                break;
            } else if (attr == R.styleable.EnumSpinnerAdapter_highlightSelected) {
                highlightSelected = a.getBoolean(attr, false);
            } else if (attr == R.styleable.EnumSpinnerAdapter_highlightColor) {
                highlightColor = a.getColor(attr, ContextCompat.getColor(getContext(), R.color.list_selected));
            } else if (attr == R.styleable.EnumSpinnerAdapter_textColor) {
                textColor = a.getColor(attr, Color.GRAY);
            } else if (attr == R.styleable.EnumSpinnerAdapter_clearText) {
                this.setClearText(a.getString(attr));
            }
        }

        //Log.i(TAG, "spinnerTitle: " + spinnerTitle);
        a.recycle();
        initDialog(originalItems);
    }

    public EnumSpinnerAdapter(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        initDialog(new ArrayList<>());
    }

    public interface MultiSpinnerListener<T> {
        void onItemsSelected(List<T> selectedItems);
    }

    public String getHintText() {
        return this.spinnerTitle;
    }

    public void setHintText(String hintText) {
        this.spinnerTitle = hintText;
    }

    public void setClearText(String clearText) {
        this.clearText = clearText;
    }

    public void setLimit(int limit, LimitExceedListener<T> listener) {
        this.limit = limit;
        this.limitListener = listener;
        isShowSelectAllButton = false; // if its limited, select all default false.
    }

    /*
    public List<T> getSelectedItems() {
        return items.parallelStream().filter(x->x.second).map(x->x.first).collect(Collectors.toList());
    }

    public List<Integer> getSelectedIds() {
        return IntStream.range(0, items.size()).parallel().boxed().filter(i -> items.get(i).second).collect(Collectors.toList());
    }
    */
    private void initDialog(List<T> items){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(spinnerTitle);

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.multispinner_dialog_recyclerview_search, null);
        builder.setView(view);

        final RecyclerView recyclerView = view.findViewById(R.id.alertSearchRecyclerView);


        adapter = new FilterableAdapter(getContext(), items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        final EditText editText = view.findViewById(R.id.alertSearchEditText);
        editText.setVisibility(VISIBLE);
        editText.setHint(searchHint);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });

        /*
        Added Select all Dialog Button.
         */
        if (isShowSelectAllButton && limit == -1) {
            builder.setNeutralButton(android.R.string.selectAll, (dialog, which) -> {
                itemsSelected=new HashSet<>(originalItems);
                dialog.cancel();
            });
        }

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            //Log.i(TAG, " ITEMS : " + items.size());
            dialog.cancel();
        });

        builder.setNegativeButton(clearText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemsSelected.clear();
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(this);
        chooser=builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

        String spinnerDisplay;
        if(itemsSelected.isEmpty()) spinnerDisplay=spinnerTitle;
        else spinnerDisplay=listOfStringToString(itemsSelected.parallelStream().map(Enum::toString).collect(Collectors.toList()));
        setText(spinnerDisplay);

        if (adapter != null)
            adapter.notifyDataSetChanged();
        }


    @Override
    public boolean performClick() {
        super.performClick();
        chooser.show();
        return true;
    }
    //TODO can user manually pre check items?
    public void setItems(List<T> originalItems) {
        this.originalItems = originalItems;
        initDialog(originalItems);
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
    }

    public boolean isShowSelectAllButton() {
        return isShowSelectAllButton;
    }

    public void setShowSelectAllButton(boolean showSelectAllButton) {
        isShowSelectAllButton = showSelectAllButton;
    }

    public interface LimitExceedListener<T> {
        void onLimitListener(Pair<T,Boolean> data);
    }

    private String listOfStringToString(List<String> list) {
        StringBuilder stringbuilder = new StringBuilder(list.size() * 4);
        for (int i = 0; i < list.size(); i++) {
            stringbuilder.append(list.get(i));
            stringbuilder.append(ITEM_SEPARATOR);
        }
        stringbuilder.setLength(stringbuilder.length() - 1);
        return stringbuilder.toString();
    }

    public class FilterableAdapter extends RecyclerView.Adapter<FilterableAdapter.ViewHolder> implements Filterable {

        final List<T> mOriginalValues; // Original Values
        final LayoutInflater inflater;
        List<T> arrayList;

        FilterableAdapter(Context context, List<T> arrayList) {
            this.arrayList = arrayList;
            this.mOriginalValues = arrayList;
            inflater = LayoutInflater.from(context);
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

            final T data = arrayList.get(position);
            boolean currentChecked=itemsSelected.contains(data);
            holder.textView.setText(data.toString());
            holder.checkBox.setChecked(currentChecked);

            holder.checkBox.setOnClickListener(v -> {
                if(itemsSelected.contains(data)){
                    itemsSelected.remove(data);
                    setMarkedItem(holder,false);
                 }
                else {
                    itemsSelected.add(data);
                    setMarkedItem(holder,true);
                }
            });

            setMarkedItem(holder,currentChecked);
        }

        private void setMarkedItem(ViewHolder holder, boolean mark){
            holder.itemView.setAlpha(mark?0.3f:1f);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    //this must be a List of type Pair<T,Boolean>
                    arrayList = (List<T>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if(constraint==null||constraint.length()==0){
                        results.values=arrayList;
                        results.count=arrayList.size();
                    }else {
                        List<T> filteredItems = originalItems.parallelStream().filter(p -> containsIgnoreCase(p.toString(), String.valueOf(constraint))).collect(Collectors.toList());
                        results.values = filteredItems;
                        results.count = filteredItems.size();
                    }
                    return results;
                }
            };
        }

        public boolean containsIgnoreCase(String str, String searchStr)     {
            if(str == null || searchStr == null) return false;

            final int length = searchStr.length();
            if (length == 0)
                return true;

            for (int i = str.length() - length; i >= 0; i--) {
                if (str.regionMatches(true, i, searchStr, 0, length))
                    return true;
            }
            return false;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            CheckBox checkBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView=itemView.findViewById(R.id.alertTextView);
                checkBox=itemView.findViewById(R.id.alertCheckbox);
            }
        }
    }
}
