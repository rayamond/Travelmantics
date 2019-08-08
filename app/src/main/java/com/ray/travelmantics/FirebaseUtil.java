package com.ray.travelmantics;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 123;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal> mDeals;
    public static ListActivity caller;
    public static boolean isAdmin;

    private FirebaseUtil() {
    }

    public static void openFbReference(String ref, final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                }
            else {
                        String userId = mFirebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                Toast.makeText(callerActivity.getBaseContext(),"welcome back",Toast.LENGTH_LONG).show();
            connectStorage();
                }


                private void checkAdmin(String uid) {
                    FirebaseUtil.isAdmin=false;

                    DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators").child(uid);
                    ChildEventListener listener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            FirebaseUtil.isAdmin=true;
                            caller.showMenu();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    ref.addChildEventListener(listener);
                }

            };


    }
    mDeals = new ArrayList<TravelDeal>();
    mDatabaseReference = mFirebaseDatabase.getReference().child(ref);

}
public static void signIn(){
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());


// Create and launch sign-in intent
    caller.startActivityForResult(
            AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.ic_launcher_foreground)
                    .build(),
            RC_SIGN_IN);
}
public static void attachListener() {
    mFirebaseAuth.addAuthStateListener(mAuthListener);
}
public static void detachListener() {
    mFirebaseAuth.removeAuthStateListener(mAuthListener);
}
public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
}
}
