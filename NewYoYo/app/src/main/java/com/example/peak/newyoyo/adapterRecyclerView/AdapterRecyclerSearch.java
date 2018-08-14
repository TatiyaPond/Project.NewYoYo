package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.peak.newyoyo.DetailActivity;
import com.example.peak.newyoyo.Holder.BaseViewHolder;
import com.example.peak.newyoyo.Holder.SearchHolder;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.empty.ProductEmpty;

import java.util.ArrayList;

import static com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerShowAll.KEY_EXTRA_OBJECT;

public class AdapterRecyclerSearch extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<ProductEmpty> item;

    public AdapterRecyclerSearch(ArrayList<ProductEmpty> item) {
        this.item = item;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if(holder instanceof SearchHolder){
            final ProductEmpty productEmpty = item.get(position);
            ((SearchHolder) holder).setItemText(productEmpty.getName_product(),
                                                productEmpty.getPrice_product(),
                                                productEmpty.getProvince_product(),
                                                productEmpty.getQrt_product());

            ((SearchHolder) holder).setItemImg(productEmpty.getImage0_product());
            ((SearchHolder) holder).setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View v, int position, boolean isLongClick, MotionEvent motionEvent) {
                    Intent itn = new Intent(v.getContext(),DetailActivity.class);
                    itn.putExtra(KEY_EXTRA_OBJECT,productEmpty);
                    v.getContext().startActivity(itn);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}
