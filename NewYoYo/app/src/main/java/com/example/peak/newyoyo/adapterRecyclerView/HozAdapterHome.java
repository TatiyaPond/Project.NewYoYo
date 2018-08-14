package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.peak.newyoyo.DetailActivity;
import com.example.peak.newyoyo.Holder.HorzHolderHome;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.empty.ProductEmpty;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerShowAll.KEY_EXTRA_OBJECT;

public class HozAdapterHome extends RecyclerView.Adapter<HorzHolderHome> {

    private Context context;
    private ArrayList<ProductEmpty> data;

    public HozAdapterHome(Context context, ArrayList<ProductEmpty> data){
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public HorzHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_home,parent,false);
        return new HorzHolderHome(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HorzHolderHome holder, int position) {
        final ProductEmpty empty = data.get(position);
        String title = empty.getName_product();
        holder.text.setText(title);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itn = new Intent(holder.context, DetailActivity.class);
                itn.putExtra(KEY_EXTRA_OBJECT, empty);
                holder.context.startActivity(itn);
            }
        });
        Picasso.with(context)
                .load(empty.getImage0_product())
                .centerCrop()
                .fit()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
