package com.shuyun.data.coupon.dao;

import com.shuyun.data.coupon.pojo.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IGoodsDao {

    List<Goods> getGoodsList(@Param("offset") int offset, @Param("limit") int limit);

    List<Goods> getGoods(@Param("id") long id, @Param("limit") int limit);



}
