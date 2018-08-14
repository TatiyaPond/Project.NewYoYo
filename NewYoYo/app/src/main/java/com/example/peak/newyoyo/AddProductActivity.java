package com.example.peak.newyoyo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerView;
import com.example.peak.newyoyo.empty.ProductEmpty;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AddProductActivity extends AppCompatActivity {
    public static final String TAG = "AddProductActivity";
    public static final String KEY_PRODUCT_USER = "name_user";
    public static final String KEY_PRODUCT_NAME = "name_product";
    public static final String KEY_PRODUCT_TYPE = "type_product";
    public static final String KEY_PRODUCT_PRICE = "price_product";
    public static final String KEY_PRODUCT_QRT = "qrt_product";
    public static final String KEY_PRODUCT_PROVINCE = "province_product";
    public static final String KEY_PRODUCT_PHONE = "phone_product";
    public static final String KEY_PRODUCT_IMG0 = "image0_product";
    public static final String KEY_PRODUCT_IMG1 = "image1_product";
    public static final String KEY_PRODUCT_IMG2 = "image2_product";
    public static final String KEY_PRODUCT_IMG3 = "image3_product";
    public static final String KEY_PRODUCT_IMG4 = "image4_product";
    public static final String KEY_PRODUCT_IMG5 = "image5_product";
    public static final String KEY_PRODUCT_DESCRIPTION = "description_product";
    public static final String KEY_PRODUCT_ID = "id_product";
    public static final String KEY_PRODUCT_UID = "uid";
    public static final String KEY_PRODUCT_URL_PROFILE = "url_imgprofile";
    public static final String KEY_PRODUCT_STATUS = "status";

    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AdapterRecyclerView mAdapter;
    private ArrayList<Bitmap> mArrBitmap;
    private ImageView imgAddPicture;
    private EditText edtNamePro, edtType, edtprice, edtQrt, edtProvince, edtDescrip, edtPhone;
    private Button btnAdd;

    private int requestPicture = 100;
    private Bitmap resizeBitmapAdd;
    private AlertDialog dialog;

    private ArrayList<String> urlImage = new ArrayList<>();

    private CollectionReference db;
    private DocumentReference mDoc;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String id;

    private String ApplicationID = "AGVBD5SAD1";
    private String APIkey = "9c846f80161a65d44ef5691e991a9ec5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = findViewById(R.id.toolbar_add_product);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewBy();
        id = String.valueOf(System.currentTimeMillis());
        mArrBitmap = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        imgAddPicture.setOnClickListener(imageClick);
        edtType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] arrtype = getResources().getStringArray(R.array.type_product);
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this)
                        .setTitle("เลือกประเภทสินค้า")
                        .setItems(arrtype,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        edtType.setText(arrtype[which]);
                                    }
                                });
                dialog.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance().collection("Product");
        mDoc = db.document(id);
        if (mUser != null) {
            mStorageRef = FirebaseStorage.getInstance().getReference("Product").child(mUser.getUid());
        } else {
            Toast.makeText(this, "user null", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener imageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mArrBitmap.size() >= 0 && mArrBitmap.size() < 6) {
                Intent itn = new Intent();
                itn.setAction(Intent.ACTION_GET_CONTENT);
                itn.setType("image/*");
                startActivityForResult(Intent.createChooser(itn, "เลือก"), requestPicture);
            } else {
                Toast.makeText(AddProductActivity.this, "เลือกรูปภาพได้สูงสุด 6 ภาพ", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void Addproduct(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
        View viewDialog = LayoutInflater.from(AddProductActivity.this).inflate(R.layout.item_progress_dialog, null);
        builder.setView(viewDialog).setCancelable(false);
        dialog = builder.create();
        dialog.show();
        addProduct();
    }

    private void addProduct() {
        String nameProduct = edtNamePro.getText().toString();
        if (mArrBitmap.size() != 0) {
            for (int i = 0; i < mArrBitmap.size(); i++) {
                StorageReference storageReference = mStorageRef.child(nameProduct)
                        .child("Image_" + i + "_" + id + ".jpg");
                byte[] Byteimage = bitmapToByteArray(i);
                if (Byteimage != null) {
                    storageReference.putBytes(Byteimage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    urlImage.add(taskSnapshot.getDownloadUrl().toString());
                                    if (urlImage.size() == mArrBitmap.size()) {
                                        addProductToFireStore();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        } else {
            Toast.makeText(this, "Array null", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProductToFireStore() {
        if (urlImage != null) {
            String urlImgProfile = String.valueOf(mUser.getPhotoUrl());
            String Uid = mUser.getUid();
            String status = "มีสินค้า";
            final ProductEmpty empty = new ProductEmpty(mUser.getDisplayName(),
                    edtNamePro.getText().toString(), edtType.getText().toString(),
                    edtprice.getText().toString(), edtQrt.getText().toString(),
                    edtProvince.getText().toString(), edtDescrip.getText().toString(), edtPhone.getText().toString(), urlImgProfile, Uid, status);

            for (int i = 0; i < urlImage.size(); i++) {
                if (i == 0) {
                    empty.setImage0_product(urlImage.get(0));
                } else if (i == 1) {
                    empty.setImage1_product(urlImage.get(1));
                } else if (i == 2) {
                    empty.setImage2_product(urlImage.get(2));
                } else if (i == 3) {
                    empty.setImage3_product(urlImage.get(3));
                } else if (i == 4) {
                    empty.setImage4_product(urlImage.get(4));
                } else if (i == 5) {
                    empty.setImage5_product(urlImage.get(5));
                }
            }
            empty.setId_product(id);
            mDoc.set(empty)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            addToAlgolia(empty);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "null array", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestPicture && resultCode == RESULT_OK) {
            if (data != null) {

                Uri uri = data.getData();
                int resizeHeight;
                int resizeWidth;
                InputStream is;
                try {
                    is = getContentResolver().openInputStream(uri);
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    resizeHeight = bm.getHeight();
                    resizeWidth = bm.getWidth();
                    if (resizeHeight > 4000 || resizeWidth > 4000) {
                        resizeBitmapAdd = Bitmap.createScaledBitmap(bm, (int) (resizeWidth * 0.4), (int) (resizeHeight * 0.4), true);
                    } else if (resizeHeight > 3000 || resizeWidth > 3000) {
                        resizeBitmapAdd = Bitmap.createScaledBitmap(bm, (int) (resizeWidth * 0.5), (int) (resizeHeight * 0.5), true);
                    } else if (resizeHeight > 2000 || resizeWidth > 2000) {
                        resizeBitmapAdd = Bitmap.createScaledBitmap(bm, (int) (resizeWidth * 0.6), (int) (resizeHeight * 0.6), true);
                    } else {
                        resizeBitmapAdd = bm;
                    }
//                    resizeBitmapAdd = resizeBitmap(uri);
                    mArrBitmap.add(resizeBitmapAdd);
                    mAdapter = new AdapterRecyclerView(mArrBitmap, this);
                    recyclerView.setAdapter(mAdapter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "data null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private byte[] bitmapToByteArray(int position) {
        Bitmap bitmap = mArrBitmap.get(position);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    private void findViewBy() {
        recyclerView = findViewById(R.id.recycler_img_addproduct);
        imgAddPicture = findViewById(R.id.img_add_picture);
        edtNamePro = findViewById(R.id.edit_name_product);
        edtType = findViewById(R.id.edit_type_product);
        edtprice = findViewById(R.id.edit_price_product);
        edtProvince = findViewById(R.id.edit_province_product);
        edtDescrip = findViewById(R.id.edit_descrip_product);
        edtQrt = findViewById(R.id.edit_qrt_product);
        btnAdd = findViewById(R.id.button_add_product);
        edtPhone = findViewById(R.id.edit_phone_product);
    }

    private void addToAlgolia(ProductEmpty empty) {
        Client client = new Client(ApplicationID, APIkey);
        Index index = client.initIndex("Product");

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_PRODUCT_NAME, empty.getName_product());
        map.put(KEY_PRODUCT_USER, empty.getName_user());
        map.put(KEY_PRODUCT_DESCRIPTION, empty.getDescription_product());
        map.put(KEY_PRODUCT_ID, empty.getId_product());
        map.put(KEY_PRODUCT_PRICE, empty.getPrice_product());
        map.put(KEY_PRODUCT_PROVINCE, empty.getProvince_product());
        map.put(KEY_PRODUCT_PHONE, empty.getPhone_product());
        map.put(KEY_PRODUCT_QRT, empty.getQrt_product());
        map.put(KEY_PRODUCT_TYPE, empty.getType_product());
        map.put(KEY_PRODUCT_UID, empty.getUid());
        map.put(KEY_PRODUCT_URL_PROFILE, empty.getUrl_imgprofile());
        map.put(KEY_PRODUCT_IMG0, empty.getImage0_product());
        map.put(KEY_PRODUCT_IMG1, empty.getImage1_product());
        map.put(KEY_PRODUCT_IMG2, empty.getImage2_product());
        map.put(KEY_PRODUCT_IMG3, empty.getImage3_product());
        map.put(KEY_PRODUCT_IMG4, empty.getImage4_product());
        map.put(KEY_PRODUCT_IMG5, empty.getImage5_product());
        map.put(KEY_PRODUCT_STATUS, empty.getStatus());
        index.addObjectAsync(new JSONObject(map), null);

        dialog.dismiss();
        finish();
        Toast.makeText(AddProductActivity.this, "ลงขายสินค้าสำเร็จ", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        db.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                            ProductEmpty empty = doc.toObject(ProductEmpty.class);
//                            String id = doc.getId();
//                            empty.setPhone_product("0876667491");
//                            update(id, empty);
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

    }

//    private void update(String id, ProductEmpty empty) {
//        db.document(id)
//                .set(empty, SetOptions.merge());
//        Client client = new Client(ApplicationID, APIkey);
//        Index index = client.initIndex("Product");
//        Map<String, Object> map = new HashMap<>();
//        map.put(KEY_PRODUCT_NAME, empty.getName_product());
//        map.put(KEY_PRODUCT_USER, empty.getName_user());
//        map.put(KEY_PRODUCT_DESCRIPTION, empty.getDescription_product());
//        map.put(KEY_PRODUCT_ID, empty.getId_product());
//        map.put(KEY_PRODUCT_PRICE, empty.getPrice_product());
//        map.put(KEY_PRODUCT_PROVINCE, empty.getProvince_product());
//        map.put(KEY_PRODUCT_QRT, empty.getQrt_product());
//        map.put(KEY_PRODUCT_TYPE, empty.getType_product());
//        map.put(KEY_PRODUCT_UID, empty.getUid());
//        map.put(KEY_PRODUCT_PHONE, empty.getPhone_product());
//        map.put(KEY_PRODUCT_URL_PROFILE, empty.getUrl_imgprofile());
//        map.put(KEY_PRODUCT_IMG0, empty.getImage0_product());
//        map.put(KEY_PRODUCT_IMG1, empty.getImage1_product());
//        map.put(KEY_PRODUCT_IMG2, empty.getImage2_product());
//        map.put(KEY_PRODUCT_IMG3, empty.getImage3_product());
//        map.put(KEY_PRODUCT_IMG4, empty.getImage4_product());
//        map.put(KEY_PRODUCT_IMG5, empty.getImage5_product());
//        map.put(KEY_PRODUCT_STATUS, empty.getStatus());
//        index.addObjectAsync(new JSONObject(map), null);
//    }
}
