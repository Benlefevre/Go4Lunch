package com.benlefevre.go4lunch.controllers.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.benlefevre.go4lunch.utils.Constants.SIGN_OUT_TASK;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Fetch the current user in Firebase
      * @return The FirebaseUser which is connected
     */
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Verify if the current user is logged in firebase
     * @return a boolean according the current user is null
     */
    protected boolean isUserLogged(){
        return (getCurrentUser() != null);
    }

    /**
     * Log out the current user to Firebase
     */
    protected void signOutFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this,returnOnLoginActivity(SIGN_OUT_TASK));
    }

    /**
     * Starts LoginActivity with an intent when it's onSuccess method is called
     * @param origin a value that defines where the method is called
     * @return just an OnSuccessListener that is used when signOut is called
     */
    protected OnSuccessListener<Void> returnOnLoginActivity(int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
    }
}
