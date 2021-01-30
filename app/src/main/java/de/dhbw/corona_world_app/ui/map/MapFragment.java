package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.map.JavaScriptInterface;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();

    private MapViewModel mapViewModel;

    MutableLiveData<String> webViewString = new MutableLiveData<>();

    LinearProgressIndicator progressBar;

    TextView textView;

    private final LoadingScreenInterface loadingScreen = new LoadingScreenInterface() {
        @Override
        public void startLoadingScreen() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void endLoadingScreen() {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void setProgressBar(int progress) {
            progressBar.setProgress(progress);
        }

        @Override
        public float getProgress() {
            return progressBar.getProgress();
        }
    };

    //todo WebView is not final -> more zoom, clickable tooltips with routing to statistics
    //todo establish order
    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "Creating MapFragment view");
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        textView = root.findViewById(R.id.bottomSheetTextView);
        Log.v(TAG, "Starting loading screen");
        loadingScreen.startLoadingScreen();
        mapViewModel.setPathToCacheDir(requireActivity().getCacheDir());
        loadingScreen.setProgressBar(10);

        //setup bottomsheet
        LinearLayout bottomSheet = root.findViewById(R.id.bottomSheet);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setPeekHeight(190);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setFitToContents(true);
        //listeners for bottom sheet
        //click event for show-dismiss bottom sheet
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        boolean cacheDisabled = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("cache_deactivated", false);
        boolean storageDisabled = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("storage_deactivated", false);
        Log.d(TAG, "Initiating view model with cache " + (cacheDisabled ? "disabled" : "enabled") + " and storage " + (storageDisabled ? "disabled" : "enabled") + "...");
        mapViewModel.init(cacheDisabled, storageDisabled);
        WebView myWebView = root.findViewById(R.id.map_web_view);
        WebSettings webSettings = myWebView.getSettings();
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                loadingScreen.endLoadingScreen();

            }
        });

        // callback for do something
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        myWebView.setWebViewClient(new WebViewClient() {
                                               public void onPageFinished(WebView view, String url) {
                                                   loadingScreen.endLoadingScreen();
                                               }
                                           });
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        JavaScriptInterface jsInterface = new JavaScriptInterface();
        myWebView.addJavascriptInterface(jsInterface, "jsinterface");
        ExecutorService service = ThreadPoolHandler.getInstance();


        jsInterface.current.observe(getViewLifecycleOwner(), isoCountry -> {
            if(isoCountry != null){
                List<Country> countryList = mapViewModel.mCountryList.getValue();
                if(countryList == null) throw new IllegalStateException("Country list was not initialized correctly!");
                for (int i = 0; i < countryList.size(); i++) {
                    if(countryList.get(i).getISOCountry().equals(isoCountry)){
                        Country country = countryList.get(i);
                        textView.setText(" " + isoCountry.toString() +
                                "\n Population: "+ country.getPopulation() +
                                "\n Healthy: " + country.getHealthy() +
                                "\n Infected: " + country.getInfected() +
                                "\n Recovered: " + country.getRecovered() +
                                "\n Deaths: " + country.getDeaths() +
                                "\n Population-Infected Ratio: "+ country.getPop_inf_ratio() +
                                "\n Infected-Deaths Ratio: "+ (double) country.getDeaths()/country.getInfected()
                                );
                    }
                }
                if(textView.getText().length()==0){
                    Log.e(TAG, "No data was found for country "+isoCountry+"!");
                    textView.setText("No data available!");
                }
            } else {
                textView.setText("");
            }
        });

        Log.v(TAG, "Requesting all countries...");
        loadingScreen.setProgressBar(25);
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mapViewModel.initCountryList();
                } catch (InterruptedException | ExecutionException | JSONException | IOException | ClassNotFoundException e) {
                    Logger.logE(TAG, "Exception during initiation of country list!", e);
                }
            }
        });
        mapViewModel.mCountryList.observe(
                getViewLifecycleOwner(), countries ->
                {
                    loadingScreen.setProgressBar(50);
                    Log.v(TAG, "Requested countries have arrived");
                    loadingScreen.setProgressBar(70);
                    webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
                    Log.v(TAG, "Loading WebView with WebString...");
                    loadingScreen.setProgressBar(100);
                    myWebView.loadData(webViewString.getValue(), "text/html", "base64");
                });
        return root;
    }
}