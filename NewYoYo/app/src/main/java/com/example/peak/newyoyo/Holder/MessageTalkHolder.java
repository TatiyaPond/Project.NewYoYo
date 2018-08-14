package com.example.peak.newyoyo.Holder;

import android.view.View;
import android.widget.TextView;

import com.example.peak.newyoyo.R;

public class MessageTalkHolder extends BaseViewHolder {
    private TextView textMessage, textTime, textSeen;

    public MessageTalkHolder(View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.item_text_message_talk);
        textTime = itemView.findViewById(R.id.item_text_time_talk);
        textSeen = itemView.findViewById(R.id.item_text_seen_talk);
    }

    public void setItemText(String text, String time){
        textMessage.setText(text);
        textTime.setText(time);
    }

    public void setItemSeen(boolean seen){
        if(!seen){
            textSeen.setVisibility(View.GONE);
        }else{
            textSeen.setVisibility(View.VISIBLE);
        }
    }
}
