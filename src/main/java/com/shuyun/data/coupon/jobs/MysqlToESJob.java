//package com.shuyun.data.coupon.jobs;
//
//
//
//import com.shuyun.data.coupon.cache.RedisCache;
//import com.shuyun.data.coupon.dao.IGoodsDao;
//import com.shuyun.data.coupon.pojo.Goods;
//import com.shuyun.data.coupon.service.IElasticService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class MysqlToESJob implements Runnable {
//
//    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
//
//    private static final String CACHE_KEY = "transfer|lastId";
//    private static final Integer LIMIT = 200;
//    private static final Long SLEEP_TIME = 10000l;
//
//
//    @Autowired
//    private IGoodsDao goodsDao;
//    @Autowired
//    private RedisCache cache;
//    @Autowired
//    private IElasticService elasticService;
//
//
//    @Override
//    public void run() {
//        while (true) {
//            try {
//                //从缓存中读取上次读取的最大ID
//                String cacheValue = cache.getCache(CACHE_KEY, String.class);
//                Long lastId;
//                if (cacheValue == null) {
//                    LOG.info("get cache with key:{} value is null", CACHE_KEY);
//                    lastId = 0l;
//                } else {
//                    lastId = Long.parseLong(cacheValue);
//                    LOG.info("get cache with key:{},with value :{}", CACHE_KEY, lastId);
//                }
//
//                //从mysql中读取对应的数据
//                List<Goods> goodsList = goodsDao.getGoods(lastId, LIMIT);
//                if(goodsList.size() != 0){
//                    for (Goods goods : goodsList) {
//                        try {
//                            //更新last id
//                            if (goods.getId() > lastId) {
//                                lastId = goods.getId();
//                            }
//                            //把数据写入elasticsearch
//                            if (!elasticService.upsert(goods)) {
//                                LOG.error("upert failed with goods id :{}", goods.getGoodid());
//                            }
//                        } catch (Exception e) {
//                            LOG.error(e.getMessage(), e);
//                        }
//                    }
//
//                    //把读取过的数据中最大的id写入 redis
//                    if(cache.setCache(CACHE_KEY, lastId.toString())){
//                        LOG.info("put cache with key:{} with value :{}", CACHE_KEY, lastId);
//                    }
//                }
//
//
//                //如果当前不能读到最大limit数据，sleep 10s
//                if (goodsList.size() < LIMIT) {
//                    try {
//                        Thread.currentThread().sleep(SLEEP_TIME);
//                    } catch (InterruptedException e) {
//                        LOG.error(e.getMessage(), e);
//                    }
//                }
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//            }
//        }
//    }
//}
