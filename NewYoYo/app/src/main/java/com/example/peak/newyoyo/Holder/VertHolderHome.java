package com.example.peak.newyoyo.Holder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.peak.newyoyo.R;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

public class VertHolderHome extends RecyclerView.ViewHolder {
    public TextView tvtitle;
    public TextView tvshowall;
    public RecyclerView hozRecycler;

    public VertHolderHome(View itemView) {
        super(itemView);
        tvtitle = itemView.findViewById(R.id.item_vert_home_title);
        tvshowall = itemView.findViewById(R.id.item_vert_home_showall);
        hozRecycler = itemView.findViewById(R.id.item_vert_home_recycler);
        SnapHelper snap = new GravitySnapHelper(Gravity.START);
        snap.attachToRecyclerView(hozRecycler);
    }
}
