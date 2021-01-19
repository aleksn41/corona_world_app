package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import com.hootsuite.nachos.NachoTextView;

public class CustomNachoTextView extends NachoTextView {
    public CustomNachoTextView(Context context) {
        super(context);
    }

    public CustomNachoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNachoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused){
            refreshAutoCompleteResults();
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)){
                return true;
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }
}