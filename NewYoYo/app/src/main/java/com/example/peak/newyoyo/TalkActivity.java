package com.example.peak.newyoyo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peak.newyoyo.adapterRecyclerView.AdapterMessageTalk;
import com.example.peak.newyoyo.empty.MessageTalk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class TalkActivity extends AppCompatActivity {
    private static final String TAG = "TalkActivity";
    private FirebaseFirestore db;
    private CollectionReference mCollChat, CollChatOther, mCollChats, mCollChatsOther;
    private DocumentReference mDoc, OtherDoc;
    private Query queryNextMessage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String KEY_ID_MESSAGE = "idMessage";

    private ProgressBar mProgressBar;
    private RecyclerView mRecycler;
    private EditText editMessage;
    private ImageView imgSend;
    private LinearLayoutManager mLayoutManager;

    private int totalItem = 0;
    private int currentitemCount = 0;
    private int scrollOutItem = 0;
    private boolean isScrolling = false;

    private String Uid;
    private String nameUserProduct;
    private String urlImage;
    private ArrayList<MessageTalk> arrMessage = new ArrayList<>();
    private AdapterMessageTalk adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        Toolbar toolbar = findViewById(R.id.toolbar_talk);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        findViewby();
        mRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(mLayoutManager);

        Intent itn = getIntent();
        Uid = itn.getStringExtra("UID");
        nameUserProduct = itn.getStringExtra("name");
        urlImage = itn.getStringExtra("urlImage");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewInflater = inflater.inflate(R.layout.custom_actionbar_chat, null);
        actionBar.setCustomView(viewInflater);

        TextView mTitle = findViewById(R.id.custom_bar_name);
        ImageView mTitleImage = findViewById(R.id.custom_bar_img);
        mTitle.setText(nameUserProduct);
        Picasso.with(this).load(urlImage)
                .placeholder(R.drawable.ic_person_default)
                .into(mTitleImage);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        mCollChat = db.collection("Chat").document(mUser.getUid()).collection(Uid);
        CollChatOther = db.collection("Chat").document(Uid).collection(mUser.getUid());
        updateSeen();

        getData();
        isTotalSize();

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentitemCount = mLayoutManager.getItemCount();
                scrollOutItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (scrollOutItem == 0 && dy < 1) {
                    if (isScrolling && currentitemCount < totalItem) {
                        isScrolling = false;
                        LoadMore();
                    }
                }
            }

        });

        mRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currentitemCount = mLayoutManager.getItemCount();
                scrollOutItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (scrollOutItem == 0) {
                    if (isScrolling && currentitemCount < totalItem) {
                        isScrolling = false;
                        LoadMore();
                    }
                }
                return false;
            }
        });


    }

    private void sendMessage() {
        String message = editMessage.getText().toString();
        editMessage.setText("");
        final String id = String.valueOf(System.currentTimeMillis());
        if (message.length() > 0) {
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String strTime = sdf.format(d);
            String from = mUser.getDisplayName();
            String mUrlProfile = mUser.getPhotoUrl().toString();
            final MessageTalk msSend = new MessageTalk(message, from, strTime, mUrlProfile, id, false);
            DocumentReference docsend = mCollChat.document(id);
            docsend.set(msSend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            createAllChat();

                            DocumentReference docOtherSend = CollChatOther.document(id);
                            docOtherSend.set(msSend)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "send success");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG, e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    });
        }
    }

    private void createAllChat() {
        mCollChats = db.collection("Chat").document(mUser.getUid()).collection("AllChat");
        Map map = new HashMap();
        map.put("urlProfile", urlImage);
        map.put("nameUser", nameUserProduct);
        mDoc = mCollChats.document(Uid);
        mDoc.set(map)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mCollChatsOther = db.collection("Chat").document(Uid).collection("AllChat");
                        Map map = new HashMap();
                        map.put("urlProfile", mUser.getPhotoUrl().toString());
                        map.put("nameUser", mUser.getDisplayName());
                        OtherDoc = mCollChatsOther.document(mUser.getUid());
                        OtherDoc.set(map)
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(TAG, e.getMessage());
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CollChatOther.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, e.getMessage());
                }

                if(adapter != null){
                    for(DocumentChange doc : querySnapshot.getDocumentChanges()){
                        switch (doc.getType()){
                            case ADDED:
                                MessageTalk messageTalk = doc.getDocument().toObject(MessageTalk.class);
                                arrMessage.add(messageTalk);
                                adapter.notifyDataSetChanged();
                                mRecycler.scrollToPosition(arrMessage.size()-1);
                                updateSeen();
                                break;
                            case MODIFIED:
                                MessageTalk mesModified = doc.getDocument().toObject(MessageTalk.class);
                                for (int i = 0 ; i < arrMessage.size() ; i++){
                                    String mesId = arrMessage.get(i).getIdMessage();
                                    if(mesId.equals(mesModified.getIdMessage())){
                                        arrMessage.set(i ,mesModified);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                break;
                            case REMOVED:


                            default :
                                break;
                        }
                    }
                }
            }
        });
    }

    private void updateSeen() {

        mCollChat.whereEqualTo("seen", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String from = String.valueOf(documentSnapshot.get("from"));
                                if(!from.equals(mUser.getDisplayName())) {
                                    DocumentReference document = mCollChat.document(documentSnapshot.getId());
                                    document.update("seen", true);
                                }
                            }
                        }
                    }
                });
    }

    private void getData() {
        CollChatOther.orderBy(KEY_ID_MESSAGE, Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (int i = queryDocumentSnapshots.size() - 1; i >= 0; i--) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                                MessageTalk message = documentSnapshot.toObject(MessageTalk.class);
                                arrMessage.add(message);
                            }
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                            queryNextMessage = mCollChat.orderBy(KEY_ID_MESSAGE, Query.Direction.DESCENDING)
                                    .limit(10)
                                    .startAfter(lastDocument);
                            adapter = new AdapterMessageTalk(TalkActivity.this, arrMessage);
                            mRecycler.setAdapter(adapter);
                            mRecycler.scrollToPosition(arrMessage.size() - 1);
                        } else {
                            adapter = new AdapterMessageTalk(TalkActivity.this, arrMessage);
                            mRecycler.setAdapter(adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                });
    }

    private void LoadMore() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (queryNextMessage != null && arrMessage.size() > 0) {
            queryNextMessage.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.size() > 0) {
                                for (int i = 0; i <= queryDocumentSnapshots.size() - 1; i++) {
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                                    MessageTalk message = documentSnapshot.toObject(MessageTalk.class);
                                    arrMessage.add(0, message);
                                }
                                DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);
                                queryNextMessage = mCollChat.orderBy(KEY_ID_MESSAGE, Query.Direction.DESCENDING)
                                        .limit(10)
                                        .startAfter(lastDocument);
                                mProgressBar.setVisibility(View.GONE);
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();
                                mRecycler.smoothScrollToPosition(0);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, e.getMessage());
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void isTotalSize() {
        mCollChat.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots != null) {
                            totalItem = queryDocumentSnapshots.getDocuments().size();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViewby() {
        editMessage = findViewById(R.id.edit_talk_send);
        imgSend = findViewById(R.id.img_talk_send);
        mRecycler = findViewById(R.id.rcycler_talk);
        mProgressBar = findViewById(R.id.progressbar_loadmore_talk);
    }
}
