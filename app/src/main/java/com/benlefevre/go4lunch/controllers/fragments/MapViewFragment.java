package com.benlefevre.go4lunch.controllers.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benlefevre.go4lunch.BuildConfig;
import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.RestaurantHelper;
import com.benlefevre.go4lunch.api.UserHelper;
import com.benlefevre.go4lunch.controllers.activities.RestaurantActivity;
import com.benlefevre.go4lunch.models.User;
import com.benlefevre.go4lunch.utils.UtilsRestaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.maps.android.ui.IconGenerator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.benlefevre.go4lunch.utils.Constants.DEFAULT_LOCATION;
import static com.benlefevre.go4lunch.utils.Constants.LAT_NORTH;
import static com.benlefevre.go4lunch.utils.Constants.LAT_SOUTH;
import static com.benlefevre.go4lunch.utils.Constants.LONG_NORTH;
import static com.benlefevre.go4lunch.utils.Constants.LONG_SOUTH;
import static com.benlefevre.go4lunch.utils.Constants.PERMISSION_GRANTED;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.USER_LAT;
import static com.benlefevre.go4lunch.utils.Constants.USER_LONG;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;

    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Activity mActivity;
    private LatLng mLastKnownLocation;
    private SharedPreferences mSharedPreferences;

    private ListenerRegistration mEventListener;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private PlacesClient mPlacesClient;

    private List<String> mIdList;
    private Bitmap mBitmapOrange;
    private Bitmap mBitmapGreen;
    private List<User> mUserList;
    private List<Place> mPlaceList;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(List<String> idList);
    }

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(boolean locationPermissionGranted) {
        MapViewFragment mapViewFragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putBoolean(PERMISSION_GRANTED, locationPermissionGranted);
        mapViewFragment.setArguments(args);
        return mapViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mSharedPreferences = Objects.requireNonNull(mActivity).getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        mIdList = new ArrayList<>();
        mPlaceList = new ArrayList<>();
        fetchUsersInFirestore();
        initMapAndPlaces();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        mMapView = view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return view;
    }

    /**
     * Fetches all users in Firebase ans sets a listener to update mUserList if a user chose a restaurant
     */
    private void fetchUsersInFirestore() {
        UserHelper.getUsersCollection().get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty())
                mUserList = queryDocumentSnapshots.toObjects(User.class);
        });
        Query query = UserHelper.getUsersCollection();
        mEventListener = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null)
                return;
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                mUserList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    mUserList.add(documentSnapshot.toObject(User.class));
                }
                addMarkerOnMap();
            }
        });
    }

    /**
     * Initializes needed fields to use GoogleMap and Places APIS.
     */
    private void initMapAndPlaces() {
        if (getArguments() != null)
            mLocationPermissionGranted = getArguments().getBoolean(PERMISSION_GRANTED);
        mLastKnownLocation = new LatLng(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        Places.initialize(mActivity, BuildConfig.google_maps_key);
        mPlacesClient = Places.createClient(mActivity);
        generateBitmapToMarkers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        Sets mGoogleMap's style without poi.
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.json_style_map));
//        Sets the realized action when user click on a Marker's InfoWindow
        mGoogleMap.setOnInfoWindowClickListener(marker -> {
            Intent intent = new Intent(mActivity, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_NAME, marker.getTitle());
            intent.putExtra(RESTAURANT_ID,marker.getSnippet());
            startActivity(intent);
        });
//        Sets the realized action when user click on a Marker
        mGoogleMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle().equals(marker.getTag())){
                marker.setTag(null);
                Intent intent = new Intent(mActivity, RestaurantActivity.class);
                intent.putExtra(RESTAURANT_NAME, marker.getTitle());
                intent.putExtra(RESTAURANT_ID,marker.getSnippet());
                startActivity(intent);
            } else{
                marker.showInfoWindow();
                marker.setTag(marker.getTitle());
            }
            return true;
        });
//        Defines witch marker's data are bind in InfoWindow
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.infowindow_item,null);
                TextView textView = view.findViewById(R.id.infowindow_twt);
                textView.setText(marker.getTitle());
                return view;
            }
        });
//        Request the user's location to GooglePlay services.
        getLastKnownLocation();
    }

    /**
     * Gets the last known location with google location services and move the GoogleMap's camera to
     * the user's position.
     * Saves in SharedPreferences the last known user's location and the LatLngBound to constrain
     * Autocomplete's queries.
     */
    private void getLastKnownLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        mLastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastKnownLocation, 19));
                        LatLngBounds bound = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
                        mSharedPreferences.edit().putFloat(USER_LAT, (float) mLastKnownLocation.latitude).apply();
                        mSharedPreferences.edit().putFloat(USER_LONG, (float) mLastKnownLocation.longitude).apply();
                        mSharedPreferences.edit().putFloat(LAT_NORTH, (float) bound.northeast.latitude).apply();
                        mSharedPreferences.edit().putFloat(LONG_NORTH, (float) bound.northeast.longitude).apply();
                        mSharedPreferences.edit().putFloat(LAT_SOUTH, (float) bound.southwest.latitude).apply();
                        mSharedPreferences.edit().putFloat(LONG_SOUTH, (float) bound.southwest.longitude).apply();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception : $%s", e.getMessage());
        }
        fetchPlacesAroundUser();
    }

    /**
     * Fetches places around the user's location into GoogleMaps server
     */
    @SuppressLint("MissingPermission")
    private void fetchPlacesAroundUser() {
//        Defines witch fields we want in the query's response.
        List<Place.Field> fields = Arrays.asList(Place.Field.TYPES, Place.Field.ID);
//        Creates the request with the defined fields.
        FindCurrentPlaceRequest placeRequest = FindCurrentPlaceRequest.builder(fields).build();
//        Requests GoogleMap's server to fetch places around user
        mPlacesClient.findCurrentPlace(placeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                FindCurrentPlaceResponse response = task.getResult();
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                        Verifies if the place's type corresponding to restaurants.
                    if (placeLikelihood.getPlace().getTypes() != null &&
                            (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)
                                    || placeLikelihood.getPlace().getTypes().contains(Place.Type.BAR) ||
                                    placeLikelihood.getPlace().getTypes().contains(Place.Type.MEAL_TAKEAWAY))) {
//                        Adds each place's id in a list to pass it to HomeActivity
                        mIdList.add(placeLikelihood.getPlace().getId());
//                        Requests details for each place corresponding to wanted types.
                        fetchDetailsAboutRestaurants(placeLikelihood.getPlace().getId());
                    }
                }
//                Sends the mIdList to HomeActivity for create a restaurant list into RestaurantFragment.
                mListener.onFragmentInteraction(mIdList);
            }
        });
    }

    /**
     * Fetches place's details according to the place's Id passed in argument.
     *
     * @param placeId The place's id that we want details.
     */
    private void fetchDetailsAboutRestaurants(String placeId) {
//        Defines witch fields we want in the query's response.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS, Place.Field.RATING);
//        Creates the request with the defined fields about the given place.
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();
//        Requests GoogleMap's server to fetch place's details.
        mPlacesClient.fetchPlace(placeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Place place = (task.getResult()).getPlace();
                mPlaceList.add(place);
                saveRestaurantInFirestore(placeId, place);
            }
            if (mPlaceList.size() == mIdList.size())
                addMarkerOnMap();
        });
    }

    /**
     * Saves a restaurant in a Firestore's Document if it doesn't already exist.
     *
     * @param placeId the place's id we want save in Firestore.
     * @param place   the place containing details returned by GoogleMaps server.
     */
    private void saveRestaurantInFirestore(String placeId, Place place) {
        String webUrl, address;
        double rating;
        List<HashMap<String, String>> hours;
        webUrl = (place.getWebsiteUri() != null) ? place.getWebsiteUri().toString() : null;
        address = UtilsRestaurant.formatAddress(place.getAddress(), place.getAddressComponents().asList());
        hours = UtilsRestaurant.getOpeningHours(place.getOpeningHours());
        rating = (place.getRating() != null) ? place.getRating() : 0.0;

        RestaurantHelper.getRestaurant(placeId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (!documentSnapshot.exists()) {
                    RestaurantHelper.createRestaurant(placeId, place.getName(), webUrl, place.getPhoneNumber());
                    RestaurantHelper.updateRestaurantInformations(placeId, place.getLatLng(), address, rating, hours);
                }
            }
        });
    }

    /**
     * Adds a marker on map according to the place's location and the place's name.
     */
    private void addMarkerOnMap() {
        if(mGoogleMap != null)
            mGoogleMap.clear();
        if (mPlaceList != null && !mPlaceList.isEmpty()) {
            for (Place place : mPlaceList) {
                int count = 0;
                for (User user : mUserList) {
                    if (user.getRestaurantId() != null && user.getRestaurantId().equals(place.getId())) {
                        count++;
                    }
                }
                if (place.getLatLng() != null) {
                    if (count > 0)
                        mGoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()).snippet(place.getId())
                                .title(place.getName()).icon(BitmapDescriptorFactory.fromBitmap(mBitmapGreen)));
                    else
                        mGoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()).snippet(place.getId())
                                .title(place.getName()).icon(BitmapDescriptorFactory.fromBitmap(mBitmapOrange)));
                }
            }
        }
    }

    /**
     * Move the map's camera to the selected restaurant's.
     *
     * @param restaurantName the restaurant's name.
     */
    public void moveCameraToSelectedRestaurant(String restaurantName) {
        for (Place place:mPlaceList){
            if (Objects.equals(place.getName(), restaurantName) && place.getLatLng() != null)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        place.getLatLng().latitude,place.getLatLng().longitude), 22));
        }
    }

    /**
     * Creates 2 bitmaps from drawable resources.
     */
    private void generateBitmapToMarkers() {
        IconGenerator iconGenerator = new IconGenerator(mActivity);
        iconGenerator.setBackground(getResources().getDrawable(R.drawable.house_location_marker_orange));
        mBitmapOrange = iconGenerator.makeIcon();
        iconGenerator.setBackground(getResources().getDrawable(R.drawable.house_location_green_marker));
        mBitmapGreen = iconGenerator.makeIcon();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MapViewFragment.OnFragmentInteractionListener)
            mListener = (OnFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        addMarkerOnMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mEventListener.remove();
    }
}
