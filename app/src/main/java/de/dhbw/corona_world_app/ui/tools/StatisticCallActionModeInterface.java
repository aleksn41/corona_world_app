package de.dhbw.corona_world_app.ui.tools;


import android.view.ActionMode;

import java.util.Set;

public interface StatisticCallActionModeInterface {
    void enterActionMode(ActionMode.Callback callback);

    void deleteItems(Set<Integer> ItemIds);

    void favouriteItems(Set<Integer> ItemIds);
}
