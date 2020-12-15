package de.dhbw.corona_world_app.ui.favourites;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.dhbw.corona_world_app.R;
import de.dhbw.corona_world_app.ui.tools.StatisticCallViewHolderInterface;

public class FavouriteItemViewHolder extends RecyclerView.ViewHolder implements StatisticCallViewHolderInterface {
        private final TextView textView;
        private final ImageView imageView;

        private View.OnClickListener imageOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getName(),"Image Clicked");
            }
        };

        public FavouriteItemViewHolder(View view) {
            super(view);
            textView =  view.findViewById(R.id.favouriteItemTextView);
            imageView = view.findViewById(R.id.favouriteItemImageView);
            imageView.setOnClickListener(imageOnClickListener);
        }

        @Override
        public void setItem(String Item) {
            textView.setText(Item);
        }
}
