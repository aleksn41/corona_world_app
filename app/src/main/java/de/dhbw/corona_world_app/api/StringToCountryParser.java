package de.dhbw.corona_world_app.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class StringToCountryParser {

    private static final String TAG = StringToCountryParser.class.getSimpleName();

    public static Country parseFromHeroOneCountry(String toParse, Country country){
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            switch (tuple[0]) {
                case"\"deaths\"":country.setDeaths(Integer.parseInt(tuple[1]));break;
                case"\"cases\"":country.setInfected(Integer.parseInt(tuple[1]));break;
                case"\"recovered\"":country.setRecovered(Integer.parseInt(tuple[1]));break;
            }
        }
        return country;
    }

    public static Country parseFromHeroOneCountry(String toParse){
        Country country = new Country();
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            switch (tuple[0]) {
                case"{\"country\"":String normalizedName = Mapper.normalizeCountryName(tuple[1].replace("\"",""));
                                   if(Mapper.isInMap(API.HEROKU, normalizedName)) {
                                       country.setISOCountry(Mapper.mapNameToISOCountry(API.HEROKU, normalizedName));
                                   } else if(!Mapper.isInBlacklist(normalizedName)){
                                       country.setISOCountry(ISOCountry.valueOf(normalizedName));
                                   }
                                   break;
                case"\"deaths\"":country.setDeaths(Integer.parseInt(collectNullToZero(tuple[1])));break;
                case"\"cases\"":country.setInfected(Integer.parseInt(collectNullToZero(tuple[1])));break;
                case"\"recovered\"":country.setRecovered(Integer.parseInt(collectNullToZero(tuple[1])));break;
            }
        }
        return country;
    }

    public static List<Country> parseFromHeroMultiCountry(String toParse){
        Log.v(TAG, "Parsing multiple countries from api "+API.HEROKU.getName()+"...");
        List<Country> countryList = new LinkedList<>();
        try {
            JSONArray jsonArray = new JSONArray(toParse);
            for(int i = 0; i < jsonArray.length(); i++) {
                countryList.add(parseFromHeroOneCountry(jsonArray.get(i).toString()));
            }
        } catch (JSONException e) {
            Logger.logE(TAG, "Error parsing JSON: "+e);
        }
        Log.v(TAG, "Finished parsing multiple countries from "+API.HEROKU.getName()+"!");
        return countryList;
    }

    /*
    Data can be false, then nothing will be set.
     */
    public static Country parsePopCount(String toParse, String name){
        Country country = new Country(ISOCountry.valueOf(name));
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            if(tuple[0].equals("\"population\"")){
                country.setPopulation(Integer.parseInt(collectNullToZero(tuple[1])));
            }
        }
        return country;
    }

    public static Map<ISOCountry, Long> parseMultiPopCount(String toParse) throws JSONException {
        Log.v(TAG,"Parsing multiple population counts of countries from String...");
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
        Log.v(TAG,"Finished parsing the population count from String!");
        return returnMap;
    }

    /*
    Data can be false, then nothing will be set.
     */
    public static long parsePopCountNumber(String toParse){
        String[] splitArray = toParse.split(",");
        long returnNumber = 0;
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            if(tuple[0].equals("\"population\"")){
                returnNumber = Long.parseLong(collectNullToZero(tuple[1]));
            }
        }
        return returnNumber;
    }

    private static String collectNullToZero(String in){
        return in.replace("null","0");
    }
}
