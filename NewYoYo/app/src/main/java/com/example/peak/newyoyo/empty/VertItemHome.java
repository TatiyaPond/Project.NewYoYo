package com.example.peak.newyoyo.empty;

import java.util.ArrayList;

public class VertItemHome {
    public String title;
    public ArrayList<ProductEmpty> empy;

    public VertItemHome(String title, ArrayList<ProductEmpty> empy) {
        this.title = title;
        this.empy = empy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ProductEmpty> getEmpy() {
        return empy;
    }

    public void setEmpy(ArrayList<ProductEmpty> empy) {
        this.empy = empy;
    }
}
