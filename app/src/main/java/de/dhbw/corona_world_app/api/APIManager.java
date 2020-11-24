package de.dhbw.corona_world_app.api;

import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String heroURL = "https://coronavirus-19-api.herokuapp.com";
    private Map<String, String> heroMap = new HashMap<>();

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
        heroMap.put("UnitedStates","USA");
    }

    public List<Country> getDataWorld(){
        Logger.logD("getDataWorld","Getting Data for every Country");
        String url = heroURL;
        url += "/countries";

        List<Country> returnList = new ArrayList<>();
        String apiReturn = createAPICall(url);

        try {
            StringToCountryParser.parseFromHeroMultiCountry(apiReturn,returnList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    public List<Country> getData(List<ISOCountry> countryList, List<Criteria> criteriaList, LocalDateTime[] timeFrame){
        Logger.logD("getData","Getting data according to following parameters: "+countryList+" ; "+criteriaList+" ; "+timeFrame);

        List<Country> returnList = new ArrayList<>();

        //building url
        for (ISOCountry isoCountry:countryList) {
            String url = heroURL;
            url += "/countries";

            //in/decrease countryList.size if necessary -> todo performance
            if (countryList != null) {
                String attachString = "";
                if(heroMap.containsKey(isoCountry.toString())) {
                    attachString = heroMap.get(isoCountry.toString());
                } else {
                    attachString = isoCountry.toString();
                }
                url += "/" + attachString;
            }

            //make api-call
            String apiReturn = createAPICall(url);

            //parse api-return into country and return
            Country country = new Country(isoCountry.toString());
            StringToCountryParser.parseFromHeroOneCountry(apiReturn, country);

            //check if popCount is to be shown
            if (criteriaList.contains(Criteria.POPULATION)) {
                StringToCountryParser.parsePopCount(createAPICall("https://restcountries.eu/rest/v2/name/" + isoCountry.getISOCode() + "?fullText=true"), country);
            }
            returnList.add(country);
        }

        return returnList;
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
