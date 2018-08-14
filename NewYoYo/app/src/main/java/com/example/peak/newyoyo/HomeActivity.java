package com.example.peak.newyoyo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.example.peak.newyoyo.empty.MessageTalk;
import com.example.peak.newyoyo.fragment.ChatFragment;
import com.example.peak.newyoyo.fragment.HomeFragment;
import com.example.peak.newyoyo.fragment.ProfileFragment;
import com.example.peak.newyoyo.fragment.SearchFragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static com.example.peak.newyoyo.LoginActivity.KEY_DEVICE_TOKEN;
import static com.example.peak.newyoyo.LoginActivity.KEY_DISPLAY;
import static com.example.peak.newyoyo.LoginActivity.KEY_UID;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    public static Bitmap bitmapCacheProfile = null;

    private int backcount = 0;

    private CollectionReference colRef, collAllChat, collChat;
    private DocumentReference DocRef, DocChat;
    private ListenerRegistration listenerRegistration;

    private String DisplayName;
    private String idDocChatAll;
    public static AHBottomNavigation bottomNav;
    private int notifiCount;
    private Bundle bundle;
    private String Uid;
    private String mDevice;
    private String tagHome = "Home";
    private String tagChat = "Chat";
    private String tagSearch = "Search";
    private String tagProfile = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewBy();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, HomeFragment.newInstance(tagHome), tagHome)
                .commit();

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("หน้าหลัก", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("ข้อความ", R.drawable.ic_message_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("ค้นหา", R.drawable.ic_search_black_24dp);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("โปรไฟล์", R.drawable.ic_person_black_24dp);
        bottomNav.addItem(item1);
        bottomNav.addItem(item2);
        bottomNav.addItem(item3);
        bottomNav.addItem(item4);
        bottomNav.setCurrentItem(0);
        bottomNav.setAccentColor(Color.parseColor("#F44336"));
        bottomNav.setBehaviorTranslationEnabled(true);
        bottomNav.setTranslucentNavigationEnabled(true);
        bottomNav.setOnTabSelectedListener(AHBottomListeber);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            Uid = bundle.getString(KEY_UID);
            mDevice = bundle.getString(KEY_DEVICE_TOKEN);
            DisplayName = bundle.getString(KEY_DISPLAY);
        }

        colRef = FirebaseFirestore.getInstance().collection("User");
        collAllChat = FirebaseFirestore.getInstance().collection("Chat").document(Uid).collection("AllChat");
        DocRef = colRef.document(Uid);
        DocChat = FirebaseFirestore.getInstance().collection("Chat").document(Uid);

        Checkpermisstions();

    }

    private void Checkpermisstions() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestPerms();
        }
    }

    private void requestPerms() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(HomeActivity.this, perms, 0);
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, perms, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{

            }

            if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){

            }else{

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                }

                if (documentSnapshot != null) {
                    String device = FirebaseInstanceId.getInstance().getToken();
                    Map map = documentSnapshot.getData();
                    mDevice = map.get("device_token").toString();
                    if (!device.equals(mDevice) && !mDevice.isEmpty()) {
                        signout();
                        Toast.makeText(HomeActivity.this, "มีการเข้าสู่ระบบจากแหล่งอื่น", Toast.LENGTH_SHORT).show();
                    } else if (mDevice.isEmpty()) {
                        signout();
                    }
                }

            }
        });

        setNotificationFromData();


//        bottomNav.setSelectedItemId(R.id.bottom_navigation_home);
//        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
//                new HomeFragment()).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        notifiCount = 0;
    }

    private void setNotificationFromData() {
        collAllChat.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            idDocChatAll = doc.getId();
                            getFalseChat(doc.getId());
                        }

                    }
                });
    }

    private void getFalseChat(String id) {
        collChat = DocChat.collection(id);
        Query query = collChat;
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                    return;
                }

                if (querySnapshot != null) {
                    for (DocumentChange docChange : querySnapshot.getDocumentChanges()) {
                        switch (docChange.getType()) {
                            case ADDED:
                                DocumentSnapshot docSnapshot = docChange.getDocument();
                                MessageTalk messageTalk = docSnapshot.toObject(MessageTalk.class);
                                if (!messageTalk.getFrom().equals(DisplayName)) {
                                    if (!messageTalk.isSeen()) {
                                        notifiCount += 1;
                                    }
                                }
                                break;
                            case MODIFIED:
                                DocumentSnapshot docSnapshot1 = docChange.getDocument();
                                MessageTalk messageTalk1 = docSnapshot1.toObject(MessageTalk.class);
                                if (!messageTalk1.getFrom().equals(DisplayName)) {
                                    if (messageTalk1.isSeen()) {
                                        notifiCount -= 1;
                                    }
                                }
                                break;
                        }
                    }
                    if (notifiCount <= 0) {
                        bottomNav.setNotification("", 1);
                    } else {
                        bottomNav.setNotification("" + notifiCount, 1);
                    }
                }
            }
        });
    }

    private void findViewBy() {
        bottomNav = findViewById(R.id.btNavHome);
//        pager = findViewById(R.id.viewpage);
    }

    private AHBottomNavigation.OnTabSelectedListener AHBottomListeber =
            new AHBottomNavigation.OnTabSelectedListener() {
                @Override
                public boolean onTabSelected(int position, boolean wasSelected) {
                    switch (position) {
                        case 0:
                            replaceFragment(HomeFragment.newInstance(tagHome), tagHome);
//                            pager.setCurrentItem(0);
                            break;
                        case 1:
                            replaceFragment(ChatFragment.newInstance(tagChat), tagChat);
//                            pager.setCurrentItem(1);
                            break;
                        case 2:
                            replaceFragment(SearchFragment.newInstance(), tagSearch);
//                            pager.setCurrentItem(2);
                            break;
                        case 3:
                            replaceFragment(ProfileFragment.newInstance(), tagProfile);
//                            pager.setCurrentItem(3);
                            break;
                    }
                    return true;
                }
            };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.bottom_navigation_home:
                            replaceFragment(HomeFragment.newInstance(tagHome), tagHome);
//                            pager.setCurrentItem(0);
                            break;
                        case R.id.bottom_navigation_chat:
                            replaceFragment(ChatFragment.newInstance(tagChat), tagChat);
//                            pager.setCurrentItem(1);
                            break;
                        case R.id.bottom_navigation_search:
                            replaceFragment(SearchFragment.newInstance(), tagSearch);
//                            pager.setCurrentItem(2);
                            break;
                        case R.id.bottom_navigation_profile:
                            replaceFragment(ProfileFragment.newInstance(), tagProfile);
//                            pager.setCurrentItem(3);
                            break;
                    }

                    return true;
                }
            };

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (tag) {
            case "Home":
                fragmentManager.findFragmentByTag(tag);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment)
                        .commit();
                break;
            case "Chat":
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment, tag)
                        .commit();
                break;
            case "Search":
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment, tag)
                        .commit();
                break;
            case "Profile":
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment, tag)
                        .commit();
                break;
        }
    }

    private void signout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        bitmapCacheProfile = null;
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backcount > 0) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        } else {
            Toast.makeText(this, "กดอีกครั้งเพื่อออก", Toast.LENGTH_SHORT).show();
            new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    backcount++;
                }
            }.start();
        }

    }
}
