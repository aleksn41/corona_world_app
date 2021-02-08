package de.dhbw.corona_world_app.ui.map;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.ChartType;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.Criteria;
import de.dhbw.corona_world_app.datastructure.StatisticCall;
import de.dhbw.corona_world_app.datastructure.displayables.ISOCountry;

/**
 * This fragment implements the {@link GenericMapFragment} for the Germany map.
 *
 * @author Thomas Meier
 */
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
        return getString(R.string.bottom_sheet_description_world, selectedCountry.getPopulation(), "100%", selectedCountry.getHealthy(), percentageFormat.format((double) selectedCountry.getHealthy() / selectedCountry.getPopulation()), selectedCountry.getInfected(), percentageFormat.format((double) selectedCountry.getInfected() / selectedCountry.getPopulation()), selectedCountry.getActive(), percentageFormat.format((double) selectedCountry.getActive() / selectedCountry.getPopulation()), selectedCountry.getRecovered(), percentageFormat.format((double) selectedCountry.getRecovered() / selectedCountry.getPopulation()), selectedCountry.getDeaths(), percentageFormat.format((double) selectedCountry.getDeaths() / selectedCountry.getPopulation()), percentageFormat.format(selectedCountry.getPop_inf_ratio()), percentageFormat.format((double) selectedCountry.getDeaths() / selectedCountry.getInfected()));
    }

    @Override
    protected int getMapBoxFormattedString() {
        return R.string.map_box_content_world;
    }

    @Override
    protected void deleteCache(MapViewModel viewModel) throws IOException {
        viewModel.deleteWorldCache();
    }
}