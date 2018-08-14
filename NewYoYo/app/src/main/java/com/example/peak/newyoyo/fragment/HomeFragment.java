package com.example.peak.newyoyo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peak.newyoyo.AddProductActivity;
import com.example.peak.newyoyo.CheckConnected;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.adapterRecyclerView.VertAdapterHome;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.example.peak.newyoyo.empty.VertItemHome;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static com.example.peak.newyoyo.HomeActivity.bottomNav;


/**
 * Created by peak on 5/16/2018.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    public static String KEY_Argument = "FragmentName";
    private View viewHom;
    public static HomeFragment fragmentHome;

    private ArrayList<VertItemHome> itemdata;
    private RecyclerView recyclr1;
    private SwipeRefreshLayout swipBar;
    private VertAdapterHome vertAdapterHome;
    private ProgressBar mProgressBar;
    private FloatingActionButton btnFabAdd;
    private String[] arrtype;

    private CollectionReference mCollection = FirebaseFirestore.getInstance().collection("Product");
    private DocumentReference mDoc = mCollection.document();

    private ArrayList<ProductEmpty> itemPant;
    private ArrayList<ProductEmpty> itemShirts;
    private ArrayList<ProductEmpty> itemSport;
    private ArrayList<ProductEmpty> itemCompu;
    private ArrayList<ProductEmpty> itemCar;
    private ArrayList<ProductEmpty> itemMoto;
    private ArrayList<ProductEmpty> itemSmartAndtap;
    private ArrayList<ProductEmpty> itemElect;
    private ArrayList<ProductEmpty> itemAccess;
    private ArrayList<ProductEmpty> itemShoes;

    public HomeFragment() {
    }


    public static HomeFragment newInstance(String tag) {
        Bundle args = new Bundle();
        args.putString(KEY_Argument, tag);
        if (fragmentHome == null) {
            fragmentHome = new HomeFragment();
        }
        fragmentHome.setArguments(args);
        return fragmentHome;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        if (viewHom == null) {
            viewHom = inflater.inflate(R.layout.fragment_home, container, false);
            itemdata = new ArrayList<>();
            arrtype = getResources().getStringArray(R.array.type_product);
            settingView(viewHom);
            setupArrayItem();
            SetView();
            btnFabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itn = new Intent(viewHom.getContext(), AddProductActivity.class);
                    startActivity(itn);
                }
            });
            swipBar.setColorSchemeResources(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3
            );
            swipBar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    CheckConnected checkConnected = new CheckConnected();
                    if (checkConnected.mConnected(viewHom.getContext())) {
                        clearItemForRefresh();
                        recyclr1.getRecycledViewPool().clear();
                        vertAdapterHome.notifyDataSetChanged();
                        SetView();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (swipBar.isRefreshing()) {
                                swipBar.setRefreshing(false);
                                recyclr1.setVisibility(View.VISIBLE);
                                Toast.makeText(viewHom.getContext(), "เกิดข้อผิดพลาดขณะรีเฟรช", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 15000);
                }
            });
        }
        return viewHom;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "OnViewCreated");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        mCollection.addSnapshotListener((Activity) viewHom.getContext(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    private void SetView() {
        if (!swipBar.isRefreshing()) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        recyclr1.setVisibility(View.GONE);
        getData(arrtype[0]);
        getData(arrtype[1]);
        getData(arrtype[2]);
        getData(arrtype[3]);
        getData(arrtype[4]);
        getData(arrtype[5]);
        getData(arrtype[6]);
        getData(arrtype[7]);
        getData(arrtype[8]);
        getData(arrtype[9]);

    }


    private void getData(String typePro) {
        mCollection.whereEqualTo("type_product", typePro)
                .orderBy("id_product", Query.Direction.DESCENDING)
                .limit(7)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String type = null;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ProductEmpty empty = documentSnapshot.toObject(ProductEmpty.class);
                            type = empty.getType_product();
                            if (type.equals("กางเกง")) {
                                itemPant.add(empty);
                            }
                            if (type.equals("เสื้อ")) {
                                itemShirts.add(empty);
                            }
                            if (type.equals("กีฬา")) {
                                itemSport.add(empty);
                            }
                            if (type.equals("คอมพิวเตอร์")) {
                                itemCompu.add(empty);
                            }
                            if (type.equals("รถยนต์")) {
                                itemCar.add(empty);
                            }
                            if (type.equals("มอเตอร์ไซค์")) {
                                itemMoto.add(empty);
                            }
                            if (type.equals("สมาร์ทโฟนและแท็บเลต")) {
                                itemSmartAndtap.add(empty);
                            }
                            if (type.equals("เครื่องใช้ไฟฟ้า")) {
                                itemElect.add(empty);
                            }
                            if (type.equals("เครื่องประดับ")) {
                                itemAccess.add(empty);
                            }
                            if (type.equals("รองเท้า")) {
                                itemShoes.add(empty);
                            }
                        }
                        if(type != null) {
                            if (type.equals(arrtype[9])) {
                                itemdata.add(new VertItemHome(arrtype[0], itemShirts));
                                itemdata.add(new VertItemHome(arrtype[1], itemPant));
                                itemdata.add(new VertItemHome(arrtype[2], itemShoes));
                                itemdata.add(new VertItemHome(arrtype[3], itemAccess));
                                itemdata.add(new VertItemHome(arrtype[4], itemSport));
                                itemdata.add(new VertItemHome(arrtype[5], itemElect));
                                itemdata.add(new VertItemHome(arrtype[6], itemCompu));
                                itemdata.add(new VertItemHome(arrtype[7], itemSmartAndtap));
                                itemdata.add(new VertItemHome(arrtype[8], itemCar));
                                itemdata.add(new VertItemHome(arrtype[9], itemMoto));
                                vertAdapterHome = new VertAdapterHome(viewHom.getContext(), itemdata);
                                recyclr1.setAdapter(vertAdapterHome);
                                mProgressBar.setVisibility(View.GONE);
                                recyclr1.setVisibility(View.VISIBLE);
                                if (swipBar.isRefreshing()) {
                                    swipBar.setRefreshing(false);
                                }
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void settingView(View view) {
        mProgressBar = view.findViewById(R.id.progressbar_home);
        btnFabAdd = view.findViewById(R.id.btn_fab_add);
        swipBar = view.findViewById(R.id.swip_bar);
        recyclr1 = view.findViewById(R.id.rcycler1);
        recyclr1.setHasFixedSize(true);
        recyclr1.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    private void setupArrayItem() {
        itemPant = new ArrayList<>();
        itemShoes = new ArrayList<>();
        itemAccess = new ArrayList<>();
        itemElect = new ArrayList<>();
        itemSmartAndtap = new ArrayList<>();
        itemMoto = new ArrayList<>();
        itemCompu = new ArrayList<>();
        itemSport = new ArrayList<>();
        itemCar = new ArrayList<>();
        itemShirts = new ArrayList<>();
    }

    private void clearItemForRefresh(){
        itemPant.clear();
        itemShoes.clear();
        itemAccess.clear();
        itemElect.clear();
        itemSmartAndtap.clear();
        itemMoto.clear();
        itemCompu.clear();
        itemSport.clear();
        itemCar.clear();
        itemShirts.clear();
        itemdata.clear();
    }
}
