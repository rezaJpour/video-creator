package net.jackhallam.videocreator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.jackhallam.videocreator.model.VideoProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Constants
    private static final int RC_GOOGLE_LOGIN = 1;
    private static final String USERS = "users";
    private static final String CLIPS_STORAGE_PATH = "gs://video-creator-1a177.appspot.com/clips/";

    // Model
    private DatabaseReference userDatabaseReference;
    StorageReference clipsFolderStorageReference;
    private List<VideoProject> videoProjects = new ArrayList<>();
    private VideoProjectsListener videoProjectsListener;
    private String currentVideoProject;

    // View
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;

    // Controller
    private UserState userState = UserState.NOT_LOGGED_IN;

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

    public List<VideoProject> getVideoProjects() {
        return videoProjects;
    }

    public void setVideoProjectListener(VideoProjectsListener videoProjectsListener) {
        this.videoProjectsListener = videoProjectsListener;
    }

    public void addVideoProject(VideoProject videoProject) {
        userDatabaseReference.push().setValue(videoProject);
    }

    private class VideoProjectsChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // TODO: This fixes a weird problem where we are trying to add duplicates
            for (int i = 0; i < videoProjects.size(); i++)
                if (videoProjects.get(i).getKey().equals(dataSnapshot.getKey()))
                    return;
            VideoProject videoProject = dataSnapshot.getValue(VideoProject.class);
            videoProject.setKey(dataSnapshot.getKey());
            videoProjects.add(videoProject);
            if (videoProjectsListener != null)
                videoProjectsListener.update();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            VideoProject videoProject = dataSnapshot.getValue(VideoProject.class);
            videoProject.setKey(dataSnapshot.getKey());
            for (int i = 0; i < videoProjects.size(); i++) {
                if (dataSnapshot.getKey().equals(videoProjects.get(i).getKey())) {
                    videoProjects.set(i, videoProject);
                    if (videoProjectsListener != null)
                        videoProjectsListener.update();
                    return;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            for (int i = 0; i < videoProjects.size(); i++) {
                if (dataSnapshot.getKey().equals(videoProjects.get(i).getKey())) {
                    videoProjects.remove(i);
                    if (videoProjectsListener != null)
                        videoProjectsListener.update();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }

    public void storeVideo(InputStream in/*String pathToVideo/*, OnFailureListener onFailureListener, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener*/) throws FileNotFoundException {
        //InputStream stream = new FileInputStream(new File(pathToVideo));
        UploadTask uploadTask = clipsFolderStorageReference.child("samplevideo.mp4").putStream(in);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("d", "onSuccess _____________________________");
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("d", "onFailure _____________________________");
            }
        });
       // uploadTask.addOnFailureListener(onFailureListener).addOnSuccessListener(onSuccessListener);
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

    public enum UserState {
        NOT_LOGGED_IN, NO_PROJECT, PROJECT
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
        myPagerAdapter.notifyDataSetChanged();
    }

    public ViewPager getViewPager() {
        return viewPager;
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

    private void setUpFabs() {
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
    }

    private void setUpViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this, viewPager);
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
        userDatabaseReference = FirebaseDatabase.getInstance().getReference(USERS + "/" + firebaseUser.getUid());
        userDatabaseReference.addChildEventListener(new VideoProjectsChildEventListener());
        clipsFolderStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(CLIPS_STORAGE_PATH);
        setUserState(UserState.NO_PROJECT);
        myPagerAdapter.userLoggedIn();
    }

    private void userLoggedOut() {
        setUserState(UserState.NOT_LOGGED_IN);
        myPagerAdapter.userLoggedOut();
        userDatabaseReference = null;
        clipsFolderStorageReference = null;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
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
