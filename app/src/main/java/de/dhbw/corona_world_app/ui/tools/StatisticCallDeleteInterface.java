package de.dhbw.corona_world_app.ui.tools;


import android.view.ActionMode;

import java.util.ArrayList;
import java.util.Set;

public interface StatisticCallDeleteInterface {
    void enterDeleteMode(ActionMode.Callback callback);
    void deleteItems(Set<Integer> ItemIds);
}
