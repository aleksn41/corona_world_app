package de.dhbw.corona_world_app.datastructure.displayables;

import de.dhbw.corona_world_app.datastructure.Displayable;

public enum GermanyState implements Displayable {
    BADEN_WUERTTEMBERG("DE-BW","Baden-Wuerttemberg"),
    BAYERN("DE-BY","Bayern"),
    BERLIN("DE-BE","Berlin"),
    BRANDENBURG("DE-BB","Brandenburg"),
    BREMEN("DE-HB","Bremen"),
    HAMBURG("DE-HH","Hamburg"),
    HESSEN("DE-HE","Hessen"),
    MECKLENBURG_VORPOMMERN("DE-MV","Mecklenburg-Vorpommern"),
    NIEDERSACHSEN("DE-NI","Niedersachsen"),
    NORDRHEIN_WESTFALEN("DE-NW","Nordrhein-Westfalen"),
    RHEINLAND_PFALZ("DE-RP","Rheinland-Pfalz"),
    SAARLAND("DE-SL","Saarland"),
    SACHSEN("DE-SN","Sachsen"),
    SACHSEN_ANHALT("DE-ST","Sachsen-Anhalt"),
    SCHLESWIG_HOLSTEIN("DE-SH","Schleswig-Holstein"),
    THUERINGEN("DE-TH","Thueringen"),
    ;

    private final String displayName;

    private final String isoCode;

    GermanyState(String isoCode,String displayName) {
        this.displayName = displayName;
        this.isoCode = isoCode;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getISOCode() {
        return this.isoCode;
    }

    @Override
    public int getFlagDrawableID() {
        return com.michaelfotiadis.androidflags.R.drawable.ic_list_country_de;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
