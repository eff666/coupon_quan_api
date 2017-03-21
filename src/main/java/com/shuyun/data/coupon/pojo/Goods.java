package com.shuyun.data.coupon.pojo;

import java.math.BigInteger;
import java.util.Date;

public class Goods {

    //推广key
    private String key;
//    //商品创建日期
//    private String create_date;
//    //商品修改日期
//    private String modify_date;

    //商品id
    private String goods_id;
    //商品推广key
    private String goods_seller_id;
    //商品推广key
    private String goods_activity_id;
    //商品名称
    private String goods_name;
    //商品价格
    private double goods_price;
    //商品图片url
    private String goods_image_url;
    //商品详情url
    private String goods_detail_url;
    //商品佣金比例
    //private double goods_cps;
    //商品是否删除
    //private String goods_is_delete = "false";
    //商品推广状态
    private String goods_state = "即将推广";

    //优惠券id
    //private String coupon_id;
    //店铺id
//    private String coupon_shop_id;
//    //优惠券名称
//    private String coupon_name;
//    //优惠券描述
//    private String coupons_desc;
//    //优惠券图片
//    private String coupon_image_url = "//assets.alicdn.com/mw/app/msp/h5/images/coupon.png";
//    //优惠券剩余总量
//    private long coupon_coupons;
//    //优惠券已领取总数
//    private long coupon_already_receive;
//    //优惠券开始日期
//    private String coupon_start_date;
//    //优惠券截止日期
//    private String coupon_end_date;
//    //优惠券是否可用
//    private String coupon_is_delete = "false";


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_seller_id() {
        return goods_seller_id;
    }

    public void setGoods_seller_id(String goods_seller_id) {
        this.goods_seller_id = goods_seller_id;
    }

    public String getGoods_activity_id() {
        return goods_activity_id;
    }

    public void setGoods_activity_id(String goods_activity_id) {
        this.goods_activity_id = goods_activity_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public double getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(double goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_image_url() {
        return goods_image_url;
    }

    public void setGoods_image_url(String goods_image_url) {
        this.goods_image_url = goods_image_url;
    }

    public String getGoods_detail_url() {
        return goods_detail_url;
    }

    public void setGoods_detail_url(String goods_detail_url) {
        this.goods_detail_url = goods_detail_url;
    }

    public String getGoods_state() {
        return goods_state;
    }

    public void setGoods_state(String goods_state) {
        this.goods_state = goods_state;
    }
}


