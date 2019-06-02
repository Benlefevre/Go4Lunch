package com.benlefevre.go4lunch.api;

import com.benlefevre.go4lunch.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";

    /**
     * Fetch a CollectionReference in Firestore with the collection's name
     *
     * @return CollectionReference according to the query
     */
    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Creates a restaurant in Firestore collection returned by getRestaurantCollection()
     *
     * @param uid         the restaurant's ID key
     * @param name        the restaurant's name
     * @param mail        the restaurant's web URL
     * @param phoneNumber the restaurant's phone number
     * @return nothing
     */
    public static Task<Void> createRestaurant(String uid, String name, String mail, String phoneNumber) {
        Restaurant restaurant = new Restaurant(uid, name, mail, phoneNumber);
        return getRestaurantsCollection().document(uid).set(restaurant);
    }

    /**
     * Updates fields values the Firestore document cooresponding to the uid
     *
     * @param uid          the selected restaurant document
     * @param location     the restaurant's location
     * @param address      the restaurant's address
     * @param rating       the restaurant's rating
     * @param openingHours the restaurant's opening hours
     * @return nothing
     */
    public static Task<Void> updateRestaurantInformations(String uid, LatLng location, String address,
                                                          double rating, List<HashMap<String, String>> openingHours) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("location", location);
        fieldMap.put("address", address);
        fieldMap.put("rating", rating);
        fieldMap.put("openingHours", openingHours);
        return getRestaurantsCollection().document(uid).update(fieldMap);
    }

    /**
     * Updates restaurant's like field in the Firestore document corresponding to the uid
     *
     * @param uid the selected restaurant document
     * @return nothing
     */
    public static Task<Void> updateRestaurantLike(String uid) {
        return getRestaurantsCollection().document(uid).update("like", FieldValue.increment(1));
    }

    /**
     * Fetches a restaurant in the Firestore Collection returned by getRestaurantsCollection()
     *
     * @param uid the wanted restaurant's uid
     * @return a DocumentSnapshot containing the document's fields values.
     */
    public static Task<DocumentSnapshot> getRestaurant(String uid) {
        return getRestaurantsCollection().document(uid).get();
    }
}
