package com.example.ocr_mparagon1j;

import android.net.Uri;
import android.widget.ImageView;

public class ParagonData {

    public String data, name, imageurl, key;

    public ParagonData(){
        this.data = "data";
        this.name = "name";
        this.imageurl = "imageurl";
    }

    public ParagonData(String data, String name, String imageurl, String key){
        this.data = data;
        this.name = name;
        this.key = key;
        this.imageurl = imageurl;
    }

}
