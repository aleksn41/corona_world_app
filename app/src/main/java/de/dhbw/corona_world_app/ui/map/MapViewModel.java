package de.dhbw.corona_world_app.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.map.MapData;

public class MapViewModel extends ViewModel {

    private APIManager manager;

    private MapData services;

    private MutableLiveData<String> mText;

    private MutableLiveData<List<Country>> countryList;

    public MapViewModel() throws Throwable {
        manager = new APIManager(false,false);
        services = new MapData();
        mText = new MutableLiveData<>();
        mText.setValue("World Map");
        countryList = new MutableLiveData<>();
        countryList.setValue(manager.getDataWorld(API.HEROKU));
    }

    public String getWebViewString(){
        Map<String,Double> countryMap = new HashMap<>();
        for (Country country:countryList.getValue()) {
            countryMap.put(country.getName(),country.getPop_inf_ratio());
        }
        return services.putEntries(countryMap);
    }

    public String getWebViewStringCustom(List<Country> countryList){
        Map<String,Double> countryMap = new HashMap<>();
        for (Country country:countryList) {
            countryMap.put(country.getName(),country.getPop_inf_ratio());
        }
        return services.putEntries(countryMap);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Country>> getReloadCountryList() throws Throwable {
        countryList.setValue(manager.getDataWorld(API.HEROKU));
        return countryList;
    }

    public LiveData<List<Country>> getCountryList(){
        return countryList;
    }
}