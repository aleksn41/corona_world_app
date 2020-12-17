package de.dhbw.corona_world_app.ui.favourites;

import android.graphics.Color;
import android.util.Log;
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
    private Boolean checked = true;

    public FavouriteItemViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.favouriteItemTextView);
        imageView = view.findViewById(R.id.favouriteItemImageView);
    }

    @Override
    public void setItem(Pair<String, Boolean> Item, int ItemPosition, StatisticCallAdapterItemOnActionCallback onActionCallback) {
        textView.setText(Item.first);
        //TODO Get Colors from Color Resource
        imageView.setColorFilter(Color.parseColor(Item.second ? "white" : "yellow"));
        checked=Item.second;
        if(onActionCallback!=null)imageView.setOnClickListener(v -> {
            //TODO support Light mode
            checked = !checked;
            Log.d(this.getClass().getName(), (checked ? "Uncheck Favourite " : "Check Favourite ")+textView.getText());
            imageView.setColorFilter(Color.parseColor(checked ? "white" : "yellow"));
            //switch checked State
            onActionCallback.callback(ItemPosition);
        });
    }

}
