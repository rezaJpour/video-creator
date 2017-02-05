package net.jackhallam.videocreator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import net.jackhallam.videocreator.model.VideoProject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Constants
    private static final int RC_GOOGLE_LOGIN = 1;

    // Model
    private static List<VideoProject> videoProjects = new ArrayList<>();

    // View
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    ViewPager viewPager;

    // Sign In
    private FirebaseAuth.AuthStateListener authStateListener;
    private OnCompleteListener<AuthResult> onFireBaseSignInCompleteListener;
    private GoogleApiClient googleApiClient;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpFabs();
        setUpViewPager();
        initializeSignInListeners();
        setupGoogleSignIn();
    }

    // ---------------------------------------
    //
    // Model
    //
    // ---------------------------------------

    public static List<VideoProject> getVideoProjects() {
        return videoProjects;
    }

    // ---------------------------------------
    //
    // Controller
    //
    // ---------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    // ---------------------------------------
    //
    // View
    //
    // ---------------------------------------

    private void hideDisplayFAB() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_black_48dp));
                fab1.show();
                fab2.hide();
                break;
            case 1:
                fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_48dp));
                fab1.show();
                fab2.show();
                break;
            default:
                fab2.setVisibility(View.INVISIBLE); // Smoother Transition
                fab1.hide();
                fab2.hide();
        }
    }

    public FloatingActionButton getFab(int whichFab) {
        return whichFab == 1 ? fab1 : fab2;
    }

    private void setUpViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), viewPager);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideDisplayFAB();
            }
        });
        hideDisplayFAB(); // For initial page
    }

    private void setUpFabs() {
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
    }

    // ---------------------------------------
    //
    // Sign In
    //
    // ---------------------------------------

    public void startGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    private void initializeSignInListeners() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null)
                    userLoggedIn();
                else
                    userLoggedOut();
            }
        };
        onFireBaseSignInCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful())
                    showError("Could not sign into Google with FireBase");
            }
        };
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showError(connectionResult.getErrorMessage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                fireBaseAuthWithGoogle(googleSignInResult.getSignInAccount());
            } else {
                showError("Invalid Google Log In");
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, onFireBaseSignInCompleteListener);
    }

    private void userLoggedIn() {
        //TODO: update fragments, etc.
    }

    private void userLoggedOut() {
        //TODO: update fragments, etc.
    }

    // ---------------------------------------
    //
    // Misc.
    //
    // ---------------------------------------

    private void showError(String error) {
        Snackbar.make(findViewById(R.id.project_outer), error, Snackbar.LENGTH_LONG).show();
    }

}
