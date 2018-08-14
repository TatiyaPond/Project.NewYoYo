package com.example.peak.newyoyo.adapterRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.peak.newyoyo.Holder.BaseViewHolder;
import com.example.peak.newyoyo.Holder.MessageTalk2Holder;
import com.example.peak.newyoyo.Holder.MessageTalkHolder;
import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.empty.MessageTalk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMessageTalk extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<MessageTalk> mMessage = new ArrayList<>();
    private Context mContext;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private int Type_A = 1, Type_B = 2;

    public AdapterMessageTalk() {
    }

    public AdapterMessageTalk(Context mContext, ArrayList<MessageTalk> mMessage) {
        this.mContext = mContext;
        this.mMessage = mMessage;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootview = null;
        switch (viewType) {
            case 1:
                rootview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_talk, null, false);
                return new MessageTalkHolder(rootview);
            case 2:
                rootview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_talk_b, null, false);
                return new MessageTalk2Holder(rootview);
            default:
                break;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        String from = mMessage.get(position).getFrom();
        if (from.equals(mUser.getDisplayName())) {
            return Type_A;
        } else {
            return Type_B;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        if(holder instanceof MessageTalkHolder){
            MessageTalk item = mMessage.get(position);
            ((MessageTalkHolder) holder).setItemText(item.getMessage(),item.getTime());
            ((MessageTalkHolder) holder).setItemSeen(item.isSeen());
        }
        if(holder instanceof MessageTalk2Holder){
            MessageTalk item = mMessage.get(position);
            ((MessageTalk2Holder) holder).setItemText(item.getMessage(),item.getTime());
            ((MessageTalk2Holder) holder).setItemImage(item.getUrlProfile());
        }
    }

    @Override
    public int getItemCount() {
        if(!mMessage.isEmpty() || mMessage !=null) {
            return mMessage.size();
        }
        return 0;
    }

}
