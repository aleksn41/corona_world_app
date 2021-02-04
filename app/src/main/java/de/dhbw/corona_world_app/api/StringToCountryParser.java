package de.dhbw.corona_world_app.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.displayables.GermanyState;
import de.dhbw.corona_world_app.datastructure.TimeFramedCountry;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

public class StringToCountryParser {

    private static final String TAG = StringToCountryParser.class.getSimpleName();

    public static List<Country> parseFromArcgisMultiCountry(String toParse) throws JSONException {
        List<Country> countries = new ArrayList<>();
        JSONObject object = new JSONObject(toParse);
        JSONArray jsonArray = object.getJSONArray("features");
        for (int i = 0; i < jsonArray.length(); i++) {
            Country country = new Country();
            JSONObject properties = jsonArray.getJSONObject(i).getJSONObject("properties");
            country.setName(GermanyState.valueOf(properties.getString("LAN_ew_GEN").replace("-", "_").replace("ü", "ue").toUpperCase()));
            country.setInfected(properties.getInt("Fallzahl"));
            country.setDeaths(properties.getInt("Death"));
            country.setPopulation((long) (properties.getInt("Fallzahl") / (properties.getDouble("faelle_100000_EW") / 100000)));
            countries.add(country);
            if (country.getName() == null)
                Log.e(TAG, properties.getString("LAN_ew_GEN").replace("-", "_").replace("ü", "ue").toUpperCase() + "has not been matched!");
        }
        return countries;
    }

    public static TimeFramedCountry parseFromPostmanOneCountryWithTimeFrame(String toParse, ISOCountry isoCountry, boolean skipFirstDate) throws JSONException {
        if(toParse.toLowerCase().startsWith("{\"message\":\"too many requests")){
            throw new TooManyRequestsException("Too many requests were made!");
        }
        TimeFramedCountry country = new TimeFramedCountry();

        try {
            JSONArray jsonArray = new JSONArray(toParse);
            int dateRange = jsonArray.length();
            if (skipFirstDate) dateRange--;

            LocalDate[] dates = new LocalDate[dateRange];
            int[] deaths = new int[dateRange];
            int[] recovered = new int[dateRange];
            int[] infected = new int[dateRange];
            int[] active = new int[dateRange];
            country.setPop_inf_ratio(new double[dateRange]);
            country.setCountry(isoCountry);
            int i = 0;
            if (skipFirstDate) i++;
            for (; i < jsonArray.length(); i++) {
                if (skipFirstDate) {
                    dates[i - 1] = LocalDate.parse(jsonArray.getJSONObject(i).getString("Date").substring(0, 10));
                    infected[i - 1] = jsonArray.getJSONObject(i).getInt("Confirmed");
                    recovered[i - 1] = jsonArray.getJSONObject(i).getInt("Recovered");
                    deaths[i - 1] = jsonArray.getJSONObject(i).getInt("Deaths");
                    active[i - 1] = jsonArray.getJSONObject(i).getInt("Active");
                } else {
                    dates[i] = LocalDate.parse(jsonArray.getJSONObject(i).getString("Date").substring(0, 10));
                    infected[i] = jsonArray.getJSONObject(i).getInt("Confirmed");
                    recovered[i] = jsonArray.getJSONObject(i).getInt("Recovered");
                    deaths[i] = jsonArray.getJSONObject(i).getInt("Deaths");
                    active[i] = jsonArray.getJSONObject(i).getInt("Active");
                }
            }
            country.setDates(dates);
            country.setDeaths(deaths);
            country.setRecovered(recovered);
            country.setInfected(infected);
            country.setActive(active);
        } catch (JSONException e){
            Log.e(TAG, "Error while parsing this JSON:\n"+toParse, e);
            throw e;
        }

        return country;
    }

    public static Country parseFromHeroOneCountry(String toParse) {
        Country country = new Country();
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            switch (tuple[0]) {
                case "{\"country\"":
                    String normalizedName = Mapper.normalizeCountryName(tuple[1].replace("\"", ""));
                    if (Mapper.isInMap(API.HEROKU, normalizedName)) {
                        country.setName(Mapper.mapNameToISOCountry(API.HEROKU, normalizedName));
                    } else if (!Mapper.isInBlacklist(normalizedName)) {
                        country.setName(ISOCountry.valueOf(normalizedName));
                    }
                    break;
                case "\"deaths\"":
                    country.setDeaths(Integer.parseInt(collectNullToZero(tuple[1])));
                    break;
                case "\"cases\"":
                    country.setInfected(Integer.parseInt(collectNullToZero(tuple[1])));
                    break;
                case "\"recovered\"":
                    country.setRecovered(Integer.parseInt(collectNullToZero(tuple[1])));
                    break;
                case "\"active\"":
                    country.setActive(Integer.parseInt(collectNullToZero(tuple[1])));
            }
        }
        return country;
    }

    public static List<Country> parseFromHeroMultiCountry(String toParse) {
        Log.v(TAG, "Parsing multiple countries from api " + API.HEROKU.getName() + "...");
        List<Country> countryList = new LinkedList<>();
        try {
            JSONArray jsonArray = new JSONArray(toParse);
            for (int i = 0; i < jsonArray.length(); i++) {
                countryList.add(parseFromHeroOneCountry(jsonArray.get(i).toString()));
            }
        } catch (JSONException e) {
            Logger.logE(TAG, "Error parsing JSON: " + e);
        }
        Log.v(TAG, "Finished parsing multiple countries from " + API.HEROKU.getName() + "!");
        return countryList;
    }

    //todo use JSONObject
    public static Country parsePopCount(String toParse, String name) {
        Country country = new Country(ISOCountry.valueOf(name));
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            if (tuple[0].equals("\"population\"")) {
                country.setPopulation(Integer.parseInt(collectNullToZero(tuple[1])));
            }
        }
        return country;
    }

    public static Map<ISOCountry, Long> parseMultiPopCount(String toParse) throws JSONException {
        Log.v(TAG, "Parsing multiple population counts of countries from String...");
        Map<ISOCountry, Long> returnMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray(toParse);
        for (int i = 0; i < jsonArray.length(); i++) {
            String name = jsonArray.getJSONObject(i).getString("name");
            if (Mapper.isInMap(API.RESTCOUNTRIES, name)) {
                returnMap.put(Mapper.mapNameToISOCountry(API.RESTCOUNTRIES, name), jsonArray.getJSONObject(i).getLong("population"));
            } else {
                String normalizedName = Mapper.normalizeCountryName(name);
                if (!Mapper.isInBlacklist(name)) {
                    returnMap.put(ISOCountry.valueOf(normalizedName), jsonArray.getJSONObject(i).getLong("population"));
                }
            }
        }
        Log.v(TAG, "Finished parsing the population count from String!");
        return returnMap;
    }

    private static String collectNullToZero(String in) {
        return in.replace("null", "0");
    }
}
