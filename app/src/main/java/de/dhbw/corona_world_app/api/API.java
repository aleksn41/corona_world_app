package de.dhbw.corona_world_app.api;

/**
 * This enum contains all necessary data for the usage of an API through the APIManager. The constants "getAllCountries" and "getOneCountry" contain the url snippets used to get the according
 * data from the API.
 *
 * @author Thomas Meier
 */
public enum API {
    HEROKU("https://coronavirus-19-api.herokuapp.com", "/countries", "/countries/", false, false, "Heroku"),
    RESTCOUNTRIES("https://restcountries.eu/rest/v2", "/all", "/alpha/", true, false, "RestCountries"),
    POSTMANAPI("https://api.covid19api.com", "/world/total", "/total/country/", true, true, "api.covid19api.com"),
    ARCGIS("https://opendata.arcgis.com", "/datasets/ef4b445a53c1406892257fe63129a8ea_0.geojson", null, false, false, "opendata.arcgis.com");

    private final String url;

    private final String name;

    private final String getAllCountries;

    private final String getOneCountry;

    private final boolean acceptsISOCode;

    private final boolean acceptsTimeFrames;

    API(String url, String getAllCountries, String getOneCountry, boolean acceptsISOCode, boolean acceptsTimeFrames, String name) {
        this.getOneCountry = getOneCountry;
        this.acceptsISOCode = acceptsISOCode;
        this.url = url;
        this.getAllCountries = getAllCountries;
        this.name = name;
        this.acceptsTimeFrames = acceptsTimeFrames;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getAllCountries() {
        return getAllCountries;
    }

    public String getOneCountry() {
        return getOneCountry;
    }

    public boolean acceptsISOCode() {
        return acceptsISOCode;
    }

    public boolean acceptsTimeFrames() {
        return acceptsTimeFrames;
    }
}
