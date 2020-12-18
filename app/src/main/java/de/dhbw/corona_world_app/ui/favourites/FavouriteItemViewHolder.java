package de.dhbw.corona_world_app.ui.favourites;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ui.tools.Pair;
import de.dhbw.corona_world_app.ui.tools.StatisticCallAdapterItemOnActionCallback;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewHolderInterface;

public class FavouriteItemViewHolder extends RecyclerView.ViewHolder implements StatisticCallViewHolderInterface<Pair<String, Boolean>> {
    private final TextView textView;
    private final ImageView imageView;

    public FavouriteItemViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.favouriteItemTextView);
        imageView = view.findViewById(R.id.favouriteItemImageView);
    }

    @Override
    public void setItem(Pair<String, Boolean> Item, int ItemPosition, StatisticCallAdapterItemOnActionCallback onActionCallback) {
        textView.setText(Item.first);
        //TODO Get Colors from Color Resource
        //TODO support Light mode
        imageView.setColorFilter(Color.parseColor(Item.second ? "yellow" : "white"));
        if(onActionCallback!=null)imageView.setOnClickListener(v -> onActionCallback.callback(ItemPosition));
    }

}
