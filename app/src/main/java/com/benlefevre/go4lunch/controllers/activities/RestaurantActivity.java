package com.benlefevre.go4lunch.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.benlefevre.go4lunch.BuildConfig;
import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.RestaurantHelper;
import com.benlefevre.go4lunch.api.UserHelper;
import com.benlefevre.go4lunch.models.Restaurant;
import com.benlefevre.go4lunch.utils.UtilsRestaurant;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_ADDRESS;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_ID;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;

public class RestaurantActivity extends BaseActivity {

    @BindView(R.id.activity_restaurant_img)
    ImageView mRestaurantImg;
    @BindView(R.id.activity_restaurant_name_txt)
    TextView mNameTxt;
    @BindView(R.id.activity_restaurant_address_txt)
    TextView mAddressTxt;
    @BindView(R.id.activity_restaurant_rating_star)
    ImageView mRatingStar;
    @BindView(R.id.activity_restaurant_rating_star2)
    ImageView mRatingStar2;
    @BindView(R.id.activity_restaurant_rating_star3)
    ImageView mRatingStar3;
    @BindView(R.id.activity_restaurant_floating_button)
    FloatingActionButton mFloatingButton;
    @BindView(R.id.activity_restaurant_call_img)
    ImageView mCallImg;
    @BindView(R.id.activity_restaurant_like_img)
    ImageView mLikeImg;
    @BindView(R.id.activity_restaurant_web_img)
    ImageView mWebImg;
    @BindView(R.id.activity_restaurant_web_txt)
    TextView mActivityRestaurantWebTxt;
    @BindView(R.id.activity_restaurant_frame_layout)
    FrameLayout mFrameLayout;

    private PlacesClient mClient;
    private SharedPreferences mSharedPreferences;

    private String mRestaurantName;
    private String mRestaurantUid;
    private Restaurant mRestaurant;
    private Uri mPhoneUri;
    private Uri mWebUri;
    private String mUserUid;
    private Date mChoiceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);
        mUserUid = getCurrentUser().getUid();
        mSharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        mRestaurantName = getIntent().getStringExtra(RESTAURANT_NAME);
        mChoiceDate = new Date();
        initPlaceApi();
        updateUi();
    }

    /**
     * Initializes Places Google API
     */
    private void initPlaceApi() {
        Places.initialize(this, BuildConfig.google_maps_key);
        mClient = Places.createClient(this);
    }

    /**
     * Calls all needed methods to fetch restaurant's information and update views.
     */
    private void updateUi() {
        RestaurantHelper.getRestaurantsCollection().whereEqualTo("name", mRestaurantName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().get(0) != null) {
                        mRestaurant = queryDocumentSnapshots.getDocuments().get(0).toObject(Restaurant.class);
                        if (mRestaurant != null) {
                            mRestaurantUid = mRestaurant.getUid();
                            mNameTxt.setText(mRestaurant.getName());
                            mAddressTxt.setText(mRestaurant.getAddress());
                            mPhoneUri = Uri.parse("tel:" + mRestaurant.getPhoneNumber());
                            if (mRestaurant.getMail() != null)
                                mWebUri = Uri.parse(mRestaurant.getMail());
                            else {
                                mWebImg.setVisibility(View.GONE);
                                mActivityRestaurantWebTxt.setVisibility(View.GONE);
                            }
                            if (mRestaurant.getRating() != 0.0) {
                                UtilsRestaurant.updateUiAccordingToRating(mRestaurant.getRating(), mRatingStar, mRatingStar2, mRatingStar3);
                            }
                            if (mSharedPreferences.getString(CHOSEN_RESTAURANT_NAME, "defaultRestaurant").equals(mRestaurantName))
                                mFloatingButton.setImageResource(R.drawable.ic_check_circle_24dp);
                            fetchRestaurantPhoto();
                        }
                    }
                });
    }

    /**
     * Fetches the restaurant's photo metadatas from the GoogleMaps server and sets as mRestaurantImg's
     * resource the fetched bitmap.
     */
    private void fetchRestaurantPhoto() {
        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(mRestaurantUid, fields).build();
        mClient.fetchPlace(placeRequest).addOnSuccessListener(fetchPlaceResponse -> {
            if (fetchPlaceResponse.getPlace().getPhotoMetadatas().get(0) != null) {
                Place place = fetchPlaceResponse.getPlace();
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(place.getPhotoMetadatas().get(0)).build();
                mClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    mRestaurantImg.setImageBitmap(bitmap);
                });
            }
        });
    }

    /**
     * Method called in mFloatingButton's onClick
     * Updates the mFloatingButton's image resource according to the user's chosen restaurant.
     * Updates fields values in Firestore according to the user's choice.
     */
    private void updateFloatingActionButton() {
        String restaurantName = mSharedPreferences.getString(CHOSEN_RESTAURANT_NAME, "defaultRestaurant");
        if (!Objects.requireNonNull(restaurantName).equals(mRestaurantName)) {
            mSharedPreferences.edit().putString(CHOSEN_RESTAURANT_NAME, mRestaurantName).apply();
            mSharedPreferences.edit().putString(CHOSEN_RESTAURANT_ID, mRestaurantUid).apply();
            mSharedPreferences.edit().putString(CHOSEN_RESTAURANT_ADDRESS, mRestaurant.getAddress()).apply();
            mFloatingButton.setImageResource(R.drawable.ic_check_circle_24dp);
            UserHelper.updateUserChosenRestaurant(mUserUid, mRestaurantUid, mRestaurantName, mChoiceDate);
        } else {
            mSharedPreferences.edit().remove(CHOSEN_RESTAURANT_NAME).apply();
            mSharedPreferences.edit().remove(CHOSEN_RESTAURANT_ID).apply();
            mSharedPreferences.edit().remove(CHOSEN_RESTAURANT_ADDRESS).apply();
            mFloatingButton.setImageResource(R.drawable.ic_restaurant2_24dp);
            UserHelper.updateUserChosenRestaurant(mUserUid, null, null, null);
        }
    }

    @OnClick({R.id.activity_restaurant_floating_button, R.id.activity_restaurant_call_img, R.id.activity_restaurant_like_img, R.id.activity_restaurant_web_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity_restaurant_floating_button:
                updateFloatingActionButton();
                break;
            case R.id.activity_restaurant_call_img:
                if (mPhoneUri != null)
                    startActivity(new Intent(Intent.ACTION_DIAL, mPhoneUri));
                break;
            case R.id.activity_restaurant_like_img:
                if (!mSharedPreferences.getBoolean(mRestaurantName, false)) {
                    RestaurantHelper.updateRestaurantLike(mRestaurantUid);
                    mSharedPreferences.edit().putBoolean(mRestaurantName, true).apply();
                    Toast.makeText(this, getString(R.string.you_like, mRestaurantName), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_restaurant_web_img:
                if (mWebUri != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, mWebUri));
                break;
        }
    }

}
