package de.dhbw.corona_world_app.ui.map;

import android.util.Log;

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

    private MapData services = new MapData();

    //private MutableLiveData<String> mText;

    public MutableLiveData<List<Country>> mCountryList = new MutableLiveData<>();

    public void init(){
        if(services!=null){
            services = new MapData();
        }
    }

    public void initCountryList() throws Throwable {
        manager = new APIManager(false,false);
        //mText = new MutableLiveData<>();
        //mText.postValue("World Map");
        mCountryList.postValue(manager.getDataWorld(API.HEROKU));
        //Map<String, Double> countryMap = new HashMap<>();
        //return services.putEntries(countryMap);
    }

    public String getWebViewStringCustom(List<Country> countryList){
        Map<String,Double> countryMap = new HashMap<>();
        Log.v(this.getClass().getName(),"Putting gotten countries into map");
        for (Country country:countryList) {
            countryMap.put(country.getName(), country.getPop_inf_ratio());
        }
        Log.v(this.getClass().getName(),"Executing service to build WebViewString");
        return services.putEntries(countryMap);
    }

   // public LiveData<String> getText() {
   //     return mText;
   // }

}