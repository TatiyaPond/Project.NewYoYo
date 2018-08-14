package com.example.peak.newyoyo.Holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peak.newyoyo.R;

public class HorzHolderHome extends RecyclerView.ViewHolder {

    public ImageView img;
    public TextView text;
    public Context context;

    public HorzHolderHome(View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.item_text_home);
        img = itemView.findViewById(R.id.item_img_home);
        context = itemView.getContext();
    }
}
