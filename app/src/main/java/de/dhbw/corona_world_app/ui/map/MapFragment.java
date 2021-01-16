package de.dhbw.corona_world_app.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;

import org.json.JSONException;

import java.io.IOException;
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

    TextRoundCornerProgressBar progressBar;

    private final LoadingScreenInterface loadingScreen = new LoadingScreenInterface() {
        @Override
        public void startLoadingScreen() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void endLoadingScreen() {
            progressBar.setProgress(0);
            progressBar.setProgressText("");
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void setProgressBar(int progress, @NonNull String message) {
            progressBar.setProgress(progress);
            progressBar.setProgressText(message);
        }

        @Override
        public float getProgress() {
            return progressBar.getProgress();
        }
    };

    //todo WebView is not final -> more zoom, clickable tooltips with routing to statistics
    //todo establish order
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "Creating MapFragment view");
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        progressBar = root.findViewById(R.id.progress_bar);
        Log.v(TAG, "Starting loading screen");
        loadingScreen.startLoadingScreen();
        mapViewModel.setPathToCacheDir(requireActivity().getCacheDir());
        loadingScreen.setProgressBar(10, "Starting...");
        boolean cacheDisabled = requireActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("cache_deactivated", false);

        WebView myWebView = root.findViewById(R.id.map_web_view);
        WebSettings webSettings = myWebView.getSettings();
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

        ExecutorService service = ThreadPoolHandler.getInstance();
        Log.v(TAG, "Requesting all countries...");
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    loadingScreen.setProgressBar(25, "Requesting data...");
                    mapViewModel.initCountryList();
                } catch (InterruptedException | ExecutionException | JSONException | IOException | ClassNotFoundException e) {
                    Logger.logE(TAG, "Exception during initiation of country list!", e);
                }
            }
        });
        mapViewModel.mCountryList.observe(getViewLifecycleOwner(), countries -> {
            loadingScreen.setProgressBar(50, "Answer arrived...");
            Log.v(TAG, "Requested countries have arrived");
            loadingScreen.setProgressBar(70, "Decrypting data...");
            webViewString.setValue(mapViewModel.getWebViewStringCustom(countries));
            Log.v(TAG, "Loading WebView with WebString...");
            loadingScreen.setProgressBar(100, "Visualizing data...");
            myWebView.loadData(webViewString.getValue(), "text/html", "base64");
        });
        return root;
    }
}