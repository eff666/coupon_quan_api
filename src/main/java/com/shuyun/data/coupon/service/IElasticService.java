package com.shuyun.data.coupon.service;

import com.shuyun.data.coupon.pojo.Goods;
import com.shuyun.data.coupon.pojo.GoodsParser;
import com.shuyun.data.coupon.result.GoodsResult;

import java.util.List;

public interface IElasticService {

     boolean upsert(Goods goods);

     List<Goods> queryGoods(String title, int offset, int limit) ;

     List<Goods> getGoodsList(int offset, int limit) ;

    GoodsResult saveGoods(String saveData);
}
