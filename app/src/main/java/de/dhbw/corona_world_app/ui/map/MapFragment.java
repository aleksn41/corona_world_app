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

import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();

    private MapViewModel mapViewModel;

    MutableLiveData<String> webViewString = new MutableLiveData<>();

    LoadingScreenInterface loadingScreen;
    //todo map ISOCodes to screen names for better understanding
    //todo WebView is not final -> more zoom, clickable tooltips with routing to statistics
    //todo establish order
    //todo @Aleks -> insert loading screen to overshadow short white screen
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG,"");
        Log.v(TAG,"Creating MapFragment view");
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        WebView myWebView = root.findViewById(R.id.map_web_view);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDisplayZoomControls(true);
        ExecutorService service = ThreadPoolHandler.getsInstance();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mapViewModel.initCountryList();
                } catch (Throwable throwable) {
                    Log.e(TAG,"Exception during initiation of country list!", throwable);
                }
            }
        });
        mapViewModel.mCountryList.observe(getViewLifecycleOwner(), countries -> {
            Log.v(TAG,"Requested countries have arrived");
            webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
            Log.v(TAG,"Loading WebView with "+ webViewString.getValue());
            myWebView.loadData(webViewString.getValue(), "text/html", "base64");
        });
        return root;
    }
}