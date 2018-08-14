package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peak.newyoyo.DetailActivity;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.paginate.recycler.LoadingListItemCreator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRecyclerShowAll extends RecyclerView.Adapter<AdapterRecyclerShowAll.ViewHolder>{
    public static final String KEY_EXTRA_OBJECT = "ProductEmpty";

    private Context mContext;
    private ArrayList<ProductEmpty> data;


    public AdapterRecyclerShowAll(Context mContext, ArrayList<ProductEmpty> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootview = LayoutInflater.from(mContext).inflate(R.layout.item_show_all, parent, false);
        return new ViewHolder(rootview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProductEmpty empty = data.get(position);
        holder.tvTitle.setText(empty.getName_product());
        holder.tvPrice.setText("฿ " + empty.getPrice_product());
        holder.tvProvince.setText("จังหวัด " + empty.getProvince_product());
        holder.tvAmount.setText("จำนวน " + empty.getQrt_product());
        Picasso.with(mContext)
                .load(empty.getImage0_product())
                .centerCrop()
                .fit()
                .into(holder.img);
        holder.setOnClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick, MotionEvent motionEvent) {
                Intent itn = new Intent(v.getContext(),DetailActivity.class);
                itn.putExtra(KEY_EXTRA_OBJECT,empty);
                mContext.startActivity(itn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvProvince;
        TextView tvAmount;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_img_show_all);
            tvTitle = itemView.findViewById(R.id.item_text_show_all);
            tvPrice = itemView.findViewById(R.id.item_price_showall);
            tvProvince = itemView.findViewById(R.id.item_province_showall);
            tvAmount = itemView.findViewById(R.id.item_amount_showall);
            itemView.setOnClickListener(this);
        }

        public void setOnClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false, null);
        }
    }
}
