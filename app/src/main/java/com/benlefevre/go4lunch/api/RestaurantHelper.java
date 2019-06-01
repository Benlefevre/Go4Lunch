package com.benlefevre.go4lunch.api;

import com.benlefevre.go4lunch.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createRestaurant(String uid, String name, String mail, String phoneNumber) {
        Restaurant restaurant = new Restaurant(uid, name, mail, phoneNumber);
        return getRestaurantsCollection().document(uid).set(restaurant);
    }

    public static Task<Void> updateRestaurantInformations(String uid,LatLng location, String address,
                                                          double rating, List<HashMap<String,String>> openingHours){
        Map<String,Object> fieldMap = new HashMap<>();
        fieldMap.put("location",location);
        fieldMap.put("address",address);
        fieldMap.put("rating",rating);
        fieldMap.put("openingHours",openingHours);
        return getRestaurantsCollection().document(uid).update(fieldMap);
    }

    public static Task<DocumentSnapshot> getRestaurant(String uid){
        return getRestaurantsCollection().document(uid).get();
    }
}
