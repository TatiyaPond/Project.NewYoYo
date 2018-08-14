package com.example.peak.newyoyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.peak.newyoyo.LoginActivity.KEY_DISPLAY;
import static com.example.peak.newyoyo.LoginActivity.KEY_EMAIL;
import static com.example.peak.newyoyo.LoginActivity.KEY_PHOTOPROFILE;
import static com.example.peak.newyoyo.LoginActivity.KEY_UID;

public class SetDisplayNameActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText edtNameDisplay;
    private Button btnSaveName;
    private ProgressBar mProgressBar;

    private static final int requsetImage = 1;
    private Uri uriImage;

    private String urlImageProfile;
    private Bitmap resizeBitmap;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_display_name);
        findViewBy();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "" + user.getUid(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "user null", Toast.LENGTH_SHORT).show();
        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebaseStorage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
        finish();
    }

    private void showImageChooser() {
        Intent itn = new Intent();
        itn.setType("image/*");
        itn.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(itn, "เลือกรูปภาพ"), requsetImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requsetImage && resultCode == RESULT_OK && data != null) {
            uriImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(uriImage);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                int bitmapHeight = bitmap.getHeight();
                int bitmapWidth = bitmap.getWidth();
                if (bitmapHeight < bitmapWidth) {
                    resizeBitmap = Bitmap.createScaledBitmap(bitmap, 1650, 1300, true);
                    imgProfile.setImageBitmap(resizeBitmap);
                } else {
                    resizeBitmap = Bitmap.createScaledBitmap(bitmap, 1300, 1650, true);
                    imgProfile.setImageBitmap(resizeBitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (imgProfile.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.img_profile_default).getConstantState()) {
            Toast.makeText(this, "กรุณาเลือกรูปโปรไฟล์", Toast.LENGTH_SHORT).show();
        } else {
            if (user != null) {
                mProgressBar.setVisibility(View.VISIBLE);
                StorageReference profileRef = FirebaseStorage.getInstance().getReference("User")
                        .child(user.getUid().toString()).child(String.valueOf(System.currentTimeMillis()));
                byte[] byteImg = BitmaptoByteArray(resizeBitmap);
                if (byteImg != null) {
                    profileRef.putBytes(byteImg)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    urlImageProfile = task.getResult().getDownloadUrl().toString();
                                    saveUserInfo();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SetDisplayNameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(this, "user null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserInfo() {
        String displayName = edtNameDisplay.getText().toString();

        if (displayName.isEmpty()) {
            edtNameDisplay.requestFocus();
        } else {
            final FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && urlImageProfile != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .setPhotoUri(Uri.parse(urlImageProfile))
                        .build();
                user.updateProfile(profile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mProgressBar.setVisibility(View.GONE);
                                    Intent itn = new Intent(SetDisplayNameActivity.this, HomeActivity.class);
                                    itn.putExtra(KEY_DISPLAY, user.getDisplayName());
                                    itn.putExtra(KEY_EMAIL, user.getEmail());
                                    itn.putExtra(KEY_UID, user.getUid());
                                    itn.putExtra(KEY_PHOTOPROFILE, user.getPhotoUrl().toString());
                                    databaseUser(user);
                                    startActivity(itn);
                                    finish();
                                    Toast.makeText(SetDisplayNameActivity.this, "อัพเดทโปรไฟล์เรียบร้อย", Toast.LENGTH_SHORT).show();
                                } else {
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(SetDisplayNameActivity.this, "อัพเดทโปรไฟล์ไม่สำเร็จกรุณาลองอีกครั้ง", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

    }

    private byte[] BitmaptoByteArray(Bitmap bm) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bo);
        byte[] byteImage = bo.toByteArray();
        return byteImage;
    }

    private void findViewBy() {
        imgProfile = findViewById(R.id.image_profile);
        edtNameDisplay = findViewById(R.id.edit_text_namedisplay);
        btnSaveName = findViewById(R.id.button_savename);
        mProgressBar = findViewById(R.id.progressbar_setDisplay);
    }

    private void databaseUser(FirebaseUser user){
        CollectionReference db = FirebaseFirestore.getInstance().collection("User");
        DocumentReference mDoc = db.document(user.getUid());
        Map<String,String> map = new HashMap<>();
        map.put("DisplayName", user.getDisplayName());
        map.put("Email", user.getEmail());
        map.put("URLImage", String.valueOf(user.getPhotoUrl()));
        mDoc.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
