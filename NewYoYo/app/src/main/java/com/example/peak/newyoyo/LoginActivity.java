package com.example.peak.newyoyo;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String KEY_EMAIL = "KEY_EMAIL";
    public static final String KEY_DISPLAY = "KEY_DISPLAYNAME";
    public static final String KEY_PHOTOPROFILE = "KEY_PHOTOPROFILE";
    public static final String KEY_UID = "KEY_UID";
    public static final String KEY_DEVICE_TOKEN = "KEY_DEVICE";

    private EditText edtusername, edtpassword;
    private Button btnSignIn, btnSignUp, btnSignInFacebook;
    private ProgressBar mProgressBar;

    private String deviceToken;
    private String getdeviceId;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTracker;
    private ProfileTracker mProfileTracker;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        findViewBy();
        mCallbackManager = CallbackManager.Factory.create();
        mAccessTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //getFacebookAcessToken(currentAccessToken);
            }
        };

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };
        mProfileTracker.startTracking();
        mAccessTracker.startTracking();

        btnSignInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFacebookAcessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.i(TAG, error.getMessage());
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.peak.newyoyo",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
    }

    private void getFacebookAcessToken(AccessToken token) {
        if (token.getToken() != null) {
            Log.d(TAG, "handleFacebookAccessToken:" + token);
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Profile profile = Profile.getCurrentProfile();
                                mUser = mAuth.getCurrentUser();
                                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(profile.getProfilePictureUri(1250, 1250))
                                        .setDisplayName(profile.getFirstName() + " " + profile.getLastName())
                                        .build();
                                mUser.updateProfile(changeRequest);
                                getUserUI(mUser);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                getUserUI(null);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            getUserUI(mUser);
        }
    }

    public void Login(View v) {
        String usern = null;
        String pass = null;
        if (edtpassword.length() == 0) {
            edtpassword.requestFocus();
        } else {
            pass = edtpassword.getText().toString();
        }

        if (edtusername.length() == 0) {
            edtusername.requestFocus();
        } else {
            usern = edtusername.getText().toString();
        }

        if (usern != null && pass != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(usern, pass)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                mUser = mAuth.getCurrentUser();
                                if (mUser.getDisplayName() == null) {
                                    Intent itn = new Intent(LoginActivity.this, SetDisplayNameActivity.class);
                                    startActivity(itn);
                                } else {
                                    getUserUI(mUser);
                                }
                            }
                        }
                    });
        }
    }

    public void Signup(View v) {
        String usern = null;
        String pass = null;
        if (edtpassword.length() == 0) {
            edtpassword.requestFocus();
        } else {
            pass = edtpassword.getText().toString();
        }

        if (edtusername.length() == 0) {
            edtusername.requestFocus();
        } else {
            usern = edtusername.getText().toString();
        }

        if (usern != null && pass != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(usern, pass)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                mUser = mAuth.getCurrentUser();
                                if (mUser.getDisplayName() == null) {
                                    Intent itn = new Intent(LoginActivity.this, SetDisplayNameActivity.class);
                                    startActivity(itn);
                                } else {
                                    getUserUI(mUser);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void getUserUI(FirebaseUser user) {
        if (user != null && !user.getDisplayName().equals("")) {
            String extraEmail = user.getEmail();
            String extraDisplayName = user.getDisplayName();
            String extraUid = user.getUid();
            String extraPhotoUrl = user.getPhotoUrl().toString();
            databaseUser(extraUid);

            Intent itn = new Intent();
            itn.setClass(LoginActivity.this, HomeActivity.class);
            itn.putExtra(KEY_DISPLAY, extraDisplayName);
            itn.putExtra(KEY_EMAIL, extraEmail);
            itn.putExtra(KEY_UID, extraUid);
            itn.putExtra(KEY_PHOTOPROFILE, extraPhotoUrl);
            itn.putExtra(KEY_DEVICE_TOKEN, deviceToken);
            startActivity(itn);
        } else if (user != null || user.getDisplayName() == null) {
            Intent itn = new Intent(LoginActivity.this, SetDisplayNameActivity.class);
            startActivity(itn);
        } else {
            Toast.makeText(this, "ไม่มีชื่อผู้ใช้อยู่ในระบบ", Toast.LENGTH_SHORT).show();
        }
    }

    private void findViewBy() {
        edtusername = findViewById(R.id.edit_text_email);
        edtpassword = findViewById(R.id.edit_text_password);
        btnSignIn = findViewById(R.id.button_login);
        btnSignUp = findViewById(R.id.button_signup);
        btnSignInFacebook = findViewById(R.id.button_login_facebook);
        mProgressBar = findViewById(R.id.progressbar_login);
    }

    private void databaseUser(String uid) {
        CollectionReference db = FirebaseFirestore.getInstance().collection("User");
        final DocumentReference mDoc = db.document(uid);
        getdeviceId = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> map = new HashMap<>();
        map.put("device_token", getdeviceId);
        map.put("DisplayName", mUser.getDisplayName());
        map.put("Email", mUser.getEmail());
        map.put("URLImage", String.valueOf(mUser.getPhotoUrl()));
        mDoc.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDoc.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot != null) {
                                            Map map = documentSnapshot.getData();
                                            deviceToken = map.get("device_token").toString();
                                        }
                                    }
                                });
                        Log.i("Firebase : ", "Created user in database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Firebase : ", e.getMessage());
                    }
                });
    }
}
