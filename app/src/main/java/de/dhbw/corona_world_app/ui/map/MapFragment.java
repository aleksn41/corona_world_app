package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;

public class MapFragment extends Fragment {

    private MapViewModel mapViewModel;

    MutableLiveData<String> webViewString = new MutableLiveData<>();

    //todo look after webview -> is not working (maybe webviewclient)
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(getClass().getName(),"Creating MapFragment view");
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        WebView myWebView = root.findViewById(R.id.map_web_view);
        myWebView.getSettings().setJavaScriptEnabled(true);
        ExecutorService service = ThreadPoolHandler.getsInstance();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mapViewModel.initCountryList();
                } catch (Throwable throwable) {
                    Log.e(getClass().getName(),"Exception during initiation of country list!\n"+ Arrays.toString(throwable.getStackTrace()));
                }
            }
        });
        mapViewModel.mCountryList.observe(getViewLifecycleOwner(), countries -> {
            Log.v(this.getClass().getName(),"Requested countries have arrived");
            webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
            Log.v(getClass().getName(),"Loading WebView with "+ webViewString.getValue());
            myWebView.loadData(webViewString.getValue(),
                    "text/html", "base64");
            //myWebView.loadUrl("https://www.google.de");
        });
        return root;
    }
}