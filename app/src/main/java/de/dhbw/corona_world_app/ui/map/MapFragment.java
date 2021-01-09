package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();

    private MapViewModel mapViewModel;

    MutableLiveData<String> webViewString = new MutableLiveData<>();

    private final LoadingScreenInterface loadingScreen = new LoadingScreenInterface() {
        @Override
        public void startLoadingScreen() {

        }

        @Override
        public void endLoadingScreen() {

        }

        @Override
        public void setProgressBar(int progress, @NonNull String message) {

        }

        @Override
        public int getProgress() {
            return 0;
        }
    };

    //todo WebView is not final -> more zoom, clickable tooltips with routing to statistics
    //todo establish order
    //loading screen will be implemented by ui-team
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG,"Starting loading screen");
        loadingScreen.startLoadingScreen();

        Log.v(TAG,"Creating MapFragment view");
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        loadingScreen.setProgressBar(10,"Starting...");

        WebView myWebView = root.findViewById(R.id.map_web_view);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);

        ExecutorService service = ThreadPoolHandler.getInstance();
        loadingScreen.setProgressBar(20,"Requesting data...");
        Log.v(TAG,"Requesting all countries...");
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mapViewModel.initCountryList();
                } catch (ConnectException | InterruptedException | ExecutionException | JSONException e) {
                    Logger.logE(TAG,"Exception during initiation of country list!", e);
                }
            }
        });
        loadingScreen.setProgressBar(30,"Request sent...");
        mapViewModel.mCountryList.observe(getViewLifecycleOwner(), countries -> {
            loadingScreen.setProgressBar(50,"Answer arrived...");
            Log.v(TAG,"Requested countries have arrived");
            loadingScreen.setProgressBar(70,"Decrypting data...");
            webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
            Log.v(TAG,"Loading WebView with WebString...");
            loadingScreen.setProgressBar(100,"Visualizing data...");
            myWebView.loadData(webViewString.getValue(), "text/html", "base64");
            loadingScreen.endLoadingScreen();
        });
        return root;
    }
}