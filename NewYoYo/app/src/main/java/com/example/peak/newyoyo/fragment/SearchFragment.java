package com.example.peak.newyoyo.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerSearch;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_DESCRIPTION;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG0;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG1;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG2;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG3;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG4;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_IMG5;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_NAME;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_PHONE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_PRICE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_PROVINCE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_QRT;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_STATUS;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_TYPE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_UID;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_URL_PROFILE;
import static com.example.peak.newyoyo.AddProductActivity.KEY_PRODUCT_USER;

/**
 * Created by peak on 5/16/2018.
 */

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mCollection = db.collection("Product");
    public static View rootViewSearch;

    private String AppID = "AGVBD5SAD1";
    private String APIkey = "9c846f80161a65d44ef5691e991a9ec5";

    private EditText edtSearch;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AdapterRecyclerSearch adapter;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootViewSearch = inflater.inflate(R.layout.fragment_search, container, false);
        edtSearch = rootViewSearch.findViewById(R.id.edit_search);
        setClearDrawableEdit(edtSearch);
        setOnEditorEditEnterKey(edtSearch);
        mRecyclerView = rootViewSearch.findViewById(R.id.rcycler_search);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return rootViewSearch;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setClearDrawableEdit(final EditText edtSearch) {
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                showKeyboard(rootViewSearch.getContext(), rootViewSearch);
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_edit_clear, null);
                edtSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

                Drawable[] drawables = edtSearch.getCompoundDrawables();
                if (drawables[2] == null) {
                    return false;
                }
                int editWidth = edtSearch.getWidth();
                int drawableWidth = drawables[2].getIntrinsicWidth();
                int drawablePos = editWidth - edtSearch.getPaddingRight() - drawableWidth;
                float x = event.getX();
                if (x >= drawablePos) {
                    edtSearch.setText("");
                }
                return false;
            }
        });
    }

    private void setOnEditorEditEnterKey(final EditText edit) {
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(rootViewSearch.getContext(), rootViewSearch);
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    searchProduct(edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void showKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
    }

    private void searchProduct(String keyWord) {
        Client client = new Client(AppID, APIkey);
        Index index = client.initIndex("Product");

        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (error != null) {
                    Log.i(TAG, error.getMessage());
                }

                if (content != null) {
                    ArrayList<ProductEmpty> arrProduct = new ArrayList<>();
                    try {
                        JSONArray jsonArray = content.getJSONArray("hits");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ProductEmpty empty = new ProductEmpty(
                                    object.getString(KEY_PRODUCT_USER),
                                    object.getString(KEY_PRODUCT_NAME),
                                    object.getString(KEY_PRODUCT_TYPE),
                                    object.getString(KEY_PRODUCT_PRICE),
                                    object.getString(KEY_PRODUCT_QRT),
                                    object.getString(KEY_PRODUCT_PROVINCE),
                                    object.getString(KEY_PRODUCT_DESCRIPTION),
                                    object.getString(KEY_PRODUCT_PHONE),
                                    object.getString(KEY_PRODUCT_URL_PROFILE),
                                    object.getString(KEY_PRODUCT_UID),
                                    object.getString(KEY_PRODUCT_STATUS));
                            empty.setImage0_product(object.getString(KEY_PRODUCT_IMG0));
                            empty.setImage1_product(object.getString(KEY_PRODUCT_IMG1));
                            empty.setImage2_product(object.getString(KEY_PRODUCT_IMG2));
                            empty.setImage3_product(object.getString(KEY_PRODUCT_IMG3));
                            empty.setImage4_product(object.getString(KEY_PRODUCT_IMG4));
                            empty.setImage5_product(object.getString(KEY_PRODUCT_IMG5));
                            arrProduct.add(empty);
                        }
                        adapter = new AdapterRecyclerSearch(arrProduct);
                        mRecyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Query query = new Query(keyWord);
        index.searchAsync(query, completionHandler);

    }

}
