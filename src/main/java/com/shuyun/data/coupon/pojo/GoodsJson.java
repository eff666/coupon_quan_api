package com.shuyun.data.coupon.pojo;

public class GoodsJson {
    //推广key
    private String key;

    /*
      1、商品信息
     */
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
    //商品推广状态
    private String goods_state = "即将推广";


    /*
      2、优惠券信息
     */
    //表示起始日期
    private String start_date;
    //表示截止日期
    private String end_date;
    //表示优惠券名称
    private String coupon_name;
    //表示优惠券剩余总数
    private long coupons;
    //表示已领取总数
    private long already_receive;
    //表示优惠券使用描述
    private String coupons_desc;
    //表示店铺名称
    private String shop_name;
    //表示店铺ID
    private String shop_id;
    //表示券图片链接
    private String image_url = "//assets.alicdn.com/mw/app/msp/h5/images/coupon.png";

    //3、
    //商品佣金比例
    private double goods_cps;

    //4、推广日期
    //推广日期
    private String promotion_start_date;
    private String promotion_end_date;

    //5、商品insert日期
    private long insert_date;
    private long modify_date;

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

    public long getCoupons() {
        return coupons;
    }

    public void setCoupons(long coupons) {
        this.coupons = coupons;
    }

    public long getAlready_receive() {
        return already_receive;
    }

    public void setAlready_receive(long already_receive) {
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

    public double getGoods_cps() {
        return goods_cps;
    }

    public void setGoods_cps(double goods_cps) {
        this.goods_cps = goods_cps;
    }

    public String getPromotion_start_date() {
        return promotion_start_date;
    }

    public void setPromotion_start_date(String promotion_start_date) {
        this.promotion_start_date = promotion_start_date;
    }

    public String getPromotion_end_date() {
        return promotion_end_date;
    }

    public void setPromotion_end_date(String promotion_end_date) {
        this.promotion_end_date = promotion_end_date;
    }

    public long getInsert_date() {
        return insert_date;
    }

    public void setInsert_date(long insert_date) {
        this.insert_date = insert_date;
    }

    public long getModify_date() {
        return modify_date;
    }

    public void setModify_date(long modify_date) {
        this.modify_date = modify_date;
    }
}
