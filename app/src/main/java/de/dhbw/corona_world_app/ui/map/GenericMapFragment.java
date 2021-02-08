package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;

import android.content.res.Configuration;
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
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Displayable;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;
import de.dhbw.corona_world_app.map.JavaScriptInterface;
import de.dhbw.corona_world_app.map.MapData;
import de.dhbw.corona_world_app.ui.tools.ErrorCode;
import de.dhbw.corona_world_app.ui.tools.ErrorDialog;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

/**
 * This abstract Fragment is used to show the user a {@link WebView} containing a map displaying a heat map of the Corona-virus spread
 * {@link BottomSheetBehavior} is used to display more Information of a selected {@link Displayable}
 * A {@link TextView} is placed in the top right to show summarized Data
 *
 * @param <T> The {@link Displayable} used to select Data
 * @author Thomas Meier ({@link WebView} and Logic)
 * @author Aleksandr Stankoski ({@link BottomSheetBehavior} and Layout)
 */
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

    };

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("unchecked")
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
        //change image based on state
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
                if (selectedCountry != null && mapViewModel.getCurrentResolution().equals(MapData.Resolution.WORLD))
                    root.findViewById(R.id.bottomSheetButton).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        boolean cacheDisabled = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("cache_deactivated", false);
        Log.d(getTAG(), "Initiating view model with cache " + (cacheDisabled ? "disabled" : "enabled") + "...");
        mapViewModel.init(cacheDisabled);

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
                        if (bottomSheet.getHeight() != 0)
                            //calculate how big the half expanded state must be to only show the name and the flag
                            bottomSheetBehavior.setHalfExpandedRatio((getResources().getDimension(R.dimen.bottom_sheet_expand_size) + getResources().getDimension(R.dimen.bottom_sheet_title_size) + (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 0 : getResources().getDimension(R.dimen.map_box_flag_height)) + getResources().getDimension(R.dimen.margin_big)) / (pxToDp(bottomSheet.getHeight()) * getResources().getDisplayMetrics().density));
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
                        selectCountry(countryList.get(i));
                    }
                }
                if (bottomSheetTitle.getText().length() == 0) {
                    Log.e(getTAG(), "No data was found for country " + isoCountry + "!");
                    bottomSheetTitle.setText(getString(R.string.no_data));
                    ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.NO_DATA_FOUND, null);
                }
            }
        });

        mapViewModel.selectedCountry.observe(getViewLifecycleOwner(), selection -> {
            bottomSheetTitle.setText(selection.getName().toString());
            ((ImageView) root.findViewById(R.id.map_box_flag)).setImageDrawable(ContextCompat.getDrawable(requireContext(), selection.getName().getFlagDrawableID()));
            selectedCountry = (Country<T>) selection;
            ((TextView) root.findViewById(R.id.bottomSheetDescription)).setText(getBottomSheetText());
        });

        Log.v(getTAG(), "Requesting all countries...");
        loadingScreen.setProgressBar(25);
        service.execute(() -> {
            Thread executionThread = Thread.currentThread();
            try {
                if (getContext() != null) {
                    executeViewModelListInitiation(mapViewModel);
                }
            } catch (InterruptedException | ExecutionException | IOException | JSONException | ClassNotFoundException e) {
                handleException(e, executionThread);
            }
        });
        mapViewModel.progress.observe(getViewLifecycleOwner(), loadingScreen::setProgressBar);
        getListFromViewModel(mapViewModel).observe(
                getViewLifecycleOwner(), countries ->
                {
                    Log.v(getTAG(), "Requested countries have arrived");
                    webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
                    Log.v(getTAG(), "Loading WebView with WebString...");
                    loadingScreen.setProgressBar(100);
                    myWebView.loadData(webViewString.getValue(), "text/html", "base64");
                });
        return root;
    }

    private void handleException(Exception e, Thread currentThread) {
        if (getContext() != null) {
            if (e instanceof InterruptedException || e instanceof ExecutionException) {
                Logger.logE(getTAG(), "Unexpected exception during initialization of country list!", e);
                requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.UNEXPECTED_ERROR, null));
            } else if (e instanceof ClassNotFoundException || e instanceof InvalidClassException || e instanceof EOFException) {
                Logger.logE(getTAG(), "Exception during loading cache!", e);
                requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.CACHE_CORRUPT, (dialog, which) -> {
                    synchronized (currentThread) {
                        currentThread.notify();
                    }
                }, "OK"));
                try {
                    synchronized (currentThread) {
                        currentThread.wait();
                    }
                    deleteCache(mapViewModel);
                    executeViewModelListInitiation(mapViewModel);
                } catch (IOException ioException) {
                    requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.COULD_NOT_DELETE_CACHE, null));
                } catch (JSONException | ExecutionException | InterruptedException | ClassNotFoundException exception) {
                    handleException(exception, currentThread);
                }
            } else if (e instanceof JSONException) {
                Logger.logE(getTAG(), "Exception while parsing data!", e);
                requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.API_CURRENTLY_NOT_AVAILABLE, null));
            } else if (e instanceof IOException) {
                Logger.logE(getTAG(), "Exception during request!", e);
                try {
                    Logger.logE(getTAG(), "Trying to ping 8.8.8.8 (Google DNS)...");
                    if (APIManager.pingGoogleDNS()) {
                        Logger.logE(getTAG(), "Success!");
                        requireActivity().runOnUiThread(() -> ErrorDialog.showBasicErrorDialog(getContext(), ErrorCode.API_CURRENTLY_NOT_AVAILABLE, null));
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

    protected abstract void deleteCache(MapViewModel viewModel) throws IOException;

    protected String getTAG() {
        return this.getClass().getSimpleName();
    }

    private void selectCountry(Country<T> selectedCountry){
        mapViewModel.selectedCountry.setValue(selectedCountry);
    }


    private void setDataOfBox(TextView textView, long populationWorld, long infectedWorld, long activeWorld, long recoveredWorld, long deathsWorld) {
        if (getContext() != null) {
            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            percentFormat.setMaximumFractionDigits(3);
            textView.setText(getString(getMapBoxFormattedString(), populationWorld, "100%", infectedWorld, percentFormat.format((double) infectedWorld / populationWorld), activeWorld, percentFormat.format((double) activeWorld / populationWorld), recoveredWorld, percentFormat.format((double) recoveredWorld / populationWorld), deathsWorld, percentFormat.format((double) deathsWorld / populationWorld)));
        }
    }

    private int pxToDp(int px) {
        if (getContext() != null) {
            DisplayMetrics displayMetrics = requireContext().getResources().getDisplayMetrics();
            return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        } else {
            return 0;
        }
    }
}