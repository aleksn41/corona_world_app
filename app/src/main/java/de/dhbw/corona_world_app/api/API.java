package de.dhbw.corona_world_app.api;

public enum API {
    HEROKU("https://coronavirus-19-api.herokuapp.com", "/countries", "/countries/", false, false, "Heroku"),
    RESTCOUNTRIES("https://restcountries.eu/rest/v2", "/all", "/alpha/", true, false, "RestCountries"),
    POSTMANAPI("https://api.covid19api.com", "/world/total", "/total/country/", true, true, "api.covid19api.com");

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
}
