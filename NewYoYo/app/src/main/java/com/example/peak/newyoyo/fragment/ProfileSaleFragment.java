package com.example.peak.newyoyo.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerSaleAndSold;
import com.example.peak.newyoyo.adapterRecyclerView.ItemClickListener;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.example.peak.newyoyo.LoginActivity.mUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSaleFragment extends Fragment {
    private static final String TAG = "ProfileSaleFragment";
    private CollectionReference mColl = FirebaseFirestore.getInstance().collection("Product");
    private List<ProductEmpty> listProduct = new ArrayList<>();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ListenerRegistration listenerRegistration;

    private RecyclerView mRecyclerView;
    private AdapterRecyclerSaleAndSold adapter;
    private static View view;

    public ProfileSaleFragment() {
        // Required empty public constructor
    }

    public static ProfileSaleFragment newInstance() {

        Bundle args = new Bundle();

        ProfileSaleFragment fragment = new ProfileSaleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_sale, container, false);
        mRecyclerView = view.findViewById(R.id.rcycler_sale);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        getProduct();
        return view;
    }

    private void getProduct() {
        mColl.whereEqualTo("uid", mUser.getUid())
                .whereEqualTo("status", "มีสินค้า")
                .orderBy("id_product", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (DocumentSnapshot doc : querySnapshot) {
                            ProductEmpty empty = doc.toObject(ProductEmpty.class);
                            if (empty != null) {
                                if (!empty.getQrt_product().equals("")) {
                                    int amount = Integer.parseInt(empty.getQrt_product());
                                    if (amount > 0) {
                                        listProduct.add(empty);
                                    }
                                }
                            }
                        }
                        adapter = new AdapterRecyclerSaleAndSold(listProduct);
                        mRecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        listenerRegistration = mColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (querySnapshot != null) {
                    if (adapter != null) {
                        for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                            switch (documentChange.getType()) {
                                case ADDED:
//                                    DocumentSnapshot documentSnapshot = documentChange.getDocument();
//                                    String id = documentSnapshot.getId();
//                                    String lastId = querySnapshot.getDocuments().get(querySnapshot.size() - 1).getId();
//                                    if (id.equals(lastId)) {
//                                        ProductEmpty emptyADD = documentChange.getDocument().toObject(ProductEmpty.class);
//                                        if (listProduct.size() > 0) {
//                                            for (int i = 0; i < listProduct.size(); i++) {
//                                                if (emptyADD.getUid().equals(mUser.getUid()) && emptyADD.getStatus().equals("มีสินค้า")) {
//
//                                                } else {
//                                                    if (i == listProduct.size() - 1) {
//                                                        listProduct.add(emptyADD);
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            if (emptyADD.getUid().equals(mUser.getUid()) && emptyADD.getStatus().equals("มีสินค้า")) {
//                                                listProduct.add(emptyADD);
//                                            }
//                                        }
//                                    }
                                    DocumentSnapshot snapshot = documentChange.getDocument();
                                    ProductEmpty emptyADD = snapshot.toObject(ProductEmpty.class);
                                    if(emptyADD.getUid().equals(mUser.getUid())&& emptyADD.getStatus().equals("มีสินค้า")) {
                                        listProduct.add(emptyADD);
                                    }
                                    adapter.notifyDataSetChanged();
                                    break;

                                case MODIFIED:
                                    DocumentSnapshot snapshotMOD = documentChange.getDocument();
                                    String ItemIdMOD = snapshotMOD.get("id_product").toString();
                                    if(listProduct.size() > 0) {
                                        for (int i = 0; i < listProduct.size(); i++) {

                                            if (listProduct.get(i).getId_product().equals(ItemIdMOD)) {
                                                ProductEmpty empty = snapshotMOD.toObject(ProductEmpty.class);

                                                if (empty.getStatus().equals("มีสินค้า")) {
                                                    listProduct.set(i, empty);
                                                } else {
                                                    listProduct.remove(i);
                                                }
                                            } else {
                                                if (i == listProduct.size() - 1) {
                                                    ProductEmpty empty = snapshotMOD.toObject(ProductEmpty.class);
                                                    if (empty.getStatus().equals("มีสินค้า") && empty.getUid().equals(mUser.getUid())) {
                                                        listProduct.add(empty);
                                                    }
                                                }
                                            }
                                        }
                                    }else{
                                        ProductEmpty empty = snapshotMOD.toObject(ProductEmpty.class);
                                        if (empty.getStatus().equals("มีสินค้า") && empty.getUid().equals(mUser.getUid())) {
                                            listProduct.add(empty);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    break;

                                case REMOVED:
                                    if (adapter != null) {
                                        DocumentSnapshot snapshotREM = documentChange.getDocument();
                                        String ItemIdREM = snapshotREM.get("id_product").toString();
                                        for (int i = 0; i < listProduct.size(); i++) {
                                            if (listProduct.get(i).getId_product().equals(ItemIdREM)) {
                                                listProduct.remove(i);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("ProfileSaleFragment", "onstop");
        view = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("ProfileSaleFragment","onpause");
        listenerRegistration.remove();
        listProduct.clear();
    }
}
