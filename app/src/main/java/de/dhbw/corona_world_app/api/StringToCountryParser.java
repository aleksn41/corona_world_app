package de.dhbw.corona_world_app.api;

import de.dhbw.corona_world_app.datastructure.Country;
//TODO use professional stuff for JSON parsing
public class StringToCountryParser {

    public static Country parseFromHeroOneCountry(String toParse, Country country){

        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            switch (tuple[0]) {
                case"\"deaths\"":country.setDeaths(Integer.parseInt(tuple[1]));
                case"\"cases\"":country.setInfected(Integer.parseInt(tuple[1]));
                case"\"recovered\"":country.setRecovered(Integer.parseInt(tuple[1]));
            }
        }
        return country;
    }

    public static Country parsePopCount(String toParse, Country country){
        String[] splitArray = toParse.split(",");
        for (String string : splitArray) {
            String[] tuple = string.split(":");
            if(tuple[0].equals("\"population\"")){
                country.setPopulation(Integer.parseInt(tuple[1]));
            }
        }
        return country;
    }
}
