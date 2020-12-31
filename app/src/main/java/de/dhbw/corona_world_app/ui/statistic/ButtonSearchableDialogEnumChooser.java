package de.dhbw.corona_world_app.ui.statistic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.R;

public class ButtonSearchableDialogEnumChooser<T extends Enum<T>> extends androidx.appcompat.widget.AppCompatButton {

    private AlertDialog chooser;

    private String dialogTitle = "";
    private String clearText = "Clear & Close";

    private FilterableAdapter<T> adapter;
    private static final char ITEM_SEPARATOR = ',';

    //TODO dialog is initialized when fragment loads, if app too slow use lazy loading
    public ButtonSearchableDialogEnumChooser(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.ButtonSearchableDialogEnumChooser);
        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ButtonSearchableDialogEnumChooser_hintText) {
                this.setDialogTitle(a.getString(attr));
                break;
            } else if (attr == R.styleable.ButtonSearchableDialogEnumChooser_clearText) {
                this.setClearText(a.getString(attr));
            }
        }
        initDialog();
        a.recycle();
    }


    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
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


    public List<T> getSelectedItems() {
        return new ArrayList<>(adapter.getSelectedItems());
    }

    public boolean anyItemSelected(){
        return adapter.anySelected();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(dialogTitle);

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.enum_dialog_recyclerview_search, null);
        builder.setView(view);

        final RecyclerView recyclerView = view.findViewById(R.id.alertSearchRecyclerView);
        adapter = new FilterableAdapter<>();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        final EditText editText = view.findViewById(R.id.alertSearchEditText);
        editText.setVisibility(VISIBLE);
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
        builder.setOnCancelListener(dialog -> setText(!adapter.anySelected() ? dialogTitle : listOfStringToString(adapter.getSelectedItems().parallelStream().map(Enum::toString).collect(Collectors.toList()))));
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
