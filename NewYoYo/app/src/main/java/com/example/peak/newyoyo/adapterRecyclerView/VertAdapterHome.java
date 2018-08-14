package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.peak.newyoyo.Holder.VertHolderHome;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.ShowAllActivity;
import com.example.peak.newyoyo.empty.VertItemHome;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;

public class VertAdapterHome extends RecyclerView.Adapter<VertHolderHome> {

    private Context context;
    private ArrayList<VertItemHome> data;
    private HozAdapterHome hozAdapterHome;

    public VertAdapterHome(Context context, ArrayList<VertItemHome> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public VertHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vert_home,null,false);
        return new VertHolderHome(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VertHolderHome holder, int position) {
        final VertItemHome item = data.get(position);
        holder.tvtitle.setText(item.getTitle());

        hozAdapterHome = new HozAdapterHome(context,item.getEmpy());
        holder.hozRecycler.setHasFixedSize(true);
        holder.hozRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.hozRecycler.setAdapter(hozAdapterHome);


        holder.tvshowall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itn = new Intent(context, ShowAllActivity.class);
                itn.putExtra("title", item.getTitle());
                context.startActivity(itn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
