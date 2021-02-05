package de.dhbw.corona_world_app.ui.map;

import android.util.Log;
import android.util.Pair;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.api.API;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.Displayable;
import de.dhbw.corona_world_app.datastructure.displayables.GermanyState;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;
import de.dhbw.corona_world_app.map.MapData;
import de.dhbw.corona_world_app.map.MapCacheObject;
import de.dhbw.corona_world_app.map.MapWithBoxCacheObject;

public class MapViewModel extends ViewModel {

    private static final String TAG = MapViewModel.class.getSimpleName();

    private static boolean alreadyRunning = false;

    private final String WORLD_CACHE_FILENAME = "/world_cache.ser";

    private final String GERMANY_CACHE_FILENAME = "/germany_cache.ser";

    private final MapData services = new MapData();

    private File pathToCacheDir;

    private final ExecutorService executorService = ThreadPoolHandler.getInstance();


    public MutableLiveData<List<Country<ISOCountry>>> mCountryList = new MutableLiveData<>();

    public MutableLiveData<List<Country<GermanyState>>> mStatesList = new MutableLiveData<>();

    public MutableLiveData<Country<ISOCountry>> mBoxValue = new MutableLiveData<>();

    public void init(boolean cacheDisabled, boolean longTermDisabled) {
        APIManager.setSettings(!cacheDisabled, !longTermDisabled);
    }

    public void cacheDataWorld(@NonNull List<Country<ISOCountry>> worldData) throws IOException {
        Log.v(TAG, "Caching world data...");
        try (FileOutputStream fileOut = new FileOutputStream(pathToCacheDir + WORLD_CACHE_FILENAME)) {
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new MapCacheObject<>(LocalDateTime.now(), worldData));
            out.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Country<ISOCountry>> getCachedDataWorldIfRelevant() throws IOException, ClassNotFoundException {
        Log.v(TAG, "Getting cached world data...");
        List<Country<ISOCountry>> returnList = null;
        File file = new File(pathToCacheDir + WORLD_CACHE_FILENAME);
        if (file.exists() && !file.isDirectory()) {
            try (FileInputStream fileIn = new FileInputStream(pathToCacheDir + WORLD_CACHE_FILENAME)) {
                ObjectInputStream in = new ObjectInputStream(fileIn);
                MapCacheObject<ISOCountry> cacheObject = (MapCacheObject<ISOCountry>) in.readObject();
                if (cacheObject.getCreationTime().isAfter(LocalDateTime.now().minusMinutes(APIManager.MAX_GET_DATA_WORLD_CACHE_AGE))) {
                    returnList = cacheObject.getDataList();
                }
                in.close();
            }
        }
        return returnList;
    }

    public void cacheGermany(@NonNull List<Country<GermanyState>> germanyData, @NonNull Country<ISOCountry> germanySummary) throws IOException {
        Log.v(TAG, "Caching germany data...");
        try (FileOutputStream fileOut = new FileOutputStream(pathToCacheDir + GERMANY_CACHE_FILENAME)) {
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new MapWithBoxCacheObject<>(LocalDateTime.now(), germanyData, germanySummary));
            out.close();
        }
    }

    @SuppressWarnings("unchecked")
    public MapWithBoxCacheObject<GermanyState> getCachedGermanyIfRelevant() throws IOException, ClassNotFoundException {
        Log.v(TAG, "Getting cached germany data...");
        File file = new File(pathToCacheDir + GERMANY_CACHE_FILENAME);
        if (file.exists() && !file.isDirectory()) {
            try (FileInputStream fileIn = new FileInputStream(file)) {
                ObjectInputStream in = new ObjectInputStream(fileIn);
                MapWithBoxCacheObject<GermanyState> cacheObject = (MapWithBoxCacheObject<GermanyState>) in.readObject();
                if (cacheObject.getCreationTime().isAfter(LocalDateTime.now().minusMinutes(APIManager.MAX_GET_DATA_WORLD_CACHE_AGE))) {
                    return cacheObject;
                }
                in.close();
            }
        }
        return null;
    }

    public void initGermany() throws IOException, InterruptedException, ExecutionException, ClassNotFoundException {
        List<Country<GermanyState>> apiGottenList;
        Country<ISOCountry> germanySummary;
        if (!alreadyRunning) {
            try {
                alreadyRunning = true;
                Log.v(TAG, "Initiating country list...");
                MapWithBoxCacheObject<GermanyState> cacheObject = null;
                if (APIManager.isCacheEnabled()) {
                    cacheObject = getCachedGermanyIfRelevant();
                }
                if(cacheObject == null) {
                    Future<List<Country<GermanyState>>> futureApiGottenList = executorService.submit(() -> APIManager.getDataGermany(API.ARCGIS));
                    germanySummary = APIManager.getData(Collections.singletonList(ISOCountry.Germany), Arrays.asList(Criteria.POPULATION, Criteria.INFECTED, Criteria.DEATHS, Criteria.RECOVERED)).get(0);
                    apiGottenList = futureApiGottenList.get();
                    if (APIManager.isCacheEnabled()) {
                        cacheGermany(apiGottenList, germanySummary);
                    }
                } else {
                    apiGottenList = cacheObject.getDataList();
                    germanySummary =cacheObject.getMapBoxValue();
                }
                if (!(apiGottenList.size() > 0)) {
                    throw new ConnectException("Could not get expected data from API " + API.ARCGIS.getName() + "!");
                }
                mBoxValue.postValue(germanySummary);
                mStatesList.postValue(apiGottenList);
                alreadyRunning = false;
            } catch (Exception e) {
                alreadyRunning = false;
                throw e;
            }
        } else {
            MapWithBoxCacheObject<GermanyState> cache = getCachedGermanyIfRelevant();
            apiGottenList = cache.getDataList();
            germanySummary = cache.getMapBoxValue();
            mBoxValue.postValue(germanySummary);
            mStatesList.postValue(apiGottenList);
            Thread.currentThread().interrupt();
        }
    }

    public void initCountryList() throws IOException, InterruptedException, ExecutionException, JSONException, ClassNotFoundException {
        List<Country<ISOCountry>> apiGottenList = null;
        if (!alreadyRunning) {
            try {
                alreadyRunning = true;
                Log.v(TAG, "Initiating country list...");
                if (APIManager.isCacheEnabled()) {
                    apiGottenList = getCachedDataWorldIfRelevant();
                }
                if (apiGottenList == null) {
                    apiGottenList = APIManager.getDataWorld(API.HEROKU);
                    if (APIManager.isCacheEnabled()) {
                        cacheDataWorld(apiGottenList);
                    }
                }
                if (apiGottenList == null || !(apiGottenList.size() > 0)) {
                    throw new ConnectException("Could not get expected data from API " + API.HEROKU.getName() + "!");
                }
                for (Country<ISOCountry> country : apiGottenList) {
                    if (country.getName().equals(ISOCountry.World)) {
                        mBoxValue.postValue(country);
                    }
                }
                mCountryList.postValue(apiGottenList);
                alreadyRunning = false;
            } catch (Exception e) {
                alreadyRunning = false;
                throw e;
            }
        } else {
            apiGottenList = getCachedDataWorldIfRelevant();
            for (Country<ISOCountry> country : apiGottenList) {
                if (country.getName().equals(ISOCountry.World)) {
                    mBoxValue.postValue(country);
                }
            }
            mCountryList.postValue(apiGottenList);
            Thread.currentThread().interrupt();
        }
    }

    public void switchResolution(MapData.Resolution resolution) {
        services.setResolution(resolution);
    }

    public MapData.Resolution getCurrentResolution() {
        return services.getResolution();
    }

    public <T extends Displayable> String getWebViewStringCustom(List<Country<T>> countryList) {
        return services.putEntries(countryList);
    }

    public void setPathToCacheDir(File pathToCacheDir) {
        this.pathToCacheDir = pathToCacheDir;
    }
}