package com.benlefevre.go4lunch.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;

import java.util.Objects;

import static com.benlefevre.go4lunch.utils.Constants.IDP_SECRET;
import static com.benlefevre.go4lunch.utils.Constants.IDP_TOKEN;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.PROVIDER;
import static com.benlefevre.go4lunch.utils.Constants.SIGN_OUT_TASK;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Fetch the current user in Firebase
     *
     * @return The FirebaseUser which is connected
     */
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Verify if the current user is logged in firebase
     *
     * @return a boolean according the current user is null
     */
    protected boolean isUserLogged() {
        return (getCurrentUser() != null);
    }

    /**
     * Log out the current user to Firebase
     */
    protected void signOutFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, returnOnLoginActivity(SIGN_OUT_TASK));
    }

    /**
     * Starts LoginActivity with an intent when it's onSuccess method is called
     *
     * @param origin a value that defines where the method is called
     * @return just an OnSuccessListener that is used when signOut is called
     */
    protected OnSuccessListener<Void> returnOnLoginActivity(int origin) {
        return aVoid -> {
            if (origin == SIGN_OUT_TASK) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };
    }

    /**
     * Deletes the user's account into Firebase Authentication and the document corresponding to
     * the user into Firestore.
     */
    protected void deleteUserAccountInFirebase() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        Context context = this;
        AuthUI.getInstance()
                .delete(context)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, getString(R.string.account_deleted), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    } else {
                        String provider = sharedPreferences.getString(PROVIDER, "");
                        String idpToken = sharedPreferences.getString(IDP_TOKEN, "");
                        String idpSecret = sharedPreferences.getString(IDP_SECRET, "");
                        AuthCredential authCredential;
                        if (Objects.requireNonNull(provider).equals(EmailAuthProvider.PROVIDER_ID)) {
                            Toast.makeText(context, getString(R.string.re_auth_to_delete), Toast.LENGTH_SHORT).show();
                            signOutFromFirebase();
                            return;
                        } else {
                            authCredential = getAuthCredential(provider, idpToken, idpSecret);
                            getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(task1 -> deleteUserAccountInFirebase());
                        }
                    }
                });
        UserHelper.deleteUser(getCurrentUser().getUid());
    }

    private AuthCredential getAuthCredential(String provider, String idpToken, String idpSecret) {
        switch (provider) {
            case GoogleAuthProvider.PROVIDER_ID:
                return GoogleAuthProvider.getCredential(idpToken, null);
            case FacebookAuthProvider.PROVIDER_ID:
                return FacebookAuthProvider.getCredential(idpToken);
            case TwitterAuthProvider.PROVIDER_ID:
                return TwitterAuthProvider.getCredential(idpToken, idpSecret);
            default:
                return null;
        }
    }
}
