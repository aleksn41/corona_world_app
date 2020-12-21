package de.dhbw.corona_world_app.ui.tools;


import android.view.ActionMode;

import java.util.ArrayList;

public interface StatisticCallDeleteInterface {
    void enterDeleteMode(ActionMode.Callback callback);
    void deleteItems(ArrayList<Integer> ItemIds);
}
