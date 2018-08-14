package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.example.peak.newyoyo.TalkActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class AdapterRecyclerChat extends RecyclerView.Adapter<AdapterRecyclerChat.ViewHolder> {

    private ArrayList<Map> arrChat;
    private Context mContext;

    public AdapterRecyclerChat(){

    }
    public AdapterRecyclerChat(Context mContext, ArrayList<Map> arrChat){
        this.arrChat = arrChat;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_chat,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Map map = arrChat.get(position);
        holder.textName.setText(map.get("nameUser").toString());

        Picasso.with(mContext).load(map.get("urlProfile").toString())
                .placeholder(R.drawable.ic_person_default)
                .into(holder.imgProfile);

        holder.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick, MotionEvent motionEvent) {
                Intent itn = new Intent(v.getContext(), TalkActivity.class);
                itn.putExtra("UID", map.get("uid").toString());
                itn.putExtra("name", map.get("nameUser").toString());
                itn.putExtra("urlImage", map.get("urlProfile").toString());
                v.getContext().startActivity(itn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgProfile;
        TextView textName;
        private ItemClickListener Listener;
        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.item_text_name_chat);
            imgProfile = itemView.findViewById(R.id.item_img_chat);
            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(ItemClickListener Listener){
            this.Listener = Listener;
        }


        @Override
        public void onClick(View v) {
            Listener.onClick(v,getAdapterPosition(),false,null);
        }
    }
}
