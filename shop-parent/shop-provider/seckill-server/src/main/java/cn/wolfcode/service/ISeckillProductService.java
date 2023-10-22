package cn.wolfcode.service;

import cn.wolfcode.domain.SeckillProduct;
import cn.wolfcode.domain.SeckillProductVo;

import java.util.List;

/**
 * Created by lanxw
 */
public interface ISeckillProductService {
    List<SeckillProduct> queryCurrentlySeckillProduct(Integer time);

    List<SeckillProductVo> queryByTime(Integer time);

    SeckillProductVo findDetails(Integer time, Long seckillId);

    int decrStockCount(Long id);

    List<SeckillProductVo> queryByTimeCache(Integer time);

    SeckillProductVo findDetailsCache(Integer time, Long seckillId);

    void syncStockToRedis(Integer time, Long seckillId);

    void incrStockCount(Long seckillId);
}
