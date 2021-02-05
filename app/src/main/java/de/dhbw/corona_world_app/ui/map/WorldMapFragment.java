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
import java.util.stream.Collectors;

import de.dhbw.corona_world_app.Logger;
import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ThreadPoolHandler;
import de.dhbw.corona_world_app.api.APIManager;
import de.dhbw.corona_world_app.api.TooManyRequestsException;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;
import de.dhbw.corona_world_app.map.JavaScriptInterface;
import de.dhbw.corona_world_app.map.MapData;
import de.dhbw.corona_world_app.ui.tools.ErrorCode;
import de.dhbw.corona_world_app.ui.tools.ErrorDialog;
import de.dhbw.corona_world_app.ui.tools.LoadingScreenInterface;

public class WorldMapFragment extends GenericMapFragment<ISOCountry> {

    @Override
    protected void setAdditionalWebViewSettingsOnPageFinished(WebView webView) {

    }

    @Override
    protected void setAdditionalWebViewSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
    }

    @Override
    protected MutableLiveData<List<Country<ISOCountry>>> getListFromViewModel(MapViewModel viewModel) {
        return viewModel.mCountryList;
    }

    @Override
    protected void executeViewModelListInitiation(MapViewModel viewModel) throws InterruptedException, ExecutionException, IOException, JSONException, ClassNotFoundException {
        viewModel.initCountryList();
    }

    @Override
    protected void goToStatistic(final View view) {
        StatisticCall request = new StatisticCall(Collections.singletonList(selectedCountry.getName()), ChartType.PIE, Arrays.asList(Criteria.HEALTHY, Criteria.INFECTED, Criteria.RECOVERED, Criteria.DEATHS), StatisticCall.NOW, StatisticCall.NOW);
        WorldMapFragmentDirections.GoToStatistic action = WorldMapFragmentDirections.goToStatistic(request, true);
        NavHostFragment navHostFragment =
                (NavHostFragment) requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        navHostFragment.getNavController().navigate(action);
    }

    @Override
    protected String getBottomSheetText() {
        return getString(R.string.bottom_sheet_description_world, selectedCountry.getPopulation(), "100%", selectedCountry.getHealthy(), percentageFormat.format((double) selectedCountry.getHealthy() / selectedCountry.getPopulation()), selectedCountry.getInfected(), percentageFormat.format((double) selectedCountry.getInfected() / selectedCountry.getPopulation()), selectedCountry.getActive(),  percentageFormat.format((double) selectedCountry.getActive() / selectedCountry.getPopulation()), selectedCountry.getRecovered(), percentageFormat.format((double) selectedCountry.getRecovered() / selectedCountry.getPopulation()), selectedCountry.getDeaths(), percentageFormat.format((double) selectedCountry.getDeaths() / selectedCountry.getPopulation()), percentageFormat.format(selectedCountry.getPop_inf_ratio()), percentageFormat.format((double) selectedCountry.getDeaths() / selectedCountry.getInfected()));
    }

    @Override
    protected int getMapBoxFormattedString() {
        return R.string.map_box_content_world;
    }
}