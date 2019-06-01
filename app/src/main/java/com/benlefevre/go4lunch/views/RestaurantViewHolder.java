package com.benlefevre.go4lunch.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benlefevre.go4lunch.BuildConfig;
import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.Restaurant;
import com.benlefevre.go4lunch.utils.Constants;
import com.benlefevre.go4lunch.utils.UtilsRestaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benlefevre.go4lunch.utils.Constants.DEFAULT_LOCATION;
import static com.benlefevre.go4lunch.utils.Constants.USER_LAT;
import static com.benlefevre.go4lunch.utils.Constants.USER_LONG;

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
    private SharedPreferences mSharedPreferences;

    public RestaurantViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(Constants.PREFERENCES,Context.MODE_PRIVATE);
        Places.initialize(mContext, BuildConfig.google_maps_key);
        mClient = Places.createClient(mContext);
        ButterKnife.bind(this,itemView);
        itemView.setTag(this);
    }

    /**
     * Updates UI with restaurant's fields value.
     * @param restaurant The item's restaurant.
     */
    public void updateUi(Restaurant restaurant){
        mName.setText(restaurant.getName());
        mAddress.setText(restaurant.getAddress());
        updateHours(restaurant);
        getDistance(restaurant);
        UtilsRestaurant.updateUiAccordingToRating(restaurant.getRating(),mStar1,mStar2,mStar3);
        fetchRestaurantPhoto(restaurant.getUid());
    }

    /**
     * Updates mDistance with the distance between user's location and restaurant's location.
     * @param restaurant The item's restaurant.
     */
    private void getDistance(Restaurant restaurant){
        LatLng userPosition = new LatLng(mSharedPreferences.getFloat(USER_LAT, (float) DEFAULT_LOCATION.latitude),mSharedPreferences.getFloat(USER_LONG, (float) DEFAULT_LOCATION.longitude));
        LatLng restaurantPosition = restaurant.getLocation();
        int distanceBetween = (int) SphericalUtil.computeDistanceBetween(userPosition,restaurantPosition);
        mDistance.setText(mContext.getString(R.string.meters,distanceBetween));
    }

    /**
     * Updates UI according to the restaurant's opening hours.
     * @param restaurant the selected restaurant.
     */
    private void updateHours(Restaurant restaurant) {
        mHours.setText(UtilsRestaurant.displayOpeningHours(restaurant,mContext));
        if (mHours.getText().equals(mContext.getString(R.string.closing_soon)))
            mHours.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        else
            mHours.setTextColor(mContext.getResources().getColor(R.color.greyText));
    }

    /**
     * Fetches a photo from GoogleMaps server according to the item's restaurant's id
     * @param restaurantId the needed restaurant's id to fetch Photo-Metadata and after a photo.
     */
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
