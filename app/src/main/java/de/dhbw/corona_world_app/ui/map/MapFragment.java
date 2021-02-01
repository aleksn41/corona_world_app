package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.map.JavaScriptInterface;
import de.dhbw.corona_world_app.ui.tools.ErrorCode;
import de.dhbw.corona_world_app.ui.tools.ErrorDialog;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();

    private MapViewModel mapViewModel;

    MutableLiveData<String> webViewString = new MutableLiveData<>();

    LinearProgressIndicator progressBar;

    TextView textView;

    NumberFormat percentageFormat=NumberFormat.getPercentInstance();

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
        textView = root.findViewById(R.id.bottomSheetTitle);
        Log.v(TAG, "Starting loading screen");
        loadingScreen.startLoadingScreen();
        mapViewModel.setPathToCacheDir(requireActivity().getCacheDir());
        loadingScreen.setProgressBar(10);
        percentageFormat.setMaximumFractionDigits(3);
        //setup bottomsheet
        RelativeLayout bottomSheet = root.findViewById(R.id.bottomSheet);
        BottomSheetBehavior<RelativeLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                loadingScreen.endLoadingScreen();
                TextView mapBox=root.findViewById(R.id.mapBox);
                mapBox.setVisibility(View.VISIBLE);
                setDataOfBox(mapBox,7000000000L,1000000L,100000L,10000L);
                bottomSheet.setVisibility(View.VISIBLE);
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
            if (isoCountry != null) {
                List<Country> countryList = mapViewModel.mCountryList.getValue();
                if (countryList == null)
                    throw new IllegalStateException("Country list was not initialized correctly!");
                for (int i = 0; i < countryList.size(); i++) {
                    if (countryList.get(i).getISOCountry().equals(isoCountry)) {
                        ((ImageView)root.findViewById(R.id.map_box_flag)).setImageDrawable(ContextCompat.getDrawable(requireContext(),isoCountry.getFlagDrawableID()));
                        Country country = countryList.get(i);
                        textView.setText(isoCountry.toString());
                        ((TextView)root.findViewById(R.id.bottomSheetDescription)).setText(getString(R.string.bottom_sheet_description,country.getPopulation(),country.getHealthy(),country.getInfected(),country.getRecovered(),country.getDeaths(),percentageFormat.format(country.getPop_inf_ratio()),percentageFormat.format((double)country.getDeaths()/country.getInfected())));
                    }
                }
                if (textView.getText().length() == 0) {
                    Log.e(TAG, "No data was found for country " + isoCountry + "!");
                    textView.setText("No data available!");
                    ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.NO_DATA_FOUND, null);
                }
            }
        });

        Log.v(TAG, "Requesting all countries...");
        loadingScreen.setProgressBar(25);
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mapViewModel.initCountryList();
                } catch (InterruptedException | ExecutionException e) {
                    Logger.logE(TAG, "Unexpected exception during initialization of country list!", e);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.UNEXPECTED_ERROR, null);
                        }
                    });
                } catch (ClassNotFoundException e) {
                    Logger.logE(TAG, "Exception during loading cache!", e);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.DATA_CORRUPT, null);
                        }
                    });
                    //todo call a method that kills cache
                } catch (JSONException e) {
                    Logger.logE(TAG, "Exception while parsing data!", e);
                } catch (IOException e) {
                    Logger.logE(TAG, "Exception during request!", e);
                    try {
                        APIManager.createAPICall("8.8.8.8");
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CONNECTION_TIMEOUT, null);
                            }
                        });
                    } catch (IOException e1) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.NO_CONNECTION, null);
                            }
                        });
                    }

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
    private void setDataOfBox(TextView textView,long populationWorld,long infectedWorld,long recoveredWorld,long deathsWorld){
        NumberFormat percentFormat=NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(3);
        textView.setText(getString(R.string.map_box_content,populationWorld,"100%",infectedWorld, percentFormat.format((double)infectedWorld*100/populationWorld),recoveredWorld,percentFormat.format((double)recoveredWorld*100/populationWorld),deathsWorld,percentFormat.format((double)deathsWorld*100/populationWorld)));
    }
}