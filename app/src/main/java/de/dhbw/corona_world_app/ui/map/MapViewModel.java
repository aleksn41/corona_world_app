package de.dhbw.corona_world_app.ui.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.map.MapData;

public class MapViewModel extends ViewModel {

    private static final String TAG = MapViewModel.class.getSimpleName();

    private APIManager manager;

    private final MapData services = new MapData();

    public MutableLiveData<List<Country>> mCountryList = new MutableLiveData<>();

    public void initCountryList() throws ConnectException, InterruptedException, ExecutionException, JSONException {
        APIManager.setSettings(true,false);
        List<Country> apiGottenList = APIManager.getDataWorld(API.HEROKU);
        if(apiGottenList==null || !(apiGottenList.size() > 0)){
            throw new ConnectException("Could not get expected data from API " + API.HEROKU.getName() + "!");
        }
        mCountryList.postValue(apiGottenList);
    }

    public String getWebViewStringCustom(List<Country> countryList){
        return services.putEntries(countryList);
    }
}