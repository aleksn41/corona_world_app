package de.dhbw.corona_world_app.ui.map;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.map.MapData;

public class MapViewModel extends ViewModel {

    private static final String TAG = MapViewModel.class.getSimpleName();

    private final MapData services = new MapData();

    private File pathToCacheDir;

    private static LocalDateTime worldCacheAge;

    public MutableLiveData<List<Country>> mCountryList = new MutableLiveData<>();

    public void init(boolean cacheDisabled, boolean longTermDisabled) {
        APIManager.setSettings(!cacheDisabled, !longTermDisabled);
    }

    public void cacheDataWorld(@NonNull List<Country> worldData) throws IOException {
        Log.v(TAG, "Caching world data...");
        FileOutputStream fileOut = new FileOutputStream(pathToCacheDir + "/world_cache.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(worldData);
        out.close();
        fileOut.close();
    }

    @SuppressWarnings("unchecked")
    public List<Country> getCachedDataWorld() throws IOException, ClassNotFoundException {
        Log.v(TAG, "Getting cached world data...");
        FileInputStream fileIn = new FileInputStream(pathToCacheDir + "/world_cache.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        List<Country> returnList = (List<Country>) in.readObject();
        in.close();
        fileIn.close();
        return returnList;
    }

    public void cacheGermany(@NonNull List<Country> germanyData) throws IOException {
        Log.v(TAG, "Caching germany data...");
        FileOutputStream fileOut = new FileOutputStream(pathToCacheDir + "/germany_cache.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(germanyData);
        out.close();
        fileOut.close();
    }

    public List<Country> getCachedGermany() throws IOException, ClassNotFoundException {
        Log.v(TAG, "Getting cached germany data...");
        FileInputStream fileIn = new FileInputStream(pathToCacheDir + "/germany_cache.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        List<Country> returnList = (List<Country>) in.readObject();
        in.close();
        fileIn.close();
        return returnList;
    }

    public void initGermany() throws IOException, InterruptedException, ExecutionException, JSONException, ClassNotFoundException {
        List<Country> apiGottenList;
        Log.v(TAG, "Initiating country list...");
        if (!APIManager.isCacheEnabled() || worldCacheAge == null || worldCacheAge.isBefore(LocalDateTime.now().minusMinutes(APIManager.MAX_GET_DATA_WORLD_CACHE_AGE))) {
            apiGottenList = APIManager.getDataGermany(API.ARCGIS);
            if (!(apiGottenList.size() > 0)) {
                throw new ConnectException("Could not get expected data from API " + API.ARCGIS.getName() + "!");
            }
            if (APIManager.isCacheEnabled()) {
                cacheGermany(apiGottenList);
                worldCacheAge = LocalDateTime.now();
            }
        } else {
            apiGottenList = getCachedGermany();
        }
        mCountryList.postValue(apiGottenList);
    }

    public void initCountryList() throws IOException, InterruptedException, ExecutionException, JSONException, ClassNotFoundException {
        List<Country> apiGottenList;
        Log.v(TAG, "Initiating country list...");
        if (!APIManager.isCacheEnabled() || worldCacheAge == null || worldCacheAge.isBefore(LocalDateTime.now().minusMinutes(APIManager.MAX_GET_DATA_WORLD_CACHE_AGE))) {
            apiGottenList = APIManager.getDataWorld(API.HEROKU);
            if (apiGottenList == null || !(apiGottenList.size() > 0)) {
                throw new ConnectException("Could not get expected data from API " + API.HEROKU.getName() + "!");
            }
            if (APIManager.isCacheEnabled()) {
                cacheDataWorld(apiGottenList);
                worldCacheAge = LocalDateTime.now();
            }
        } else {
            apiGottenList = getCachedDataWorld();
        }
        mCountryList.postValue(apiGottenList);
    }

    public void switchResolution(MapData.Resolution resolution){
        services.setResolution(resolution);
    }

    public MapData.Resolution getCurrentResolution(){
        return services.getResolution();
    }

    public String getWebViewStringCustom(List<Country> countryList) {
        return services.putEntries(countryList);
    }

    public void setPathToCacheDir(File pathToCacheDir) {
        this.pathToCacheDir = pathToCacheDir;
    }
}