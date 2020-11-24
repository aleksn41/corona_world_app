package de.dhbw.corona_world_app.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIManager {

    private boolean cacheEnabled;

    private boolean longTermStorageEnabled;

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
    }

    public List<Country> getData(List<ISOCountry> countryList, List<Criteria> criteriaList, LocalDateTime[] timeFrame){
        List<Country> returnList = new ArrayList<>();
        String url = "https://coronavirus-19-api.herokuapp.com";
        url += "/countries";
        String apiReturn = createAPICall(url);


        Country country = new Country(countryList.get(0).toString());
        switch (criteriaList.get(0)){
            case DEATHS:country.setDeaths(1);
            case INFECTED:country.setInfected(1);
            case RECOVERED:country.setRecovered(1);
        }
        returnList.add(country);
        return returnList;
    }

    private String evaluateCriteria(Criteria criteria){
        String returnString = "";
        switch (criteria){
            case DEATHS:returnString = "deaths";
            case RECOVERED:returnString = "recovered";
            case INFECTED:returnString = "confirmed";
        }
        return returnString;
    }

    public String createAPICall(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()){
            Logger.logD("OkHTTP","Making GET-Request to "+ url);
            return response.body().string();
        } catch (IOException e) {
            Logger.logE("OkHTTPException","GET-Request "+ url +" failed");
        }
        return null;
    }

    public void enableCache(){
        cacheEnabled = true;
    }

    public void disableCache(){
        cacheEnabled = false;
    }

    public void enableLongTermStorage(){
        longTermStorageEnabled = true;
    }

    public void disableLongTermStorage(){
        longTermStorageEnabled = false;
    }

    public void disableLogsForTesting(){
        Logger.disableLogging();
    }
}
