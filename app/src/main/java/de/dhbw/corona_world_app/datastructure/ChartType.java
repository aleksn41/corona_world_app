package de.dhbw.corona_world_app.datastructure;

public enum ChartType {
    BAR("Bar-Chart"),
    PIE("Pie-Chart"),
    LINE("Line-Chart");

    final String displayName;
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
