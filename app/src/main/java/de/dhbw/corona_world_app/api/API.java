package de.dhbw.corona_world_app.api;

public enum API {
    HEROKU("https://coronavirus-19-api.herokuapp.com", "/countries", "/countries/", false, "Heroku"),
    RESTCOUNTRIES("https://restcountries.eu/rest/v2", "/all", "/alpha/", true, "RestCountries");

    private final String url;

    private final String name;

    private final String getAllCountries;

    private final String getOneCountry;

    private final boolean acceptsISOCode;

    API(String url, String getAllCountries, String getOneCountry, boolean acceptsISOCode, String name) {
        this.getOneCountry = getOneCountry;
        this.acceptsISOCode = acceptsISOCode;
        this.url = url;
        this.getAllCountries = getAllCountries;
        this.name = name;
    }

    public String getOneCountry() {
        return getOneCountry;
    }

    public boolean acceptsISOCode() {
        return acceptsISOCode;
    }

    public String getAllCountries() {
        return getAllCountries;
    }

    public String getUrl(){
        return this.url;
    }

    public String getName() {
        return name;
    }
}
