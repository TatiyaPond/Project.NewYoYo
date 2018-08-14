package com.example.peak.newyoyo.adapterRecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.peak.newyoyo.Holder.BaseViewHolder;
import com.example.peak.newyoyo.Holder.SaleHolder;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_DESCRIPTION;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_ID;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG0;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG1;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG2;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG3;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG4;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG5;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_NAME;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_PRICE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_PROVINCE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_QRT;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_STATUS;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_TYPE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_UID;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_URL_PROFILE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_USER;

public class AdapterRecyclerSaleAndSold extends RecyclerView.Adapter<BaseViewHolder> {

    private List<ProductEmpty> items;
    private CollectionReference mColl = FirebaseFirestore.getInstance().collection("Product");
    private String AppID = "AGVBD5SAD1";
    private String APIkey = "9c846f80161a65d44ef5691e991a9ec5";
    private Client client = new Client(AppID, APIkey);
    private PopupMenu popupMenu;
    private View view;

    public AdapterRecyclerSaleAndSold(List<ProductEmpty> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_and_sold, parent, false);
        return new SaleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof SaleHolder) {
            final ProductEmpty empty = items.get(position);
            ((SaleHolder) holder).setItemText(empty.getName_product(),
                    empty.getQrt_product(),
                    empty.getStatus());
            ((SaleHolder) holder).setItemImg(empty.getImage0_product());
            if (empty.getStatus().equals("มีสินค้า")) {
                ((SaleHolder) holder).setColorblue();
            } else {
                ((SaleHolder) holder).setColorred();
            }

            ((SaleHolder) holder).imgDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    popupMenu = new PopupMenu(view.getContext(), v);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.action_dots, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_sale:
                                    editQrtMyproduct(empty.getId_product(), v.getContext());
                                    break;
                                case R.id.delete_sale:
                                    deleteProduct(empty.getId_product());
                                    break;
                            }
                            return true;
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void deleteProduct(final String id) {
        DocumentReference doc = mColl.document(id);
        doc.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteFromAlgolia(id);
                        Log.i("ProfileSaleFragment", "Delete success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("ProfileSaleFragment", "Delete file " + e.getMessage());
                    }
                });
    }

    private void editQrtMyproduct(final String id, final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dialog_edit, null);
        final EditText edtQrt = view.findViewById(R.id.edt_edit_qrt);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("แก้ไขจำนวนสินค้า")
                .setCancelable(true)
                .setView(view)
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (edtQrt.getText().length() >= 0) {
                            int qrt = Integer.parseInt(edtQrt.getText().toString());
                            updateDataFromFireStore(id, qrt);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "ใส่จำนวนไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateDataFromFireStore(final String id, final int qrt) {
        DocumentReference doc = mColl.document(id);
        if (qrt > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("qrt_product", String.valueOf(qrt));
            map.put("status", "มีสินค้า");
            doc.update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updateFromAlgolid(id, qrt, "มีสินค้า");
                            Log.i("ProfileSaleFragment", "Update Firebase success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("ProfileSaleFragment", "Delete Firebase fail " + e.getMessage());
                        }
                    });
        } else if (qrt == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("qrt_product", String.valueOf(qrt));
            map.put("status", "สินค้าหมด");
            doc.update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updateFromAlgolid(id, qrt, "สินค้าหมด");
                            Log.i("ProfileSaleFragment", "Update Firebase success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("ProfileSaleFragment", "Delete Firebase fail " + e.getMessage());
                        }
                    });
        }
    }

    private void updateFromAlgolid(final String id, final int qrt, final String status) {
        final Index index = client.initIndex("Product");
        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    JSONArray jsonArray = content.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String ObjectId = object.getString("objectID");
                        String name_pro = object.getString(KEY_PRODUCT_NAME);
                        String user_name = object.getString(KEY_PRODUCT_USER);
                        String type = object.getString(KEY_PRODUCT_TYPE);
                        String price = object.getString(KEY_PRODUCT_PRICE);
                        String provin = object.getString(KEY_PRODUCT_PROVINCE);
                        String img0 = object.getString(KEY_PRODUCT_IMG0);
                        String img1 = object.getString(KEY_PRODUCT_IMG1);
                        String img2 = object.getString(KEY_PRODUCT_IMG2);
                        String img3 = object.getString(KEY_PRODUCT_IMG3);
                        String img4 = object.getString(KEY_PRODUCT_IMG4);
                        String img5 = object.getString(KEY_PRODUCT_IMG5);
                        String descrip = object.getString(KEY_PRODUCT_DESCRIPTION);
                        String pro_id = object.getString(KEY_PRODUCT_ID);
                        String uid = object.getString(KEY_PRODUCT_UID);
                        String url_profile = object.getString(KEY_PRODUCT_URL_PROFILE);

                        JSONObject upObject = new JSONObject();
                        upObject.put(KEY_PRODUCT_QRT, String.valueOf(qrt));
                        upObject.put(KEY_PRODUCT_NAME, name_pro);
                        upObject.put(KEY_PRODUCT_USER, user_name);
                        upObject.put(KEY_PRODUCT_TYPE, type);
                        upObject.put(KEY_PRODUCT_PRICE, price);
                        upObject.put(KEY_PRODUCT_PROVINCE, provin);
                        upObject.put(KEY_PRODUCT_IMG0, img0);
                        upObject.put(KEY_PRODUCT_IMG1, img1);
                        upObject.put(KEY_PRODUCT_IMG2, img2);
                        upObject.put(KEY_PRODUCT_IMG3, img3);
                        upObject.put(KEY_PRODUCT_IMG4, img4);
                        upObject.put(KEY_PRODUCT_IMG5, img5);
                        upObject.put(KEY_PRODUCT_DESCRIPTION, descrip);
                        upObject.put(KEY_PRODUCT_ID, pro_id);
                        upObject.put(KEY_PRODUCT_UID, uid);
                        upObject.put(KEY_PRODUCT_URL_PROFILE, url_profile);
                        upObject.put(KEY_PRODUCT_STATUS, status);

                        index.saveObjectAsync(upObject, ObjectId, new CompletionHandler() {
                            @Override
                            public void requestCompleted(JSONObject content, AlgoliaException error) {
                                if (error != null) {
                                    Log.i("ProfileSaleFragment", error.getMessage());
                                } else {
                                    Log.i("ProfileSaleFragment", "Update from algolis success");
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        index.searchAsync(new Query(id), completionHandler);
    }

    private void deleteFromAlgolia(String id) {
        final Index index = client.initIndex("Product");
        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    JSONArray jsonArray = content.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String ObjectId = object.getString("objectID");
                        index.deleteObjectAsync(ObjectId, new CompletionHandler() {
                            @Override
                            public void requestCompleted(JSONObject content, AlgoliaException error) {
                                if (error != null) {
                                    Log.i("ProfileSaleFragment", error.getMessage());
                                } else {
                                    Log.i("ProfileSaleFragment", "Delete from algolis success");
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        index.searchAsync(new Query(id), completionHandler);
    }
}
