package de.dhbw.corona_world_app.datastructure;

import androidx.annotation.NonNull;

public enum Criteria {
    DEATHS("Deaths"),
    RECOVERED("Recovered"),
    INFECTED("Infected"),
    HEALTHY("Healthy"),
    POPULATION("Population"),
    ID_RATION("Infected Deaths Ratio"),
    IH_RATION("Infected Health Ratio");

    String displayName;

    Criteria(String displayName){
        this.displayName=displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
