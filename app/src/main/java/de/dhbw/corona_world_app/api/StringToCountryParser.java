package de.dhbw.corona_world_app.api;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.datastructure.Country;
//TODO use professional stuff for JSON parsing
public class StringToCountryParser {

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
                case"{\"country\"":country.setName(tuple[1].replace("\"",""));break;
                case"\"deaths\"":country.setDeaths(Integer.parseInt(collectNullToZero(tuple[1])));break;
                case"\"cases\"":country.setInfected(Integer.parseInt(collectNullToZero(tuple[1])));break;
                case"\"recovered\"":country.setRecovered(Integer.parseInt(collectNullToZero(tuple[1])));break;
            }
        }
        return country;
    }

    public static List<Country> parseFromHeroMultiCountry(String toParse, List<Country> countryList){
        try {
            JSONArray jsonArray = new JSONArray(toParse);
            for(int i = 0; i < jsonArray.length(); i++) {
                countryList.add(parseFromHeroOneCountry(jsonArray.get(i).toString()));
            }
        } catch (JSONException e) {
            Logger.logE("ParsingException","Error parsing JSON: "+e);
        }
        //System.out.println(countryList);
        return countryList;
    }

    /*
    Data can be false, then nothing will be set.
     */
    public static Country parsePopCount(String toParse, Country country){
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            if(tuple[0].equals("\"population\"")){
                country.setPopulation(Integer.parseInt(collectNullToZero(tuple[1])));
            }
        }
        return country;
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
