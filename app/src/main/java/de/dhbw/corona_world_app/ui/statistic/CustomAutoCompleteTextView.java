package de.dhbw.corona_world_app.ui.statistic;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This class is used to use functionality the {@link androidx.appcompat.widget.AppCompatAutoCompleteTextView} does not usually have
 * This includes showing suggestions when the user has not typed anything and minimizing the keyboard instead of the dropdown when back is pressed
 * @author Aleksandr Stankoski
 */
public class CustomAutoCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    public CustomAutoCompleteTextView(@NonNull Context context) {
        super(context);
    }

    public CustomAutoCompleteTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAutoCompleteTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            refreshAutoCompleteResults();
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)) {
                return true;
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
