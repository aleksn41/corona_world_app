package de.dhbw.corona_world_app.ui.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private APIManager manager;

    private final MapData services = new MapData();

    private File pathToCacheDir;

    private LocalDateTime worldCacheAge;

    public MutableLiveData<List<Country>> mCountryList = new MutableLiveData<>();

    public void cacheDataWorld(@NonNull List<Country> worldData) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(pathToCacheDir + "/world_cache.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(worldData);
        out.close();
        fileOut.close();
    }

    @SuppressWarnings("unchecked")
    public List<Country> getCachedDataWorld() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(pathToCacheDir + "/world_cache.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        List<Country> returnList = (List<Country>) in.readObject();
        in.close();
        fileIn.close();
        return returnList;
    }

    public void initCountryList() throws IOException, InterruptedException, ExecutionException, JSONException, ClassNotFoundException {
        List<Country> apiGottenList;
        APIManager.enableCache();
        if(!APIManager.isCacheEnabled() || worldCacheAge==null || worldCacheAge.isBefore(LocalDateTime.now().minusMinutes(APIManager.MAX_GET_DATA_WORLD_CACHE_AGE))) {
            apiGottenList = APIManager.getDataWorld(API.HEROKU);
            if (apiGottenList == null || !(apiGottenList.size() > 0)) {
                throw new ConnectException("Could not get expected data from API " + API.HEROKU.getName() + "!");
            }
            if(APIManager.isCacheEnabled()) {
                cacheDataWorld(apiGottenList);
                worldCacheAge = LocalDateTime.now();
            }
        } else {
            apiGottenList = getCachedDataWorld();
        }
        mCountryList.postValue(apiGottenList);
    }

    public String getWebViewStringCustom(List<Country> countryList){
        return services.putEntries(countryList);
    }

    public void setPathToCacheDir(File pathToCacheDir){
        this.pathToCacheDir = pathToCacheDir;
    }
}