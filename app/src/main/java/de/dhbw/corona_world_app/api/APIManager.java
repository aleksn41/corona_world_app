package de.dhbw.corona_world_app.api;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.ISOCountry;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class APIManager {

    private boolean cacheEnabled;

    private boolean longTermStorageEnabled;

    private Mapper mapper;

    public static final int MAX_COUNTRY_LIST_SIZE = 5;

    private Map<String, String> heroToPopMap;
    private Map<String, String> heroToGoogleMap;

    private ExecutorService service;

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
        mapper = new Mapper();
        service = ThreadPoolHandler.getsInstance();
    }

    //todo fix mapping
    public List<Country> getDataWorld(APIs api){
        Logger.logD("getDataWorld","Getting Data for every Country from api "+api.getName());


        List<Country> returnList = new ArrayList<>();
        Future future = service.submit(new Callable<String>() {
                                           @Override
                                           public String call() throws Exception {
                                               String url = api.getUrl();
                                               url += api.getGetAll();
                                               return createAPICall(url);
                                           }
                                       }
        );
        String apiReturn = null;
        try {
            apiReturn = future.get().toString();
        } catch (ExecutionException e) {
            Logger.logE("getDataWorld", "Error executing async call\n" + e.getStackTrace());
        } catch (InterruptedException e) {
            Logger.logE("getDataWorld", "Interruption error\n" + e.getStackTrace());
        }

        StringToCountryParser.parseFromHeroMultiCountry(apiReturn,returnList);

        Map<ISOCountry, Long> popMap = getAllCountriesPopData();

        int cnt = 0;
        for (Country country:returnList) {
            if(popMap.containsKey(country.getName())) {
                country.setPopulation(popMap.get(country.getName()));
            } else if(heroToGoogleMap.containsKey(country.getName())) {
                country.setPopulation(popMap.get(heroToGoogleMap.get(country.getName())));
            } else {
                cnt+=1;
                Logger.logD("getDataWorld","country \""+country.getName()+"\" has no popCount\nINFO: Try adding an entry into the according Map");
            }
        }
        Logger.logD("getDataWorld","count of countries with no popCount: "+cnt);
        return returnList;
    }

    //todo mapping
    public List<Country> getData(List<ISOCountry> countryList, List<Criteria> criteriaList, LocalDateTime[] timeFrame){
        Logger.logD("getData","Getting data according to following parameters: "+countryList+" ; "+criteriaList+" ; "+timeFrame);

        List<Country> returnList = new ArrayList<>();
        if(countryList.size() <= MAX_COUNTRY_LIST_SIZE) {
            for (ISOCountry isoCountry : countryList) {

                //make api-call
                Future future = service.submit(new Callable<String>() {
                                                   @Override
                                                   public String call() throws Exception {
                                                       String url = APIs.HEROKU.getUrl();
                                                       url += APIs.HEROKU.getGetOne();

                                                       //in/decrease countryList.size if necessary -> todo performance
                                                       if (countryList != null) {
                                                           String attachString = "";
                                                           if (heroToPopMap.containsKey(isoCountry.toString())) {
                                                               attachString = heroToPopMap.get(isoCountry.toString());
                                                           } else {
                                                               attachString = isoCountry.toString();
                                                           }
                                                           url += attachString;
                                                       }
                                                       return createAPICall(url);
                                                   }
                                               }
                );
                String apiReturn = null;
                try {
                    apiReturn = future.get().toString();
                } catch (ExecutionException e) {
                    Logger.logE("getData", "Error executing async call\n" + e.getStackTrace());
                } catch (InterruptedException e) {
                    Logger.logE("getData", "Interruption error\n" + e.getStackTrace());
                }

                //parse api-return into country and return
                Country country = new Country(isoCountry.toString());
                StringToCountryParser.parseFromHeroOneCountry(apiReturn, country);

                //check if popCount is to be shown
                if (criteriaList.contains(Criteria.POPULATION)) {
                    Future future2 = service.submit(new Callable<String>() {
                                                        @Override
                                                        public String call() throws Exception {
                                                            return createAPICall(APIs.RESTCOUNTRIES.getUrl() + APIs.RESTCOUNTRIES.getGetOne() + isoCountry.getISOCode());
                                                        }
                                                    }
                    );
                    String apiReturn2 = null;
                    try {
                        apiReturn2 = future2.get().toString();
                    } catch (ExecutionException e) {
                        Logger.logE("getData", "Error executing async call\n" + e.getStackTrace());
                    } catch (InterruptedException e) {
                        Logger.logE("getData", "Interruption error\n" + e.getStackTrace());
                    }
                    StringToCountryParser.parsePopCount(apiReturn2, country);
                }
                returnList.add(country);
            }
        } else {
            throw new IllegalArgumentException("Input country list is too big, max allowed="+MAX_COUNTRY_LIST_SIZE);
        }
        return returnList;
    }

    //Gets a map with ISOCountries mapped to a {@code long} population count gotten by the restcountries api.
    public Map<ISOCountry,Long> getAllCountriesPopData() {
        Logger.logD("getAllCountriesPopData","Getting population data...");
        Future future = service.submit(new Callable<String>() {
                                           @Override
                                           public String call() throws Exception {
                                               return createAPICall(APIs.RESTCOUNTRIES.getUrl()+APIs.RESTCOUNTRIES.getGetAll());
                                           }
                                       }
        );
        Map<ISOCountry,Long> returnMap = new HashMap<>();
        try {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(future.get().toString());
            } catch (ExecutionException e) {
                Logger.logE("getAllCountriesPopData","Error executing async call\n"+e.getStackTrace());
            } catch (InterruptedException e) {
                Logger.logE("getAllCountriesPopData","Interruption error\n"+e.getStackTrace());
            }
            for(int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                if(mapper.isInMap(APIs.RESTCOUNTRIES,name)) {
                    returnMap.put(mapper.mapNameToISOCountry(APIs.RESTCOUNTRIES,name), jsonArray.getJSONObject(i).getLong("population"));
                } else {
                    String normalizedName = normalize(name);
                    if(!mapper.isInBlacklist(name)) {
                        returnMap.put(ISOCountry.valueOf(normalizedName), jsonArray.getJSONObject(i).getLong("population"));
                    }
                }
            }
        } catch (JSONException e) {
            Logger.logE("getAllCountriesPopData","Error parsing JSON\n"+e.getStackTrace());
        }
        return returnMap;
    }

    //normalizes a country name by removing commas and replacing spaces with underscores
    private String normalize(String countryName){
        return countryName.replace(",","").replace(" ","_");
    }

    //creates a GET-Call to an url and returns the {@code String} body
    public String createAPICall(String url) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        String toReturn = "";
        try {
            toReturn = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            Logger.logE("createAPICall","Error executing call "+url+"\n"+e.getStackTrace());
        }
        return toReturn;
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

    //disables logs for testing
    public void disableLogsForTesting(){
        Logger.disableLogging();
    }
}
