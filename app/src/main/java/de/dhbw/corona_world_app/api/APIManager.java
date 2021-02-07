package de.dhbw.corona_world_app.api;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.TimeFramedCountry;
import de.dhbw.corona_world_app.datastructure.displayables.GermanyState;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class APIManager {

    public static final int MAX_COUNTRY_LIST_SIZE = 10;

    public static final int MAX_GET_DATA_WORLD_CACHE_AGE = 15; //time-unit is minutes

    public static final int MAX_LIVE_STATISTICS_CACHE_AGE = 15; //time-unit is minutes

    public static final int MAX_CACHED_STATISTIC_CALLS = 50;

    private static boolean cacheEnabled;

    private static boolean longTermStorageEnabled;

    private static final String TAG = APIManager.class.getName();

    private static final ExecutorService service = ThreadPoolHandler.getInstance();

    public static void setSettings(boolean cacheEnabled, boolean longTermStorageEnabled) {
        APIManager.cacheEnabled = cacheEnabled;
        APIManager.longTermStorageEnabled = longTermStorageEnabled;
    }

    //gets the data of the whole world through the specified api
    public static List<Country<ISOCountry>> getDataWorld(@NonNull API api) throws ExecutionException, JSONException, InterruptedException, IOException {
        Logger.logV(TAG, "Getting data for every Country from api " + api.getName() + "...");
        List<Country<ISOCountry>> returnList;
        try {
            Future<String> future = service.submit(() -> createAPICall(api.getUrl() + api.getAllCountries()));

            int cnt = 0;

            String apiReturn = future.get();
            returnList = StringToCountryParser.parseFromHeroMultiCountry(apiReturn);

            Map<ISOCountry, Long> popMap = getAllCountriesPopData();

            for (Country<ISOCountry> country : returnList) {
                ISOCountry isoCountry = country.getName();
                if (country.getName() != null && !Mapper.isInBlacklist(isoCountry.name())) {
                    if (popMap.containsKey(isoCountry)) {
                        country.setPopulation(popMap.get(isoCountry));
                    } else {
                        cnt += 1;
                        Logger.logD("APIManager.getDataWorld", "country \"" + isoCountry.name() + "\" has no popCount\nINFO: Try adding an entry into the according Map");
                    }
                }
            }

            Logger.logD(TAG, "Count of countries with no popCount: " + cnt);
            returnList = returnList.stream().filter(c -> c.getName() != null).collect(Collectors.toList());
            Logger.logV(TAG, "Returning data list...");
        } catch (ExecutionException e){
            if(e.getCause() instanceof IOException){
                throw (IOException) e.getCause();
            } else {
                throw e;
            }
        }
        return returnList;
    }

    public static List<Country<GermanyState>> getDataGermany(@NonNull API api) throws ExecutionException, InterruptedException, JSONException, IOException {
        Logger.logV(TAG, "Getting data for every state of germany...");

        List<Country<GermanyState>> returnList;
        try {
            Future<String> future = service.submit(() -> createAPICall(api.getUrl() + api.getAllCountries()));

            String apiReturn = future.get();
            returnList = StringToCountryParser.parseFromArcgisMultiCountry(apiReturn);

            Logger.logV(TAG, "Returning data list...");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            } else {
                throw e;
            }
        }

        return returnList;
    }

    //this method creates one/multiple async calls to get the specified country's/countries' data and returns it through a list of country-objects
    public static List<Country<ISOCountry>> getData(@NonNull List<ISOCountry> countryList, @NonNull List<Criteria> criteriaList) throws IllegalArgumentException, ExecutionException, InterruptedException {
        Logger.logV(TAG, "Getting data according to following parameters: " + countryList + " ; " + criteriaList);
        List<Country<ISOCountry>> returnList = new ArrayList<>();
        List<Future<String>> futureCoronaData = new ArrayList<>();
        List<Future<Country<ISOCountry>>> futurePopData = new ArrayList<>();

        boolean popNeeded = criteriaList.contains(Criteria.POPULATION) || criteriaList.contains(Criteria.IH_RATION) || criteriaList.contains(Criteria.HEALTHY);

        if (countryList.size() <= MAX_COUNTRY_LIST_SIZE) {
            for (ISOCountry isoCountry : countryList) {
                Future<String> future = service.submit(() -> {
                            String url = API.HEROKU.getUrl();
                            url += API.HEROKU.getOneCountry();
                            String attachString;
                            if (Mapper.isInReverseMap(API.HEROKU, isoCountry)) {
                                attachString = Mapper.mapISOCountryToName(API.HEROKU, isoCountry);
                            } else {
                                attachString = Mapper.denormalizeISOCountryName(isoCountry.name());
                            }
                            url += attachString;
                            return createAPICall(url);
                        }
                );
                futureCoronaData.add(future);
                if (popNeeded) {
                    Future<Country<ISOCountry>> future1 = service.submit(() -> StringToCountryParser.parsePopCount(createAPICall(API.RESTCOUNTRIES.getUrl() + API.RESTCOUNTRIES.getOneCountry() + isoCountry.getISOCode()), isoCountry.name()));
                    futurePopData.add(future1);
                }
            }
            Logger.logV(TAG, "All requests have been sent...");
            for (int i = 0; i < futureCoronaData.size(); i++) {
                String currentString = futureCoronaData.get(i).get();
                Country<ISOCountry> country = StringToCountryParser.parseFromHeroOneCountry(currentString);
                if (popNeeded) country.setPopulation(futurePopData.get(i).get().getPopulation());
                returnList.add(country);
            }
            Logger.logV(TAG, "Country-List finished constructing...");
        } else {
            Logger.logE(TAG, "Throwing IllegalArgumentException! MAX_COUNTRY_LIST_SIZE has been exceeded!");
            throw new IllegalArgumentException("Input country list is too big, max allowed " + MAX_COUNTRY_LIST_SIZE + "!");
        }
        return returnList;
    }

    public static List<TimeFramedCountry> getData(@NonNull List<ISOCountry> countryList, @NonNull List<Criteria> criteriaList, LocalDate startDate, LocalDate endDate) throws ExecutionException, InterruptedException, JSONException, TooManyRequestsException, UnavailableException {
        Logger.logV(TAG, "Getting data according to following parameters: " + countryList + " ; " + criteriaList);
        if (endDate != null && (startDate == null || endDate.isBefore(startDate)))
            throw new IllegalArgumentException("Ending date is before starting date!");
        List<TimeFramedCountry> returnList = new ArrayList<>();
        List<Future<String>> futureCoronaData = new ArrayList<>();
        List<Future<Country<ISOCountry>>> futurePopData = new ArrayList<>();
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = LocalDate.now();

        final LocalDate finalStartDate;
        final LocalDate finalEndDate = endDate;

        //this is only needed because the api cannot handle when start and end date are equal and returns all dates' data
        boolean startAndEndEqual = startDate.equals(endDate);
        if (startAndEndEqual) {
            finalStartDate = startDate.minusDays(1);
        } else {
            finalStartDate = startDate;
        }

        boolean popNeeded = criteriaList.contains(Criteria.POPULATION) || criteriaList.contains(Criteria.IH_RATION) || criteriaList.contains(Criteria.HEALTHY);
        if (startAndEndEqual && startDate.equals(LocalDate.now())) {
            List<Country<ISOCountry>> countries = getData(countryList, criteriaList);
            List<TimeFramedCountry> timeframedCountries = new ArrayList<>();
            for (Country<ISOCountry> country : countries) {
                TimeFramedCountry countryToAdd = new TimeFramedCountry();
                countryToAdd.setInfected(new int[]{country.getInfected()});
                countryToAdd.setDates(new LocalDate[]{startDate});
                countryToAdd.setDeaths(new int[]{country.getDeaths()});
                countryToAdd.setRecovered(new int[]{country.getRecovered()});
                countryToAdd.setPop_inf_ratio(new double[1]);
                countryToAdd.setActive(new int[]{country.getActive()});
                countryToAdd.setPopulation(country.getPopulation());
                countryToAdd.setCountry(country.getName());
                timeframedCountries.add(countryToAdd);
            }
            return timeframedCountries;
        } else {
            if (countryList.size() <= MAX_COUNTRY_LIST_SIZE) {
                for (ISOCountry isoCountry : countryList) {
                    Future<String> future = service.submit(() -> {
                                String url = API.POSTMANAPI.getUrl();
                                url += API.POSTMANAPI.getOneCountry();
                                url += isoCountry.getISOCode();
                                url += getFormattedTimeFrameURLSnippet(API.POSTMANAPI, finalStartDate, finalEndDate);
                                return createAPICall(url);
                            }
                    );
                    futureCoronaData.add(future);

                    if (popNeeded) {
                        Future<Country<ISOCountry>> future1 = service.submit(() -> StringToCountryParser.parsePopCount(createAPICall(API.RESTCOUNTRIES.getUrl() + API.RESTCOUNTRIES.getOneCountry() + isoCountry.getISOCode()), isoCountry.name()));
                        futurePopData.add(future1);
                    }
                }
                Logger.logV(TAG, "All requests have been sent...");
                for (int i = 0; i < futureCoronaData.size(); i++) {
                    String currentString = futureCoronaData.get(i).get();
                    TimeFramedCountry country = StringToCountryParser.parseFromPostmanOneCountryWithTimeFrame(currentString, countryList.get(i), startAndEndEqual);
                    if (popNeeded)
                        country.setPopulation(futurePopData.get(i).get().getPopulation());
                    returnList.add(country);
                }
                Logger.logV(TAG, "Country-List finished constructing...");
            } else {
                Logger.logE(TAG, "Throwing IllegalArgumentException! MAX_COUNTRY_LIST_SIZE has been exceeded!");
                throw new IllegalArgumentException("Input country list is too big, max allowed " + MAX_COUNTRY_LIST_SIZE + "!");
            }
            return returnList;
        }
    }

    //Gets a map with ISOCountries mapped to a {@code long} population count gotten by the restcountries api.
    public static Map<ISOCountry, Long> getAllCountriesPopData() throws ExecutionException, InterruptedException, JSONException {
        Logger.logV(TAG, "Getting population data...");
        Future<String> future = service.submit(() -> createAPICall(API.RESTCOUNTRIES.getUrl() + API.RESTCOUNTRIES.getAllCountries()));
        return StringToCountryParser.parseMultiPopCount(future.get());
    }

    //creates a GET-Call to an url and returns the {@code String} body
    public static String createAPICall(@NonNull String url) throws IOException {
        Logger.logV(TAG, "Making api call to " + url + " ...");
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        return Objects.requireNonNull(client.newCall(request).execute().body()).string();
    }

    public static boolean pingGoogleDNS() throws IOException {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(10000);
    }

    private static String getFormattedTimeFrameURLSnippet(@NonNull API api, @NonNull LocalDate from, @NonNull LocalDate to) {
        if (!api.acceptsTimeFrames())
            throw new IllegalArgumentException("The given api \"" + api.getName() + "\" does not support time frames!");
        if (api == API.POSTMANAPI) {
            return "?from=" + LocalDateTime.of(from, LocalTime.MIDNIGHT).format(DateTimeFormatter.ISO_DATE_TIME) + "&to=" + LocalDateTime.of(to, LocalTime.MIDNIGHT).format(DateTimeFormatter.ISO_DATE_TIME);
        }
        throw new IllegalArgumentException("Given API has not yet been implemented to use time frames!");
    }

    public static boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public static boolean isLongTermStorageEnabled() {
        return longTermStorageEnabled;
    }

    public static void enableCache() {
        APIManager.cacheEnabled = true;
    }

    public static void disableCache() {
        APIManager.cacheEnabled = false;
    }

    public static void enableLongTermStorage() {
        APIManager.longTermStorageEnabled = true;
    }

    public static void disableLongTermStorage() {
        APIManager.longTermStorageEnabled = false;
    }

    //disables logs for testing
    public static void disableLogsForTesting() {
        Logger.disableLogging();
    }
}
