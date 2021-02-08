package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.datastructure.Country;
import de.dhbw.corona_world_app.datastructure.displayables.GermanyState;
import de.dhbw.corona_world_app.map.MapData;

public class GermanyMapFragment extends GenericMapFragment<GermanyState> {

    private static final String TAG = GermanyMapFragment.class.getSimpleName();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setAdditionalWebViewSettingsOnPageFinished(WebView webView) {
        webView.zoomBy(2.15f);
    }

    @Override
    protected void setAdditionalWebViewSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
    }

    @Override
    protected MutableLiveData<List<Country<GermanyState>>> getListFromViewModel(MapViewModel viewModel) {
        return viewModel.mStatesList;
    }

    @Override
    protected void executeViewModelListInitiation(MapViewModel viewModel) throws InterruptedException, ExecutionException, ClassNotFoundException, JSONException, IOException {
        viewModel.switchResolution(MapData.Resolution.GERMANY);
        viewModel.initGermany();
    }

    @Override
    protected void goToStatistic(View view) {
    }

    @Override
    protected String getBottomSheetText() {
        return getString(R.string.bottom_sheet_description_germany, selectedCountry.getPopulation(), "100%", selectedCountry.getHealthy(), percentageFormat.format((double) selectedCountry.getHealthy() / selectedCountry.getPopulation()), selectedCountry.getInfected(), percentageFormat.format((double) selectedCountry.getInfected() / selectedCountry.getPopulation()),  selectedCountry.getDeaths(), percentageFormat.format((double) selectedCountry.getDeaths() / selectedCountry.getPopulation()), percentageFormat.format(selectedCountry.getPop_inf_ratio()), percentageFormat.format((double) selectedCountry.getDeaths() / selectedCountry.getInfected()));
    }

    @Override
    protected int getMapBoxFormattedString() {
        return R.string.map_box_content_germany;
    }

    @Override
    protected void deleteCache(MapViewModel viewModel) throws IOException {
        viewModel.deleteGermanyCache();
    }
}