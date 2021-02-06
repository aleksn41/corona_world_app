package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.Displayable;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;
import de.dhbw.corona_world_app.map.JavaScriptInterface;
import de.dhbw.corona_world_app.map.MapData;
import de.dhbw.corona_world_app.ui.tools.ErrorCode;
import de.dhbw.corona_world_app.ui.tools.ErrorDialog;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

public abstract class GenericMapFragment<T extends Displayable> extends Fragment {

    private MapViewModel mapViewModel;

    MutableLiveData<String> webViewString = new MutableLiveData<>();

    LinearProgressIndicator progressBar;

    TextView bottomSheetTitle;

    ImageView bottomSheetExpandImage;

    boolean bottomSheetDirectionUp = true;

    Country<T> selectedCountry;

    NumberFormat percentageFormat = NumberFormat.getPercentInstance();

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

    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(getTAG(), "Creating MapFragment view");
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        progressBar = root.findViewById(R.id.progressBar);
        bottomSheetTitle = root.findViewById(R.id.bottomSheetTitle);
        Log.v(getTAG(), "Starting loading screen");
        loadingScreen.startLoadingScreen();
        mapViewModel.setPathToCacheDir(requireActivity().getCacheDir());
        loadingScreen.setProgressBar(10);
        percentageFormat.setMaximumFractionDigits(3);
        root.findViewById(R.id.bottomSheetButton).setOnClickListener(this::goToStatistic);
        //setup bottomsheet
        RelativeLayout bottomSheet = root.findViewById(R.id.bottomSheet);
        BottomSheetBehavior<RelativeLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetExpandImage = root.findViewById(R.id.bottom_sheet_expand);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setFitToContents(false);
        //listeners for bottom sheet
        //click event for show-dismiss bottom sheet
        bottomSheet.setOnClickListener(view -> {
            switch (bottomSheetBehavior.getState()) {
                case BottomSheetBehavior.STATE_EXPANDED:
                case BottomSheetBehavior.STATE_COLLAPSED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    if (!bottomSheetDirectionUp) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_DRAGGING:
                case BottomSheetBehavior.STATE_HIDDEN:
                case BottomSheetBehavior.STATE_SETTLING:
                    break;
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetExpandImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_expand_more_24));
                        bottomSheetDirectionUp = false;
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetExpandImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_expand_less_24));
                        bottomSheetDirectionUp = true;
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
                if (selectedCountry != null && mapViewModel.getCurrentResolution().equals(MapData.Resolution.WOLRD))
                    root.findViewById(R.id.bottomSheetButton).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        boolean cacheDisabled = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("cache_deactivated", false);
        boolean storageDisabled = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("storage_deactivated", false);
        Log.d(getTAG(), "Initiating view model with cache " + (cacheDisabled ? "disabled" : "enabled") + " and storage " + (storageDisabled ? "disabled" : "enabled") + "...");
        mapViewModel.init(cacheDisabled, storageDisabled);
        WebView myWebView = root.findViewById(R.id.map_web_view);
        WebSettings webSettings = myWebView.getSettings();

        myWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("ClickableViewAccessibility")
            public void onPageFinished(WebView view, String url) {
                loadingScreen.setProgressBar(100);
                myWebView.setOnTouchListener(null);
                setAdditionalWebViewSettingsOnPageFinished(myWebView);
                TextView mapBox = root.findViewById(R.id.mapBox);
                Country<ISOCountry> boxCountry = mapViewModel.mBoxValue.getValue();
                setDataOfBox(mapBox, boxCountry.getPopulation(), boxCountry.getInfected(), boxCountry.getActive(), boxCountry.getRecovered(), boxCountry.getDeaths());
                bottomSheet.setVisibility(View.VISIBLE);
                bottomSheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        bottomSheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //if the user switches between fragments very quickly the Fragment is stopped but still activated this listener
                        if(bottomSheet.getHeight()!=0)bottomSheetBehavior.setHalfExpandedRatio((float) 152 / pxToDp(bottomSheet.getHeight()));
                    }
                });
                mapBox.setVisibility(View.VISIBLE);
                loadingScreen.endLoadingScreen();
            }
        });
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        setAdditionalWebViewSettings(myWebView);
        JavaScriptInterface jsInterface = new JavaScriptInterface();
        myWebView.addJavascriptInterface(jsInterface, "jsinterface");
        ExecutorService service = ThreadPoolHandler.getInstance();

        jsInterface.current.observe(getViewLifecycleOwner(), isoCountry -> {
            if (isoCountry != null) {
                List<Country<T>> countryList = getListFromViewModel(mapViewModel).getValue();
                if (countryList == null)
                    throw new IllegalStateException("Country list was not initialized correctly!");
                for (int i = 0; i < countryList.size(); i++) {
                    if (countryList.get(i).getName().equals(isoCountry)) {
                        ((ImageView) root.findViewById(R.id.map_box_flag)).setImageDrawable(ContextCompat.getDrawable(requireContext(), isoCountry.getFlagDrawableID()));
                        selectedCountry = countryList.get(i);
                        bottomSheetTitle.setText(isoCountry.toString());
                        ((TextView) root.findViewById(R.id.bottomSheetDescription)).setText(getBottomSheetText());
                    }
                }
                if (bottomSheetTitle.getText().length() == 0) {
                    Log.e(getTAG(), "No data was found for country " + isoCountry + "!");
                    bottomSheetTitle.setText("No data available!");
                    ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.NO_DATA_FOUND, null);
                }
            }
        });

        Log.v(getTAG(), "Requesting all countries...");
        loadingScreen.setProgressBar(25);
        service.execute(() -> {
            try {
                if(getContext()!=null) {
                    executeViewModelListInitiation(mapViewModel);
                }
            } catch (InterruptedException | ExecutionException | IOException | JSONException | ClassNotFoundException e) {
                handleException(e);
            }
        });
        getListFromViewModel(mapViewModel).observe(
                getViewLifecycleOwner(), countries ->
                {
                    loadingScreen.setProgressBar(50);
                    Log.v(getTAG(), "Requested countries have arrived");
                    loadingScreen.setProgressBar(70);
                    webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
                    Log.v(getTAG(), "Loading WebView with WebString...");
                    loadingScreen.setProgressBar(100);
                    myWebView.loadData(webViewString.getValue(), "text/html", "base64");
                });
        return root;
    }

    private void handleException(Exception e) {
        if(getContext()!=null) {
            if (e instanceof InterruptedException || e instanceof ExecutionException) {
                Logger.logE(getTAG(), "Unexpected exception during initialization of country list!", e);
                requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.UNEXPECTED_ERROR, null));
            } else if (e instanceof ClassNotFoundException) {
                Logger.logE(getTAG(), "Exception during loading cache!", e);
                requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.DATA_CORRUPT, null));
                //todo call a method that kills cache
            } else if (e instanceof JSONException) {
                Logger.logE(getTAG(), "Exception while parsing data!", e);
                //todo inform user
            } else if (e instanceof IOException) {
                Logger.logE(getTAG(), "Exception during request!", e);
                try {
                    Logger.logE(getTAG(), "Trying to ping 8.8.8.8 (Google DNS)...");
                    if (APIManager.pingGoogleDNS()) {
                        Logger.logE(getTAG(), "Success!");
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CONNECTION_TIMEOUT, null));
                    } else {
                        Logger.logE(getTAG(), "Failure!");
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.NO_CONNECTION, null));
                    }
                } catch (IOException e1) {
                    Logger.logE(getTAG(), "Failure with Exception!", e1);
                    requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.NO_CONNECTION, null));
                }
            }
        }
    }

    protected abstract void setAdditionalWebViewSettingsOnPageFinished(WebView webView);

    protected abstract void setAdditionalWebViewSettings(WebView webView);

    protected abstract MutableLiveData<List<Country<T>>> getListFromViewModel(MapViewModel viewModel);

    protected abstract void executeViewModelListInitiation(MapViewModel viewModel) throws InterruptedException, ExecutionException, IOException, JSONException, ClassNotFoundException;

    protected abstract void goToStatistic(final View view);

    protected abstract String getBottomSheetText();

    protected abstract int getMapBoxFormattedString();

    protected String getTAG(){
        return this.getClass().getSimpleName();
    }
    
    private void setDataOfBox(TextView textView, long populationWorld, long infectedWorld, long activeWorld, long recoveredWorld, long deathsWorld) {
        if(getContext()!=null) {
            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            percentFormat.setMaximumFractionDigits(3);
            textView.setText(getString(getMapBoxFormattedString(), populationWorld, "100%", infectedWorld, percentFormat.format((double) infectedWorld / populationWorld), activeWorld, percentFormat.format((double) activeWorld / populationWorld), recoveredWorld, percentFormat.format((double) recoveredWorld / populationWorld), deathsWorld, percentFormat.format((double) deathsWorld / populationWorld)));
        }
    }

    private int pxToDp(int px) {
        if(getContext()!=null) {
            DisplayMetrics displayMetrics = requireContext().getResources().getDisplayMetrics();
            return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        } else {
            return 0;
        }
    }
}