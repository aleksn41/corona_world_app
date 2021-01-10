package de.dhbw.corona_world_app.ui.tools;

import androidx.annotation.NonNull;

public interface LoadingScreenInterface {

    public void startLoadingScreen();

    public void endLoadingScreen();

    public void setProgressBar(int progress, @NonNull String message);

    public int getProgress();
}
