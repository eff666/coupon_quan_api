package com.shuyun.data.coupon.pojo;

import java.util.ArrayList;
import java.util.List;

public class GoodsParser {
    //商品信息
    private List<Goods> goods_list = new ArrayList<Goods>();

    //优惠券信息
    private CouponDetails coupon;

    //商品佣金比例
    private double goods_cps;

    //商品推广日期
    private PromotionDate promotion_date;




    public List<Goods> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<Goods> goods_list) {
        this.goods_list = goods_list;
    }

    public CouponDetails getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponDetails coupon) {
        this.coupon = coupon;
    }

    public double getGoods_cps() {
        return goods_cps;
    }

    public void setGoods_cps(double goods_cps) {
        this.goods_cps = goods_cps;
    }

    public PromotionDate getPromotion_date() {
        return promotion_date;
    }

    public void setPromotion_date(PromotionDate promotion_date) {
        this.promotion_date = promotion_date;
    }
}
