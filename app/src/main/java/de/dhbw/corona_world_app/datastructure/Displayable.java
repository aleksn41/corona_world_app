package de.dhbw.corona_world_app.datastructure;

/**
 * Interface which specifies (through implementation) the type of an according Country-Object.
 *
 * @author Thomas Meier
 */
public interface Displayable {
    String getDisplayName();

    String getISOCode();

    int getFlagDrawableID();
}
