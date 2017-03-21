package com.shuyun.data.coupon.pojo;

public class CouponDetails {
    //表示起始日期
    private String start_date;
    //表示截止日期
    private String end_date;
    //表示优惠券名称
    private String coupon_name;
    //表示优惠券剩余总数
    private String coupons;
    //表示已领取总数
    private String already_receive;
    //表示优惠券使用描述
    private String coupons_desc;
    //表示店铺名称
    private String shop_name;
    //表示店铺ID
    private String shop_id;
    //表示券图片链接
    private String image_url = "//assets.alicdn.com/mw/app/msp/h5/images/coupon.png";

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public String getCoupons() {
        return coupons;
    }

    public void setCoupons(String coupons) {
        this.coupons = coupons;
    }

    public String getAlready_receive() {
        return already_receive;
    }

    public void setAlready_receive(String already_receive) {
        this.already_receive = already_receive;
    }

    public String getCoupons_desc() {
        return coupons_desc;
    }

    public void setCoupons_desc(String coupons_desc) {
        this.coupons_desc = coupons_desc;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
