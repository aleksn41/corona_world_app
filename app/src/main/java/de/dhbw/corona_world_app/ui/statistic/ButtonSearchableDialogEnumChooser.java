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
import android.widget.Button;
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
import java.util.Set;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;

public class ButtonSearchableDialogEnumChooser<T extends Enum<T>> extends androidx.appcompat.widget.AppCompatButton {

    private AlertDialog chooser;

    private String spinnerTitle = "";
    private String searchHint = "Type to search";
    private String clearText = "Clear All";

    private FilterableAdapter<T> adapter;
    private static final char ITEM_SEPARATOR = ',';

    //TODO dialog is initialized when fragment loads, if app too slow use lazy loading
    public ButtonSearchableDialogEnumChooser(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.EnumSpinnerAdapter);
        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.EnumSpinnerAdapter_hintText) {
                this.setHintText(a.getString(attr));
                spinnerTitle = this.getHintText();
                break;
            } else if (attr == R.styleable.EnumSpinnerAdapter_clearText) {
                this.setClearText(a.getString(attr));
            }
        }
        initDialog();
        a.recycle();
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

    public void setLimit(int limit, FilterableAdapter.LimitExceedListener<T> limitExceedListener) {
        adapter.setLimit(limit);
        adapter.setLimitExceedListener(limitExceedListener);
        chooser.setOnShowListener(dialog -> {
            Button selectAllButton=chooser.getButton(AlertDialog.BUTTON_NEUTRAL);
            boolean canSelectAll=adapter.getLimit() == -1 || adapter.getAllItems().size() <= adapter.getLimit();
            selectAllButton.setEnabled(canSelectAll);
            selectAllButton.setVisibility(canSelectAll?VISIBLE:INVISIBLE);
        });
    }


    public Set<T> getSelectedItems() {
        return adapter.getSelectedItems();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(spinnerTitle);

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.multispinner_dialog_recyclerview_search, null);
        builder.setView(view);

        final RecyclerView recyclerView = view.findViewById(R.id.alertSearchRecyclerView);
        adapter = new FilterableAdapter<>();
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

        builder.setNeutralButton(android.R.string.selectAll, (dialog, which) -> {
            adapter.selectAllItems();
            dialog.cancel();
        });

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.cancel();
        });

        builder.setNegativeButton(clearText, (dialog, which) -> {
            adapter.unSelectAllItems();
            dialog.cancel();
        });
        builder.setOnCancelListener(dialog -> setText(!adapter.anySelected() ? spinnerTitle : listOfStringToString(getSelectedItems().parallelStream().map(Enum::toString).collect(Collectors.toList()))));
        chooser = builder.create();
    }


    @Override
    public boolean performClick() {
        super.performClick();
        chooser.show();
        return true;
    }

    public void setItems(List<T> originalItems) {
        adapter.setItems(originalItems);
    }

    public void setSearchHint(String searchHint) {
        this.searchHint = searchHint;
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
}
