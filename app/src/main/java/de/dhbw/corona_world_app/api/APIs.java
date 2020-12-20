package de.dhbw.corona_world_app.api;

public enum APIs {
    HEROKU("https://coronavirus-19-api.herokuapp.com", "/countries", "Heroku"),
    RESTCOUNTRIES("https://restcountries.eu/rest/v2", "/all", "RestCountries");

    private String url;

    private String name;

    private String getAll;

    APIs(String url, String getAll, String name) {
        this.url = url;
        this.getAll = getAll;
        this.name = name;
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
