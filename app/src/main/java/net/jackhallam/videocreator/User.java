package net.jackhallam.videocreator;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import net.jackhallam.videocreator.model.VideoProject;

import java.util.List;

/**
 * Created by jackhallam on 1/30/17.
 */
public class User {
    public static final int RC_GOOGLE_LOGIN = 1;

    private static User instance;
    private State state;

    private enum State {
        LOGGED_OUT, LOGGED_IN, IN_PROJECT
    }

    private User() {
    }

    public static synchronized User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public void logIn(MainActivity mainActivity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mainActivity.getGoogleApiClient());
        mainActivity.startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    public void logOut(OnLogOutListener onLogOutListener) {


        // First, log the user out


        // Then, notify the caller that we are logged out
        onLogOutListener.onLogOut();
    }



    public State getState() {
        return state;
    }

    public List<VideoProject> getVideoProjects() {
        return null;
    }

    public VideoProject getCurrentVideoProject() {
        return null;
    }


    private interface OnLogInListener {
        void onLogInComplete();
        void onLogInFail();
    }

    private interface OnLogOutListener {
        void onLogOut();
    }
}
