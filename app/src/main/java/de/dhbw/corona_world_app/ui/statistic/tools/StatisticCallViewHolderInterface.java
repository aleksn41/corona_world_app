package de.dhbw.corona_world_app.ui.statistic.tools;

import de.dhbw.corona_world_app.ui.statistic.tools.StatisticCallAdapterItemOnActionCallback;

//TODO Change String to StatisticCall
public interface StatisticCallViewHolderInterface<T>{
    //Puts the Item Information into the holder
    void setItem(T Item, int ItemPosition, StatisticCallAdapterItemOnActionCallback onActionCallback);
}
