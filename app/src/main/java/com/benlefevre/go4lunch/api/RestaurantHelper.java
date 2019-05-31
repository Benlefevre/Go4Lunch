package com.benlefevre.go4lunch.api;

import com.benlefevre.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createRestaurant(String uid, String name, String mail, String phoneNumber) {
        Restaurant restaurant = new Restaurant(uid, name, mail, phoneNumber);
        return getRestaurantsCollection().document(uid).set(restaurant);
    }

    public static Task<DocumentSnapshot> getRestaurant(String uid){
        return getRestaurantsCollection().document(uid).get();
    }
}
