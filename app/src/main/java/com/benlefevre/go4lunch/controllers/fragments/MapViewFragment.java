package com.benlefevre.go4lunch.controllers.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benlefevre.go4lunch.BuildConfig;
import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.RestaurantHelper;
import com.benlefevre.go4lunch.utils.UtilsRestaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.benlefevre.go4lunch.utils.Constants.DEFAULT_LOCATION;
import static com.benlefevre.go4lunch.utils.Constants.PERMISSION_GRANTED;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.USER_LAT;
import static com.benlefevre.go4lunch.utils.Constants.USER_LONG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;

    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Activity mActivity;
    private LatLng mLastKnownLocation;
    private SharedPreferences mSharedPreferences;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private PlacesClient mPlacesClient;

    private List<String> mIdList;

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
     * Initializes needed fields to use GoogleMap and Places APIS.
     */
    private void initMapAndPlaces() {
        if (getArguments() != null)
            mLocationPermissionGranted = getArguments().getBoolean(PERMISSION_GRANTED);
        mLastKnownLocation = new LatLng(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        Places.initialize(mActivity, BuildConfig.google_maps_key);
        mPlacesClient = Places.createClient(mActivity);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        Sets mGoogleMap's style without poi.
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mActivity, R.raw.json_style_map));
//        Request the user's location.
        getLastKnownLocation();
    }

    /**
     * Gets the last known location with google location services and move the GoogleMap's camera to
     * the user's position.
     */
    private void getLastKnownLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        mLastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mSharedPreferences.edit().putFloat(USER_LAT, (float) mLastKnownLocation.latitude).apply();
                        mSharedPreferences.edit().putFloat(USER_LONG, (float) mLastKnownLocation.longitude).apply();
                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastKnownLocation, 18));

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
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS, Place.Field.RATING);
//        Creates the request with the defined fields about the given place.
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();
//        Requests GoogleMap's server to fetch place's details.
        mPlacesClient.fetchPlace(placeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Place place = (task.getResult()).getPlace();
                addMarkerOnMap(place);
                saveRestaurantInFirestore(placeId, place);

            }
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
     *
     * @param place The place for witch to add a marker.
     */
    private void addMarkerOnMap(Place place) {
        if (place.getLatLng() != null)
            mGoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
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
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(List<String> idList);
    }
}
