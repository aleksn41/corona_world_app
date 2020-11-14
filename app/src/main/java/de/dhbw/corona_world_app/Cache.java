package de.dhbw.corona_world_app;

import java.util.LinkedList;
import java.util.List;

public class Cache {

    private static List<Country> countries = new LinkedList<>();

    public static void addCountry(Country country){
        countries.add(country);
    }

    public static void deleteCountry(Country country){
        countries.remove(country);
    }

    public static List<String> getCCwoData(){
        List<String> nameList = new LinkedList<>();
        countries.forEach(country -> nameList.add(country.getName()));
        return nameList;
    }
}
