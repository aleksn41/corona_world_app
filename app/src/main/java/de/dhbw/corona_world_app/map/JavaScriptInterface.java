package de.dhbw.corona_world_app.map;

import android.webkit.JavascriptInterface;

import androidx.lifecycle.MutableLiveData;

import de.dhbw.corona_world_app.api.Mapper;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class JavaScriptInterface {

    public MutableLiveData<ISOCountry> current = new MutableLiveData<>();

    public JavaScriptInterface(){

    }

    @JavascriptInterface
    public void setISOCountry(String isoCode){
        if(isoCode == null){
            current.postValue(null);
        } else {
            current.postValue(Mapper.mapISOCodeToISOCountry(isoCode));
        }
    }
}
