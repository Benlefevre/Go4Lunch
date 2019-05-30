package com.benlefevre.go4lunch.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.benlefevre.go4lunch.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.jetbrains.annotations.NotNull;

import static com.benlefevre.go4lunch.utils.Constants.PERMISSION_GRANTED;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private boolean mLocationPermissionGranted;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(boolean locationPermissionGranted){
        MapViewFragment mapViewFragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putBoolean(PERMISSION_GRANTED,locationPermissionGranted);
        mapViewFragment.setArguments(args);
        return mapViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            mLocationPermissionGranted = getArguments().getBoolean(PERMISSION_GRANTED);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
    }
}
