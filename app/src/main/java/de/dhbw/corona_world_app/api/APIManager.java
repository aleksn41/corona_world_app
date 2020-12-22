package de.dhbw.corona_world_app.api;

import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public static final int MAX_COUNTRY_LIST_SIZE = 5;

    private final ExecutorService service;

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
        Mapper.init();
        service = ThreadPoolHandler.getsInstance();
    }

    public List<Country> getDataWorld(API api) throws IOException {
        Logger.logD("getDataWorld","Getting Data for every Country from api "+api.getName());

        List<Country> returnList = new ArrayList<>();
        Future<String> future = service.submit(() -> createAPICall(api.getUrl() + api.getAllCountries()));

        int cnt = 0;

        try {
            String apiReturn = future.get();
            StringToCountryParser.parseFromHeroMultiCountry(apiReturn, returnList);

            Map<ISOCountry, Long> popMap = getAllCountriesPopData();

            for (Country country:returnList) {
                if(!Mapper.isInBlacklist(country.getName())) {
                    if (popMap.containsKey(ISOCountry.valueOf(country.getName()))) {
                        country.setPopulation(popMap.get(ISOCountry.valueOf(country.getName())));
                    } else {
                        cnt += 1;
                        Logger.logD("getDataWorld", "country \"" + country.getName() + "\" has no popCount\nINFO: Try adding an entry into the according Map");
                    }
                }
            }
        } catch (ExecutionException e) {
            Logger.logE("getDataWorld", "Error executing async call\n" + Arrays.toString(e.getStackTrace()));
            throw (IOException) Objects.requireNonNull(e.getCause());
        } catch (InterruptedException e) {
            Logger.logE("getDataWorld", "Interruption error\n" + Arrays.toString(e.getStackTrace()));
        }

        Logger.logD("getDataWorld","count of countries with no popCount: "+cnt);
        return returnList;
    }

    //todo performance
    public List<Country> getData(List<ISOCountry> countryList, List<Criteria> criteriaList, LocalDateTime[] timeFrame) throws IOException {
        Logger.logD("getData","Getting data according to following parameters: "+countryList+" ; "+criteriaList+" ; "+ Arrays.toString(timeFrame));

        List<Country> returnList = new ArrayList<>();
        if (countryList.size() <= MAX_COUNTRY_LIST_SIZE) {
            for (ISOCountry isoCountry : countryList) {

                //make api-call
                Future<String> future = service.submit(() -> {
                    String url = API.HEROKU.getUrl();
                    url += API.HEROKU.getOneCountry();

                    String attachString;
                    if (Mapper.isInReverseMap(API.HEROKU, isoCountry)) {
                        attachString = Mapper.mapISOCountryToName(API.HEROKU, isoCountry);
                    } else {
                        attachString = isoCountry.toString();
                    }
                    url += attachString;
                    return createAPICall(url);
                }
                );
                Country country = new Country(isoCountry.toString());
                try {
                    String apiReturn = future.get();
                    //parse api-return into country and return

                    StringToCountryParser.parseFromHeroOneCountry(apiReturn, country);

                    //check if popCount is to be shown
                    if (criteriaList.contains(Criteria.POPULATION)) {
                        Future<String> future2 = service.submit(() -> createAPICall(API.RESTCOUNTRIES.getUrl() + API.RESTCOUNTRIES.getOneCountry() + isoCountry.getISOCode()));
                        String apiReturn2 = future2.get();
                        StringToCountryParser.parsePopCount(apiReturn2, country);
                    }
                } catch (ExecutionException e) {
                    Logger.logE("getData", "Error executing async call\n" + Arrays.toString(e.getStackTrace()));
                    throw (IOException) Objects.requireNonNull(e.getCause());
                } catch (InterruptedException e) {
                    Logger.logE("getData", "Interruption error\n" + Arrays.toString(e.getStackTrace()));
                }
                returnList.add(country);
            }
        } else {
            throw new IllegalArgumentException("Input country list is too big, max allowed="+MAX_COUNTRY_LIST_SIZE);
        }
        return returnList;
    }

    //Gets a map with ISOCountries mapped to a {@code long} population count gotten by the restcountries api.
    public Map<ISOCountry,Long> getAllCountriesPopData() throws IOException {
        Logger.logD("getAllCountriesPopData","Getting population data...");
        Future<String> future = service.submit(() -> createAPICall(API.RESTCOUNTRIES.getUrl()+ API.RESTCOUNTRIES.getAllCountries()));
        Map<ISOCountry, Long> returnMap = new HashMap<>();
        try {
            returnMap = StringToCountryParser.parseMultiPopCount(future.get());
        } catch (ExecutionException e) {
            Logger.logE("getAllCountriesPopData", "Error executing async call\n" + Arrays.toString(e.getStackTrace()));
            throw (IOException) Objects.requireNonNull(e.getCause());
        } catch (InterruptedException e) {
            Logger.logE("getAllCountriesPopData", "Interruption error\n" + Arrays.toString(e.getStackTrace()));
        } catch (JSONException e) {
            Logger.logE("getAllCountriesPopData", "Error parsing JSON\n" + Arrays.toString(e.getStackTrace()));
        }
        return returnMap;
    }

    //creates a GET-Call to an url and returns the {@code String} body
    public String createAPICall(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        String toReturn;
        toReturn = Objects.requireNonNull(client.newCall(request).execute().body()).string();
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
