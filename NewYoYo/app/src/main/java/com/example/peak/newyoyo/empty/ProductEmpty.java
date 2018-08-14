package com.example.peak.newyoyo.empty;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductEmpty implements Parcelable{
    private String id_product = null;
    private String name_user = null;
    private String Uid = null;
    private String url_imgprofile = null;
    private String name_product = null;
    private String type_product = null;
    private String price_product = null;
    private String qrt_product = null;
    private String province_product = null;
    private String description_product = null;
    private String phone_product = null;
    private String image0_product = null;
    private String image1_product = null;
    private String image2_product = null;
    private String image3_product = null;
    private String image4_product = null;
    private String image5_product = null;
    private String status = null;

    public ProductEmpty() {
    }

    public ProductEmpty(String nameuser,String namePro, String typePro, String pricePro, String qrtPro,
                        String provincePro, String descripPro, String phonePro, String url_imgprofile, String Uid, String status){
        this.name_user = nameuser;
        this.name_product = namePro;
        this.type_product = typePro;
        this.price_product = pricePro;
        this.qrt_product = qrtPro;
        this.province_product = provincePro;
        this.description_product = descripPro;
        this.phone_product = phonePro;
        this.url_imgprofile = url_imgprofile;
        this.Uid = Uid;
        this.status = status;
    }

    protected ProductEmpty(Parcel in) {
        id_product = in.readString();
        name_user = in.readString();
        Uid = in.readString();
        url_imgprofile = in.readString();
        name_product = in.readString();
        type_product = in.readString();
        price_product = in.readString();
        qrt_product = in.readString();
        province_product = in.readString();
        description_product = in.readString();
        image0_product = in.readString();
        image1_product = in.readString();
        image2_product = in.readString();
        image3_product = in.readString();
        image4_product = in.readString();
        image5_product = in.readString();
        status = in.readString();
        phone_product = in.readString();
    }

    public static final Creator<ProductEmpty> CREATOR = new Creator<ProductEmpty>() {
        @Override
        public ProductEmpty createFromParcel(Parcel in) {
            return new ProductEmpty(in);
        }

        @Override
        public ProductEmpty[] newArray(int size) {
            return new ProductEmpty[size];
        }
    };



    public String getPhone_product() {
        return phone_product;
    }

    public void setPhone_product(String phone_product) {
        this.phone_product = phone_product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getName_product() {
        return name_product;
    }

    public void setName_product(String name_product) {
        this.name_product = name_product;
    }

    public String getType_product() {
        return type_product;
    }

    public void setType_product(String type_product) {
        this.type_product = type_product;
    }

    public String getPrice_product() {
        return price_product;
    }

    public void setPrice_product(String price_product) {
        this.price_product = price_product;
    }

    public String getQrt_product() {
        return qrt_product;
    }

    public void setQrt_product(String qrt_product) {
        this.qrt_product = qrt_product;
    }

    public String getProvince_product() {
        return province_product;
    }

    public void setProvince_product(String province_product) {
        this.province_product = province_product;
    }

    public String getDescription_product() {
        return description_product;
    }

    public void setDescription_product(String description_product) {
        this.description_product = description_product;
    }

    public String getImage0_product() {
        return image0_product;
    }

    public void setImage0_product(String image0_product) {
        this.image0_product = image0_product;
    }

    public String getImage1_product() {
        return image1_product;
    }

    public void setImage1_product(String image1_product) {
        this.image1_product = image1_product;
    }

    public String getImage2_product() {
        return image2_product;
    }

    public void setImage2_product(String image2_product) {
        this.image2_product = image2_product;
    }

    public String getImage3_product() {
        return image3_product;
    }

    public void setImage3_product(String image3_product) {
        this.image3_product = image3_product;
    }

    public String getImage4_product() {
        return image4_product;
    }

    public void setImage4_product(String image4_product) {
        this.image4_product = image4_product;
    }

    public String getImage5_product() {
        return image5_product;
    }

    public void setImage5_product(String image5_product) {
        this.image5_product = image5_product;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public String getUrl_imgprofile() {
        return url_imgprofile;
    }

    public void setUrl_imgprofile(String url_imgprofile) {
        this.url_imgprofile = url_imgprofile;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_product);
        dest.writeString(name_user);
        dest.writeString(Uid);
        dest.writeString(url_imgprofile);
        dest.writeString(name_product);
        dest.writeString(type_product);
        dest.writeString(price_product);
        dest.writeString(qrt_product);
        dest.writeString(province_product);
        dest.writeString(description_product);
        dest.writeString(image0_product);
        dest.writeString(image1_product);
        dest.writeString(image2_product);
        dest.writeString(image3_product);
        dest.writeString(image4_product);
        dest.writeString(image5_product);
        dest.writeString(status);
        dest.writeString(phone_product);
    }
}
