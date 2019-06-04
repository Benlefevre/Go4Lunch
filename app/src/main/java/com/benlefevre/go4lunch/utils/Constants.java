package com.benlefevre.go4lunch.utils;

import com.google.android.gms.maps.model.LatLng;

public class Constants {

    public static final String PREFERENCES = "sharedPreferences";
    public static final int RC_SIGN_IN = 123;
    public static final int SIGN_OUT_TASK = 123;
    public static final int AUTOCOMPLETE_REQUEST = 456;

    public static final String USER_NAME = "userName";

    public static final String PROVIDER = "provider";
    public static final String IDP_TOKEN = "idpToken";
    public static final String IDP_SECRET = "idpSecret";
    public static final String MESS_TOKEN = "messagingToken";
    public static final String MESS_TOKEN_CHANGED = "massagingTokenChanged";

    public static final String RESTAURANT = "restaurant";
    public static final String MAP = "map";
    public static final String WORKMATES = "workmates";

    public static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    public static final String PERMISSION_GRANTED = "permission";

    public static final LatLng DEFAULT_LOCATION = new LatLng(48.852686, 2.337957);

    public static final String ORIGIN = "origin";
    public static final String ID_LIST = "idList";

    public static final String MAPVIEW = "mapView";
    public static final String RESTAURANT_FRAGMENT = "restaurantFragment";
    public static final String WORKMATE_FRAGMENT = "workmateFragment";
    public static final String RESTAURANT_ACTIVITY = "restaurantActivity";

    public static final String USER_LAT = "userLat";
    public static final String USER_LONG = "userLong";
    public static final String LAT_NORTH = "latNorth";
    public static final String LONG_NORTH = "longNorth";
    public static final String LAT_SOUTH = "latSouth";
    public static final String LONG_SOUTH = "longSouth";

    public static final String RESTAURANT_NAME = "restaurantName";

    public static final String CHOSEN_RESTAURANT_NAME = "chosenRestaurantName";
    public static final String CHOSEN_RESTAURANT_ADDRESS = "chosenRestaurantAddress";
    public static final String CHOSEN_RESTAURANT_ID = "chosenRestaurantId";
}
