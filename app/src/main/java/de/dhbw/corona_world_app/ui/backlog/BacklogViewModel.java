package de.dhbw.corona_world_app.ui.backlog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BacklogViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BacklogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Backlog");
    }

    public LiveData<String> getText() {
        return mText;
    }
}