package de.dhbw.corona_world_app.ui.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.map.MapData;

public class MapViewModel extends ViewModel {

    private static final String TAG = MapViewModel.class.getSimpleName();

    private APIManager manager;

    private MapData services = new MapData();

    public MutableLiveData<List<Country>> mCountryList = new MutableLiveData<>();

    public void initCountryList() throws Throwable {
        manager = new APIManager(false,false);
        List<Country> apiGottenList = manager.getDataWorld(API.HEROKU);
        if(apiGottenList==null || !(apiGottenList.size() > 0)){
            throw new ConnectException("Could not get expected data from API " + API.HEROKU.getName() + "!");
        }
        mCountryList.postValue(apiGottenList);
    }

    public String getWebViewStringCustom(List<Country> countryList){
        Map<String,Double> countryMap = new HashMap<>();
        Log.v(TAG,"Putting gotten countries into map...");
        int cnt = 0;
        for (Country country:countryList) {
            if(country.getISOCountry()!=null) {
                cnt++;
                countryMap.put(country.getISOCountry().getISOCode(), country.getPop_inf_ratio());
            }
        }
        Log.v(TAG,"Finished constructing map of size "+cnt+"!\nExecuting service to build WebViewString...");
        return services.putEntries(countryList);
    }
}