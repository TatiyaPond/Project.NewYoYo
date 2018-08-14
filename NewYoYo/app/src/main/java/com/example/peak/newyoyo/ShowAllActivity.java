package com.example.peak.newyoyo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerShowAll;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class ShowAllActivity extends AppCompatActivity {
    private static final String TAG = "ShowAllActivity";

    private String title = null;
    private ArrayList<ProductEmpty> arrData;
    private RecyclerView recyclerView;
    private ProgressBar mProgress, mProgressLoadmore;
    private LinearLayoutManager mLayoutManager;
    private AdapterRecyclerShowAll adapter;

    private boolean isLoading = true;
    private int ITEMS_PER = 10;
    private int totalItems = 0;
    private int CreatedItems = 0;
    private int visibleItemCount = 0;
    private int pastVisibleItems = 0;
    private int previousTotal = 0;
    private Query queFirst, queNext;

    private CollectionReference mCollecttion;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        title = getIntent().getStringExtra("title");
        Toolbar toolbar = findViewById(R.id.toolbar_showall);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mProgressLoadmore = findViewById(R.id.progressbar_loadmore);
        mProgress = findViewById(R.id.progressbar_show_all);
        mProgress.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.rcycler_show_all);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        arrData = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        mCollecttion = db.collection("Product");
        queFirst = mCollecttion.whereEqualTo("type_product", title)
                .orderBy("id_product", Query.Direction.DESCENDING)
                .limit(ITEMS_PER);
        queFirst.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (ProductEmpty empty : queryDocumentSnapshots.toObjects(ProductEmpty.class)) {
                            arrData.add(empty);
                        }
                        adapter = new AdapterRecyclerShowAll(ShowAllActivity.this, arrData);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        mProgress.setVisibility(GONE);

                        DocumentSnapshot lastvisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size()-1);
                        queNext = mCollecttion.whereEqualTo("type_product", title)
                                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                                .startAfter(lastvisible)
                                .limit(ITEMS_PER);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, e.toString());
                    }
                });

        mCollecttion.whereEqualTo("type_product", title)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        totalItems = queryDocumentSnapshots.getDocuments().size();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mLayoutManager.getChildCount();
                CreatedItems = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (isLoading) {
                        if (CreatedItems > previousTotal) {
                            isLoading = false;
                            previousTotal = CreatedItems;
                        }
                    }

                    if (!isLoading && (CreatedItems - visibleItemCount) <= (pastVisibleItems + ITEMS_PER)
                            && CreatedItems < totalItems) {
                        loadNextItem();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadNextItem() {
        mProgressLoadmore.setVisibility(View.VISIBLE);
        isLoading = false;
        queNext.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()!=0) {
                            for (ProductEmpty empty : queryDocumentSnapshots.toObjects(ProductEmpty.class)) {
                                arrData.add(empty);
                            }
                            adapter.notifyItemRangeInserted(arrData.size() - 1, ITEMS_PER);

                            DocumentSnapshot lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                            queNext = mCollecttion.whereEqualTo("type_product", title)
                                    .orderBy("id_product", Query.Direction.DESCENDING)
                                    .startAfter(lastVisible)
                                    .limit(ITEMS_PER);
                        }
                        mProgressLoadmore.setVisibility(GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,e.getMessage());
                    }
                });
    }

//    private void updateDoc() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        final String url_image = String.valueOf(user.getPhotoUrl());
//        mCollecttion.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        int i = 0;
//                        for (ProductEmpty empty : queryDocumentSnapshots.toObjects(ProductEmpty.class)) {
//                            String id = queryDocumentSnapshots.getDocuments().get(i).getId();
//                            String Uid = null;
//                            String nameuser = empty.getName_user();
//                            if(nameuser.equals("pond")){
//                                Uid = "gkVjMfXE22V4AsGeNykykTHVGsg1";
//                            }else if(nameuser.equals("aith")){
//                                Uid = "eTljFA0tWMYFntRm6IxxlYdt5PH3";
//                            }else if(nameuser.equals("Pond Rattanapornprasit")){
//                                Uid = "pe6ZzFsq9Ggo4jNNi05R9qI9Geu2";
//                            }
//                            empty.setUid(Uid);
//                            arrData.add(empty);
//                            update(id, empty);
//                            i++;
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i(TAG, e.toString());
//                    }
//                });
//    }



    private void update(String id, ProductEmpty empty) {
        mCollecttion.document(id)
                .set(empty, SetOptions.merge());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
