package com.example.peak.newyoyo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.peak.newyoyo.fragment.HomeFragment.KEY_Argument;
import static com.example.peak.newyoyo.LoginActivity.mUser;

/**
 * Created by peak on 5/16/2018.
 */

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private RecyclerView Rcv;
    private ArrayList<Map> arrListChat = new ArrayList<>();
    private AdapterRecyclerChat adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mColl = db.collection("Chat").document(mUser.getUid()).collection("AllChat");
    private DocumentReference mDocument;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static ChatFragment newInstance(String tag) {

        Bundle args = new Bundle();
        args.putString(KEY_Argument, tag);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        Rcv = rootView.findViewById(R.id.rcycler_chat);
        Rcv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        Rcv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Rcv.getContext(), linearLayoutManager.getOrientation());
        Rcv.addItemDecoration(dividerItemDecoration);
        Rcv.getRecycledViewPool().clear();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> snapshot = queryDocumentSnapshots.getDocuments();
                    arrListChat.clear();
                    for (int i = 0; i < snapshot.size(); i++) {
                        DocumentSnapshot documentSnapshot = snapshot.get(i);
                        Map map = new HashMap();
                        map.put("uid", documentSnapshot.getId());
                        map.put("nameUser", documentSnapshot.get("nameUser"));
                        map.put("urlProfile", documentSnapshot.get("urlProfile"));
                        arrListChat.add(map);
                    }
                    if (adapter != null && arrListChat.size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else if (adapter != null && arrListChat.size() == 0) {
                        adapter.notifyDataSetChanged();
                    } else if (adapter == null && arrListChat.size() > 0) {
                        adapter = new AdapterRecyclerChat(getContext(), arrListChat);
                        Rcv.setAdapter(adapter);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
