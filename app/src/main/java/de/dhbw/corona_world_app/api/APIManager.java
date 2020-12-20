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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIManager {

    private boolean cacheEnabled;

    private boolean longTermStorageEnabled;

    private Mapper mapper;

    private Map<String, String> heroToPopMap;
    private Map<String, String> heroToGoogleMap;

    private ExecutorService service;

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
        mapper = new Mapper();
        service = ThreadPoolHandler.getsInstance();
    }

    //todo update
    public List<Country> getDataWorld(APIs api){
        Logger.logD("getDataWorld","Getting Data for every Country from api "+api.getName());
        String url = api.getUrl();
        url += api.getGetAll();

        List<Country> returnList = new ArrayList<>();
        String apiReturn = createAPICall(url);

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
                //System.out.println("\""+country
                // .getName()+"\"s popCount could not be set");
                Logger.logD("getDataWorld","country \""+country.getName()+"\" has no popCount\nINFO: Try adding an according entry into the <Name,Alias> Map");
            }
        }
        //System.out.println(popMap);
        //System.out.println("count of countries with no popCount: "+cnt);
        Logger.logD("getDataWorld","count of countries with no popCount: "+cnt);
        return returnList;
    }

    //todo update
    public List<Country> getData(List<ISOCountry> countryList, List<Criteria> criteriaList, LocalDateTime[] timeFrame){
        Logger.logD("getData","Getting data according to following parameters: "+countryList+" ; "+criteriaList+" ; "+timeFrame);

        List<Country> returnList = new ArrayList<>();

        //building url
        for (ISOCountry isoCountry:countryList) {
            String url = APIs.HEROKU.getUrl();
            url += APIs.HEROKU.getGetOne();

            //in/decrease countryList.size if necessary -> todo performance
            if (countryList != null) {
                String attachString = "";
                if(heroToPopMap.containsKey(isoCountry.toString())) {
                    attachString = heroToPopMap.get(isoCountry.toString());
                } else {
                    attachString = isoCountry.toString();
                }
                url += attachString;
            }

            //make api-call
            String apiReturn = createAPICall(url);

            //parse api-return into country and return
            Country country = new Country(isoCountry.toString());
            StringToCountryParser.parseFromHeroOneCountry(apiReturn, country);

            //check if popCount is to be shown
            if (criteriaList.contains(Criteria.POPULATION)) {
                StringToCountryParser.parsePopCount(createAPICall(APIs.RESTCOUNTRIES.getUrl()+APIs.RESTCOUNTRIES.getGetOne()+isoCountry.getISOCode()), country);
            }
            returnList.add(country);
        }

        return returnList;
    }

    public Map<ISOCountry,Long> getAllCountriesPopData() {
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

    private String normalize(String countryName){
        return countryName.replace(",","").replace(" ","_");
    }

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

    public void disableLogsForTesting(){
        Logger.disableLogging();
    }
}
