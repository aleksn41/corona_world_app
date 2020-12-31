package de.dhbw.corona_world_app.datastructure;

import androidx.annotation.NonNull;

public enum Criteria {
    DEATHS,
    RECOVERED,
    INFECTED,
    HEALTHY,
    POPULATION,
    ID_RATION,
    IH_RATION;


    @NonNull
    @Override
    public String toString() {
        return super.toString().replace("_"," ");
    }
}
