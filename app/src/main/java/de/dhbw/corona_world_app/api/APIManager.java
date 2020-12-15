package de.dhbw.corona_world_app.api;

import org.json.JSONArray;
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
    private Map<String, String> heroToPopMap = new HashMap<>();
    private Map<String, String> heroToGoogleMap = new HashMap<>();

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
        this.setMapData();
    }

    public List<Country> getDataWorld(){
        Logger.logD("getDataWorld","Getting Data for every Country");
        String url = heroURL;
        url += "/countries";

        List<Country> returnList = new ArrayList<>();
        String apiReturn = createAPICall(url);

        StringToCountryParser.parseFromHeroMultiCountry(apiReturn,returnList);
        Map<String, Long> popMap = getAllCountriesPopData();

        int cnt = 0;
        for (Country country:returnList) {
            if(popMap.containsKey(country.getName())) {
                country.setPopulation(popMap.get(country.getName()));
            } else if(heroToGoogleMap.containsKey(country.getName())) {
                country.setPopulation(popMap.get(heroToGoogleMap.get(country.getName())));
            } else {
                cnt+=1;
                //System.out.println("\""+country.getName()+"\"s popCount could not be set");
                Logger.logD("getDataWorld","country \""+country.getName()+"\" has no popCount\nINFO: Try adding an according entry into the <Name,Alias> Map");
            }
        }
        //System.out.println(popMap);
        //System.out.println("count of countries with no popCount: "+cnt);
        Logger.logD("getDataWorld","count of countries with no popCount: "+cnt);
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
                if(heroToPopMap.containsKey(isoCountry.toString())) {
                    attachString = heroToPopMap.get(isoCountry.toString());
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

    private Map<String,Long> getAllCountriesPopData(){
        String apiReturn = createAPICall("https://restcountries.eu/rest/v2/all");
        Map<String,Long> returnMap = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(apiReturn);
            for(int i = 0; i < jsonArray.length(); i++) {
                returnMap.put(jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getLong("population"));
            }
        } catch (JSONException e) {
            Logger.logE("ParsingException","Error parsing JSON: "+e);
        }
        return returnMap;
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

    private <Ke, Va> Map<Va,Ke> getReverseMap(Map<Ke,Va> inMap){
        Map<Va,Ke> reverseMap = new HashMap<>();
        for(Map.Entry<Ke, Va> entry : inMap.entrySet()){
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
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

    public void setMapData(){
        heroToPopMap.put("UnitedStates","USA");
        heroToGoogleMap.put("USA","United States of America");
        heroToGoogleMap.put("UK","United Kingdom of Great Britain and Northern Ireland");
        heroToGoogleMap.put("Russia","Russian Federation");
        heroToGoogleMap.put("Iran","Iran (Islamic Republic of)");
        heroToGoogleMap.put("Czechia","Czech Republic");
        heroToGoogleMap.put("UAE","United Arab Emirates");
        heroToGoogleMap.put("Bolivia","Bolivia (Plurinational State of)");
        heroToGoogleMap.put("Moldova","Moldova (Republic of)");
        heroToGoogleMap.put("Palestine","Palestine, State of");
        heroToGoogleMap.put("Venezuela","Venezuela (Bolivarian Republic of)");
        heroToGoogleMap.put("North Macedonia","Macedonia (the former Yugoslav Republic of)");
        heroToGoogleMap.put("S. Korea","Korea (Republic of)");
        heroToGoogleMap.put("Ivory Coast","Côte d'Ivoire");
        heroToGoogleMap.put("DRC","Congo (Democratic Republic of the)");
        heroToGoogleMap.put("Syria","Syrian Arab Republic");
        heroToGoogleMap.put("Eswatini","Swaziland");
        heroToGoogleMap.put("CAR","Central African Republic");
        //heroNameToPopNameMap.put("Channel Islands","");
        heroToGoogleMap.put("Vietnam","Viet Nam");
        //heroNameToPopNameMap.put("Sint Maarten","");
        heroToGoogleMap.put("Saint Martin","Saint Martin (French part)");
        heroToGoogleMap.put("Turks and Caicos","Turks and Caicos Islands");
        //heroNameToPopNameMap.put("Diamond Princess",""); //it's a ship
        heroToGoogleMap.put("Faeroe Islands","Faroe Islands");
        heroToGoogleMap.put("Tanzania","Tanzania, United Republic of");
        heroToGoogleMap.put("Caribbean Netherlands","Bonaire, Sint Eustatius and Saba");
        heroToGoogleMap.put("St. Barth","Saint Barthélemy");
        heroToGoogleMap.put("Brunei","Brunei Darussalam");
        heroToGoogleMap.put("St. Vincent Grenadines","Saint Vincent and the Grenadines");
        heroToGoogleMap.put("British Virgin Islands","Virgin Islands (British)");
        heroToGoogleMap.put("Laos","Lao People's Democratic Republic");
        //heroNameToPopNameMap.put("Vatican City",""); //not in Google Maps
        heroToGoogleMap.put("Falkland Islands","Falkland Islands (Malvinas)");
        heroToGoogleMap.put("Saint Pierre Miquelon","Saint Pierre and Miquelon");
        //heroNameToPopNameMap.put("MS Zaandam",""); //it's a ship too
    }
}
