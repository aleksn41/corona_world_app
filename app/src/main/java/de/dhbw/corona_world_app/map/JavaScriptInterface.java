package de.dhbw.corona_world_app.map;

import android.webkit.JavascriptInterface;

import androidx.lifecycle.MutableLiveData;

import de.dhbw.corona_world_app.api.Mapper;
import de.dhbw.corona_world_app.datastructure.Displayable;

/**
 * This class is used to translate the selected country of the map from javascript to java.
 *
 * @author Thomas Meier
 */
public class JavaScriptInterface {

    public final MutableLiveData<Displayable> current = new MutableLiveData<>();

    public JavaScriptInterface(){

    }

    @JavascriptInterface
    public void setISOCountry(String isoCode){
        if(isoCode == null){
            current.postValue(null);
        } else if(isoCode.length()==2){
            current.postValue(Mapper.mapISOCodeToISOCountry(isoCode));
        } else if(isoCode.length()==5){
            current.postValue(Mapper.mapISOCodeToGermanyState(isoCode));
        }
    }
}
