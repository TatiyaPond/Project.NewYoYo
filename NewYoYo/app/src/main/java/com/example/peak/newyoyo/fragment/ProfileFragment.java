package com.example.peak.newyoyo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.peak.newyoyo.AdapterPagerProfile;
import com.example.peak.newyoyo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import static com.example.peak.newyoyo.HomeActivity.bitmapCacheProfile;
import static com.example.peak.newyoyo.LoginActivity.mUser;

/**
 * Created by peak on 5/16/2018.
 */


@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final String KEY_EXTRABUNDLE = "extra_bundle";
    private ImageView img_profile;
    private View viewProfile;
    public static ProfileFragment fragmentProfile;
    private AdapterPagerProfile adapterPager;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();
        if (fragmentProfile == null) {
            fragmentProfile = new ProfileFragment();
        }
        fragmentProfile.setArguments(args);
        return fragmentProfile;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewProfile == null) {
            viewProfile = inflater.inflate(R.layout.fragment_profile, container, false);
            img_profile = viewProfile.findViewById(R.id.Image_profile);
            viewPager = viewProfile.findViewById(R.id.viewpager_profile);
            tabLayout = viewProfile.findViewById(R.id.tabLayout);
            setFragment();
            setUI();
        }
        return viewProfile;
    }

    private void setFragment() {
        adapterPager = new AdapterPagerProfile(getFragmentManager());
        adapterPager.AddFragment(ProfileInfoFragment.newInstance(), "ข้อมูล");
        adapterPager.AddFragment(ProfileSaleFragment.newInstance(), "สินค้าลงขาย");
        adapterPager.AddFragment(ProfileSoldOutFragment.newInstance(), "สินค้าหมด");
        viewPager.setAdapter(adapterPager);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUI() {
        final String urlPhoto = mUser.getPhotoUrl().toString();
        if (bitmapCacheProfile == null) {
//            new DownloadImage().execute(urlPhoto);
            Picasso.with(getContext()).load(urlPhoto)
                    .placeholder(R.drawable.ic_person_default)
                    .into(img_profile);
        } else {
            img_profile.setImageBitmap(bitmapCacheProfile);
        }
//        text_profile.setText("ชื่อ " + name + "\n\n" +
//                "Email " + email);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
