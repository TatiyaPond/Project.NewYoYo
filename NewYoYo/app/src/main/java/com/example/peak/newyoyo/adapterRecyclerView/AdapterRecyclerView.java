package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.peak.newyoyo.R;

import java.util.ArrayList;

/**
 * Created by peak on 5/21/2018.
 */

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {
    private ArrayList<Bitmap> arrybitmaps;
    private Context mContext;

    public AdapterRecyclerView(ArrayList<Bitmap> arrybitmaps, Context mContext) {
        this.arrybitmaps = arrybitmaps;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_add_image,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img.setImageBitmap(arrybitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return arrybitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.image_add_item);
        }
    }
}

