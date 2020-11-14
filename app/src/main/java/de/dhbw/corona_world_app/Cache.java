package de.dhbw.corona_world_app;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Cache {

    private static List<Country> countries = new LinkedList<>();

    public static void addCountry(Country country){
        countries.add(country);
    }

    public static void deleteCountry(Country country){
        countries.remove(country);
    }

    public static List<String> getCCsWoData() {
        List<String> nameList = new LinkedList<>();
        countries.forEach(country -> nameList.add(country.getName()));
        return nameList;
    }

    public static List<Country> getCCs(){
        return countries;
    }

    public static Country getCC(String name){
        List<Country> countryListReturn = countries.stream()
                                                    .filter(country -> name == country.getName())
                                                    .collect(Collectors.toList());
        if(countryListReturn.size()>1){
            Log.e("DuplicateException","There is more than one country with the name: "+name);
        } else if(countryListReturn.size()<1){
            Log.e("CountryNotFoundException","There is no country with the name: "+name);
        }
        return countryListReturn.get(0);
    }

    /**
     * Appends a list of countries to the already cached list.
     *
     * @param countriesToAdd list of countries to be added
     * @return {@code boolean} listChanged
     */
    public static boolean addCountries(List<Country> countriesToAdd){
        return countries.addAll(countriesToAdd);
    }
}
