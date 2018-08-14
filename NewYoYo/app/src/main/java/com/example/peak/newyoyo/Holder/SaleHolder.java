package com.example.peak.newyoyo.Holder;

import android.content.Context;
import android.graphics.Color;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.adapterRecyclerView.ItemClickListener;
import com.squareup.picasso.Picasso;

public class SaleHolder extends BaseViewHolder implements View.OnClickListener{
    private ImageView img;
    public ImageView imgDots;
    private TextView textName, textAmount, textStatus;
    private Context mContext;
    public PopupMenu popupMenu;
    private ItemClickListener listener;

    public SaleHolder(final View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.item_img_sale);
        textName = itemView.findViewById(R.id.item_text_sale);
        textAmount = itemView.findViewById(R.id.item_amount_sale);
        textStatus = itemView.findViewById(R.id.item_status_sale);
        imgDots = itemView.findViewById(R.id.item_dots_sale);
        mContext = itemView.getContext();
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    public void setItemText(String name, String amount, String status){
        textName.setText(name);
        textAmount.setText("จำนวน " + amount);
        textStatus.setText(status);
    }

    public void setColorblue(){
        textStatus.setTextColor(Color.parseColor("#d9152ff6"));
    }

    public void setColorred(){
        textStatus.setTextColor(Color.parseColor("#ff0f0f"));
    }

    public void setItemImg(String urlImg) {
        Picasso.with(mContext)
                .load(urlImg)
                .centerCrop()
                .fit()
                .into(img);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false, null);
    }
}
