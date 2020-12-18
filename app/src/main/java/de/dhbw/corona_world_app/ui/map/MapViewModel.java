package de.dhbw.corona_world_app.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;

public class MapViewModel extends ViewModel {

    private APIManager manager;

    private MutableLiveData<String> mText;

    private MutableLiveData<List<Country>> countryList;

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("World Map");
        countryList.setValue(manager.getDataWorld());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Country>> getReloadCountryList(){
        countryList.setValue(manager.getDataWorld());
        return countryList;
    }

    public LiveData<List<Country>> getCountryList(){
        return countryList;
    }
}