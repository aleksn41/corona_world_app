package de.dhbw.corona_world_app.ui.info;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.dhbw.corona_world_app.R;

/**
 * This Fragment shows a text with basic Information to the App
 * @author Aleksandr Stankoski
 */
public class InfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_info, container, false);
        TextView text=root.findViewById(R.id.info_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        return root;
    }
}
