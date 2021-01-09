package de.dhbw.corona_world_app.api;

public enum API {
    HEROKU("https://coronavirus-19-api.herokuapp.com", "/countries", "/countries/", false, false , null, "Heroku"),
    RESTCOUNTRIES("https://restcountries.eu/rest/v2", "/all", "/alpha/", true, false, null, "RestCountries"),
    POSTMANAPI("https://api.covid19api.com", "/world/total","/total/country/",true, true, new PostmanConverter(), "api.covid19api.com");

    private final String url;

    private final String name;

    private final String getAllCountries;

    private final String getOneCountry;

    private final APIDateTimeConverter timeConverter;

    private final boolean acceptsISOCode;

    private final boolean acceptsOneCountryTime;

    API(String url, String getAllCountries, String getOneCountry, boolean acceptsISOCode, boolean acceptsOneCountryTime, APIDateTimeConverter timeConverter, String name) {
        this.getOneCountry = getOneCountry;
        this.acceptsISOCode = acceptsISOCode;
        this.url = url;
        this.getAllCountries = getAllCountries;
        this.name = name;
        this.acceptsOneCountryTime = acceptsOneCountryTime;
        this.timeConverter = timeConverter;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getGetAllCountries() {
        return getAllCountries;
    }

    public String getGetOneCountry() {
        return getOneCountry;
    }

    public APIDateTimeConverter getTimeConverter() {
        return timeConverter;
    }

    public boolean isAcceptsISOCode() {
        return acceptsISOCode;
    }

    public boolean isAcceptsOneCountryTime() {
        return acceptsOneCountryTime;
    }
}
