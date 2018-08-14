package com.example.peak.newyoyo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peak.newyoyo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.example.peak.newyoyo.HomeActivity.bitmapCacheProfile;
import static com.example.peak.newyoyo.LoginActivity.mUser;
import static com.example.peak.newyoyo.fragment.HomeFragment.fragmentHome;
import static com.example.peak.newyoyo.fragment.ProfileFragment.fragmentProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {

    private Button btnSignOut;
    private TextView text_profile;
    private TextView text_Verification;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private View rootViewProfileInfo;

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    public static ProfileInfoFragment newInstance() {

        Bundle args = new Bundle();

        ProfileInfoFragment fragment = new ProfileInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootViewProfileInfo = inflater.inflate(R.layout.fragment_profile_info, container, false);
        text_profile = rootViewProfileInfo.findViewById(R.id.text_profile);
        text_Verification = rootViewProfileInfo.findViewById(R.id.text_verification);
        btnSignOut = rootViewProfileInfo.findViewById(R.id.button_logout);
        emailVerification(mUser);
        text_profile.setText("ชื่อ : " + mUser.getDisplayName() + "\n\n" +
                "Email : " + mUser.getEmail());
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutfromprofile();
                mAuth.signOut();
                bitmapCacheProfile = null;
                mUser = null;
                fragmentHome = null;
                fragmentProfile = null;
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return rootViewProfileInfo;
    }

    private void signoutfromprofile() {
        CollectionReference colRef = FirebaseFirestore.getInstance().collection("User");
        DocumentReference DocRef = colRef.document(mUser.getUid());
        DocRef.update("device_token", "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    private void emailVerification(final FirebaseUser user) {
        if (user != null) {
            if (user.isEmailVerified()) {
                text_Verification.setText("ยืนยันอีแมลแล้ว");
                text_Verification.setTextColor(getResources().getColor(R.color.colorTextVerified));
            } else if (!user.isEmailVerified()) {
                text_Verification.setText("ยังไม่ยืนยันอีเมลกรุณายืนยันอีเมล(คลิกที่นี่)");
                text_Verification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getView().getContext(), "ส่งข้อความไปยังอีเมลของท่านแล้ว", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getView().getContext(), "การส่งข้อความล้มเหลว", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else {
                Toast.makeText(getView().getContext(), "User null2", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getView().getContext(), "User null1", Toast.LENGTH_SHORT).show();
        }
    }

}
