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

    public static final int MAX_COUNTRY_LIST_SIZE = 10;

    private final ExecutorService service;

    public APIManager(boolean cacheEnabled, boolean longTermStorageEnabled){
        this.cacheEnabled = cacheEnabled;
        this.longTermStorageEnabled = longTermStorageEnabled;
        service = ThreadPoolHandler.getInstance();
    }

    public List<Country> getDataWorld(API api) throws Throwable {
        Logger.logD("APIManager.getDataWorld","Getting Data for every Country from api "+api.getName());

        List<Country> returnList;
        Future<String> future = service.submit(() -> createAPICall(api.getUrl() + api.getAllCountries()));

        int cnt = 0;

        try {
            String apiReturn = future.get();
            returnList = StringToCountryParser.parseFromHeroMultiCountry(apiReturn);

            Map<ISOCountry, Long> popMap = getAllCountriesPopData();

            for (Country country:returnList) {
                if(!Mapper.isInBlacklist(country.getName())) {
                    if (popMap.containsKey(ISOCountry.valueOf(country.getName()))) {
                        country.setPopulation(popMap.get(ISOCountry.valueOf(country.getName())));
                    } else {
                        cnt += 1;
                        Logger.logD("APIManager.getDataWorld", "country \"" + country.getName() + "\" has no popCount\nINFO: Try adding an entry into the according Map");
                    }
                }
            }
        } catch (ExecutionException e) {
            Logger.logE("APIManager.getDataWorld", "Error executing async call\n" + Arrays.toString(e.getStackTrace()));
            throw Objects.requireNonNull(e.getCause());
        } catch (InterruptedException e) {
            Logger.logE("APIManager.getDataWorld", "Interruption error\n" + Arrays.toString(e.getStackTrace()));
            throw e;
        }

        Logger.logD("APIManager.getDataWorld","count of countries with no popCount: "+cnt);
        return returnList;
    }

    public List<Country> getData(List<ISOCountry> countryList, List<Criteria> criteriaList, LocalDateTime[] timeFrame) throws Throwable {
        Logger.logD("APIManager.getData", "Getting data according to following parameters: " + countryList + " ; " + criteriaList + " ; " + Arrays.toString(timeFrame));

        List<Country> returnList = new ArrayList<>();
        List<Future<String>> futureCoronaData = new ArrayList<>();
        List<Future<Country>> futurePopData = new ArrayList<>();
        if (countryList.size() <= MAX_COUNTRY_LIST_SIZE) {
            for (ISOCountry isoCountry : countryList) {
                //make api-call
                //System.out.println("Call for " + isoCountry.name() + " starting now " + LocalDateTime.now());

                Future<String> future = service.submit(() -> {
                            String url = API.HEROKU.getUrl();
                            url += API.HEROKU.getOneCountry();
                            String attachString;
                            if (Mapper.isInReverseMap(API.HEROKU, isoCountry)) {
                                attachString = Mapper.mapISOCountryToName(API.HEROKU, isoCountry);
                                //System.out.println(attachString);
                            } else {
                                //System.out.println(isoCountry+" here");
                                attachString = Mapper.denormalizeISOCountryName(isoCountry.name());
                            }
                            url += attachString;
                            return createAPICall(url);
                        }
                );
                futureCoronaData.add(future);
                if (criteriaList.contains(Criteria.POPULATION)) {
                    Future<Country> future1 = service.submit(new Callable<Country>() {
                        @Override
                        public Country call() throws Exception {
                            return StringToCountryParser.parsePopCount(createAPICall(API.RESTCOUNTRIES.getUrl() + API.RESTCOUNTRIES.getOneCountry() + isoCountry.getISOCode()),isoCountry.name());
                        }
                    });
                    futurePopData.add(future1);
                }
                //System.out.println("Call for " + isoCountry.name() + " ending now " + LocalDateTime.now());
            }
            for (int i = 0; i < futureCoronaData.size(); i++){
                String currentString = futureCoronaData.get(i).get();
                Country country = StringToCountryParser.parseFromHeroOneCountry(currentString);
                country.setPopulation(futurePopData.get(i).get().getPopulation());
                returnList.add(country);
            }
        } else {
            throw new IllegalArgumentException("Input country list is too big, max allowed=" + MAX_COUNTRY_LIST_SIZE);
        }
        return returnList;
    }

    //Gets a map with ISOCountries mapped to a {@code long} population count gotten by the restcountries api.
    public Map<ISOCountry,Long> getAllCountriesPopData() throws Throwable {
        Logger.logD("APIManager.getAllCountriesPopData", "Getting population data...");
        Future<String> future = service.submit(() -> createAPICall(API.RESTCOUNTRIES.getUrl() + API.RESTCOUNTRIES.getAllCountries()));
        Map<ISOCountry, Long> returnMap = new HashMap<>();
        try {
            returnMap = StringToCountryParser.parseMultiPopCount(future.get());
        } catch (ExecutionException e) {
            Logger.logE("APIManager.getAllCountriesPopData", "Error executing async call\n" + Arrays.toString(e.getStackTrace()));
            throw Objects.requireNonNull(e.getCause());
        } catch (InterruptedException e) {
            Logger.logE("APIManager.getAllCountriesPopData", "Interruption error\n" + Arrays.toString(e.getStackTrace()));
            throw e;
        } catch (JSONException e) {
            Logger.logE("APIManager.getAllCountriesPopData", "Error parsing JSON\n" + Arrays.toString(e.getStackTrace()));
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
