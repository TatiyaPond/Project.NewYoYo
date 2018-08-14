package com.example.peak.newyoyo.adapterRecyclerView;

import android.view.MotionEvent;
import android.view.View;

import com.example.peak.newyoyo.empty.ProductEmpty;

import java.util.ArrayList;

public interface ItemClickListener {
    void onClick(View v, int position, boolean isLongClick, MotionEvent motionEvent);
}
