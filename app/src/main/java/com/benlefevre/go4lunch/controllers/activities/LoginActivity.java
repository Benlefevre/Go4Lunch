package com.benlefevre.go4lunch.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.UserHelper;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static com.benlefevre.go4lunch.utils.Constants.IDP_SECRET;
import static com.benlefevre.go4lunch.utils.Constants.IDP_TOKEN;
import static com.benlefevre.go4lunch.utils.Constants.IS_LOGGED;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.PROVIDER;
import static com.benlefevre.go4lunch.utils.Constants.RC_SIGN_IN;

public class LoginActivity extends BaseActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        boolean isLogged = mSharedPreferences.getBoolean(IS_LOGGED, false);

        // If the user is already logged, we display the HomeActivity else we display the login screen
        if (isUserLogged() && isLogged) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else
            createSignInIntent();
    }

    /**
     * Creates an SignIn Intent according the passed parameters in builder.
     */
    private void createSignInIntent() {
        //All the providers for signIn methods
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        //The custom layout for the signIn screen
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setEmailButtonId(R.id.login_mail_btn)
                .setGoogleButtonId(R.id.login_google_btn)
                .setFacebookButtonId(R.id.login_facebook_btn)
                .setTwitterButtonId(R.id.login_twitter_btn)
                .build();

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAlwaysShowSignInMethodScreen(true)
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setIsSmartLockEnabled(false, true)
                .setTheme(R.style.LoginTheme)
                .build(), RC_SIGN_IN);
    }

    /**
     * Creates a user in Firestore with the FirebaseUser's values
     */
    public void createUserInFireStoreWhenUserIsLogged() {
        if (getCurrentUser() != null) {
            FirebaseUser user = getCurrentUser();
            String displayName = user.getDisplayName();
            String mail = null;
            String urlPhoto = null;
            if (user.getEmail() != null)
                mail = user.getEmail();
            if (user.getPhotoUrl() != null)
                urlPhoto = user.getPhotoUrl().toString();

            UserHelper.createUser(user.getUid(), displayName, mail, urlPhoto);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    /**
     * Defines the activity's behavior according to the onActivityResult
     */
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                createUserInFireStoreWhenUserIsLogged();
                if (idpResponse.getProviderType() != null)
                    mSharedPreferences.edit().putString(PROVIDER, idpResponse.getProviderType()).apply();
                mSharedPreferences.edit().putString(IDP_TOKEN, idpResponse.getIdpToken()).apply();
                mSharedPreferences.edit().putString(IDP_SECRET, idpResponse.getIdpSecret()).apply();
                mSharedPreferences.edit().putBoolean(IS_LOGGED,true).apply();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                if (idpResponse == null)
                    Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                else if (idpResponse.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                    Toast.makeText(this, getString(R.string.network_access), Toast.LENGTH_SHORT).show();
                else if (idpResponse.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                    Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
