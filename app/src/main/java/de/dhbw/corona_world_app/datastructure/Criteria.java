package de.dhbw.corona_world_app.datastructure;

/**
 * This class contains all available criteria that can be selected in the statistics tab and will be displayed in the BottomSheetView of a selected country in the map.
 *
 * @author Thomas Meier
 */
public enum Criteria {
    DEATHS("Deaths"),
    RECOVERED("Recovered"),
    INFECTED("Infected"),
    HEALTHY("Healthy"),
    ACTIVE("Active"),
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
