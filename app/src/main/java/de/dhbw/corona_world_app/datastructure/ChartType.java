package de.dhbw.corona_world_app.datastructure;

/**
 * This Class contains all available chart types that can be displayed and their display names.
 *
 * @author Thomas Meier
 */
public enum ChartType {
    BAR("Bar-Chart"),
    PIE("Pie-Chart"),
    LINE("Line-Chart");

    String displayName;
    ChartType(String displayName){
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
