package de.dhbw.corona_world_app.api;

public enum APIs {
    HEROKU("https://coronavirus-19-api.herokuapp.com", "/countries", "/countries/", false, "Heroku"),
    RESTCOUNTRIES("https://restcountries.eu/rest/v2", "/all", "/alpha/", true, "RestCountries");

    private String url;

    private String name;

    private String getAll;

    private String getOne;

    private boolean acceptsISO;

    APIs(String url, String getAll, String getOne, boolean acceptsISO, String name) {
        this.url = url;
        this.getAll = getAll;
        this.name = name;
    }

    public String getGetOne() {
        return getOne;
    }

    public boolean isAcceptsISO() {
        return acceptsISO;
    }

    public String getGetAll() {
        return getAll;
    }

    public String getUrl(){
        return this.url;
    }

    public String getName() {
        return name;
    }
}
