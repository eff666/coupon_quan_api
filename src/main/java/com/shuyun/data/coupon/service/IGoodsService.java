package com.shuyun.data.coupon.service;

import com.shuyun.data.coupon.result.ReportResult;
import com.shuyun.query.meta.ReportResultSimple;

import java.util.List;

public interface IGoodsService {

    //List<Goods> getGoodsList(int offset, int limit);

    ReportResult queryGoods(String queryData) ;

    ReportResultSimple searchGoods(String queryData);

}
