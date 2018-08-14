package com.example.peak.newyoyo.Holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peak.newyoyo.R;
import com.squareup.picasso.Picasso;

public class MessageTalk2Holder extends BaseViewHolder {
    TextView textMessage, textTime;
    ImageView imgProfile;
    Context mContext;

    public MessageTalk2Holder(View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.item_text_message_talk);
        imgProfile = itemView.findViewById(R.id.item_img_profile_talk);
        textTime = itemView.findViewById(R.id.item_text_time_talk);
        mContext = itemView.getContext();
    }

    public void setItemText(String text, String time){
        textMessage.setText(text);
        textTime.setText(time);
    }

    public void setItemImage(String URLImage){
        Picasso.with(mContext).load(URLImage)
                .placeholder(R.drawable.ic_person_default)
                .into(imgProfile);
    }
}
