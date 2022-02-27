package com.example.ocr_mparagon1j;

public class UploadInfo {

    public String imageName;
    public String imageURL;

    public UploadInfo(String name, String url) {
        this.imageName = name;
        this.imageURL = url;
    }

    public String getImageName() {
        return imageName;
    }
    public String getImageURL() {
        return imageURL;
    }
}