package com.shuyun.data.coupon.web;

import com.shuyun.data.coupon.result.GoodsResult;
import com.shuyun.data.coupon.result.ReportResult;
import com.shuyun.data.coupon.service.IElasticService;
import com.shuyun.data.coupon.service.IGoodsService;
import com.shuyun.query.meta.ReportResultSimple;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Resource
    private IGoodsService goodsService;

    @Resource
    private IElasticService elasticService;


//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public List<Goods> list(Integer offset, Integer limit) {
//
//        offset = offset == null ? 0 : offset;
//        limit = limit == null ? 20 : limit;
//        List<Goods> list = goodsService.getGoodsList(offset, limit);
//
//        return list;
//    }

    /*
     * 1、maimaimai
     */
    //maimaimai接口查询
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public ReportResultSimple search(@RequestBody String querData) {
        return goodsService.searchGoods(querData);
    }

    /*
     * 2、推宝
     */
    //商品优惠券和商品链接领取查询
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ReportResult query(@RequestBody String queryData) {
        return goodsService.queryGoods(queryData);
    }

    /*
     * 3、推宝
     */
    //将商品数据保存到es
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public GoodsResult saveGoods(@RequestBody String saveData) {
        return elasticService.saveGoods(saveData);
    }
}


