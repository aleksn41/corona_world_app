package de.dhbw.corona_world_app.map;

import android.webkit.JavascriptInterface;

import androidx.lifecycle.MutableLiveData;

import de.dhbw.corona_world_app.api.Mapper;
import de.dhbw.corona_world_app.datastructure.ISOCountry;

public class JavaScriptInterface {

    private MutableLiveData<ISOCountry> current;

    @JavascriptInterface
    public void setISOCountry(String isoCode){
        current.setValue(Mapper.mapISOCodeToISOCountry(isoCode));
    }

    public MutableLiveData<ISOCountry> getCurrent() {
        return current;
    }

    public void setCurrent(MutableLiveData<ISOCountry> current) {
        this.current = current;
    }
}
