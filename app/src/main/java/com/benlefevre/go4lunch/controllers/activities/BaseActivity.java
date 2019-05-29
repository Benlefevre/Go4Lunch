package com.benlefevre.go4lunch.controllers.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int SIGN_OUT_TASK = 123 ;

    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected boolean isUserLogged(){
        return (getCurrentUser() != null);
    }

    protected void signOutFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this,returnOnLoginActivity(SIGN_OUT_TASK));
    }

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
