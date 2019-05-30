package com.benlefevre.go4lunch.api;

import com.benlefevre.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    /**
     * Fetch a CollectionReference in Firestore with the collection's name
     * @return CollectionReference according to the query
     */
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Creates a user in Firestore
     * @param uid the user's ID key
     * @param displayName the user's name
     * @param mail the user's mail
     * @param urlPhoto the user's photo's url
     * @return nothing
     */
    public static Task<Void> createUser(String uid, String displayName, String mail, String urlPhoto){
        User user = new User(uid,displayName,mail,urlPhoto);
        return getUsersCollection().document(uid).set(user);
    }

    /**
     * Fetchs an user in Firestore's collection defines in getUsersCollection
     * @param uid the user's ID key that we want fetch
     * @return a DocumentSnapshot of our query
     */
    public static Task<DocumentSnapshot> getUser(String uid){
        return getUsersCollection().document(uid).get();
    }

    /**
     * Deletes into Firestore the document corresponding to the selected user
     * @param uid The selected user's uid
     */
    public static Task<Void> deleteUser(String uid){
        return getUsersCollection().document(uid).delete();
    }
}
