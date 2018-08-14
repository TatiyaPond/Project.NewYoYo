package com.example.peak.newyoyo.Holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peak.newyoyo.R;
import com.example.peak.newyoyo.adapterRecyclerView.ItemClickListener;
import com.squareup.picasso.Picasso;

public class SearchHolder extends BaseViewHolder implements View.OnClickListener {

    ImageView img;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvProvince;
    TextView tvAmount;
    Context mContext;
    ItemClickListener itemClickListener;

    public SearchHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        img = itemView.findViewById(R.id.item_img_search);
        tvTitle = itemView.findViewById(R.id.item_text_search);
        tvPrice = itemView.findViewById(R.id.item_price_search);
        tvProvince = itemView.findViewById(R.id.item_province_search);
        tvAmount = itemView.findViewById(R.id.item_amount_search);
        itemView.setOnClickListener(this);
    }

    public void setItemText(String title, String price, String province, String amount) {
        tvTitle.setText(title);
        tvPrice.setText("ราคา " + price);
        tvProvince.setText("จังหวัด " + province);
        tvAmount.setText("จำนวน " + amount);
    }

    public void setItemImg(String urlImg) {
        Picasso.with(mContext)
                .load(urlImg)
                .centerCrop()
                .fit()
                .into(img);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false, null);
    }
}
