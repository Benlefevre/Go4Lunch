package com.benlefevre.go4lunch.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benlefevre.go4lunch.BuildConfig;
import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.Restaurant;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.restaurant_item_name_txt_view)
    TextView mName;
    @BindView(R.id.restaurant_item_adr_txt_view)
    TextView mAddress;
    @BindView(R.id.restaurant_item_hour_txt_view)
    TextView mHours;
    @BindView(R.id.restaurant_item_distance_txt_view)
    TextView mDistance;
    @BindView(R.id.restaurant_item_first_star_img)
    ImageView mStar1;
    @BindView(R.id.restaurant_item_second_star_img)
    ImageView mStar2;
    @BindView(R.id.restaurant_item_third_star_img)
    ImageView mStar3;
    @BindView(R.id.restaurant_item_img_view)
    ImageView mPhoto;
    @BindView(R.id.restaurant_item_nb_user_txt)
    TextView mNbUsers;

    private Context mContext;
    private PlacesClient mClient;

    public RestaurantViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        Places.initialize(mContext, BuildConfig.google_maps_key);
        mClient = Places.createClient(mContext);
        ButterKnife.bind(this,itemView);
    }

    public void updateUi(Restaurant restaurant){
        mName.setText(restaurant.getName());
        mAddress.setText(restaurant.getAddress());
        fetchRestaurantPhoto(restaurant.getUid());

    }

    private void fetchRestaurantPhoto(String restaurantId){
        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(restaurantId,fields).build();
        mClient.fetchPlace(request).addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                Place responsePlace = task.getResult().getPlace();
                if (responsePlace.getPhotoMetadatas() != null){
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(responsePlace.getPhotoMetadatas().get(0)).build();
                    mClient.fetchPhoto(photoRequest).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful() && task1.getResult() != null){
                            Bitmap bitmap = task1.getResult().getBitmap();
                            mPhoto.setImageBitmap(bitmap);
                        }else
                            mPhoto.setImageResource(R.drawable.ic_restaurant2_24dp);
                    });
                }
            }
        });
    }
}
