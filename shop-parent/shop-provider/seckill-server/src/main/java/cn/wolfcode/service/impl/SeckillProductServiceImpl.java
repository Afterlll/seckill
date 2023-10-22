package cn.wolfcode.service.impl;

import cn.wolfcode.common.exception.BusinessException;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.domain.SeckillProduct;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.mapper.SeckillProductMapper;
import cn.wolfcode.redis.SeckillRedisKey;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.web.feign.ProductFeignApi;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lanxw
 */
@Service
public class SeckillProductServiceImpl implements ISeckillProductService {
    @Autowired
    private SeckillProductMapper seckillProductMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private ProductFeignApi productFeignApi;

    @Override
    public List<SeckillProduct> queryCurrentlySeckillProduct(Integer time) {
        return seckillProductMapper.queryCurrentlySeckillProduct(time);
    }

    @Override
    public List<SeckillProductVo> queryByTime(Integer time) {
        // 1. 查询出当天所有的秒杀商品数据
        List<SeckillProduct> seckillProductList = queryCurrentlySeckillProduct(time);
        if (seckillProductList == null) {
            return Collections.emptyList();
        }
        // 2. 根据秒杀商品信息查询出所有的商品id
        List<Long> seckillProductIds = seckillProductList.stream().map(SeckillProduct::getProductId).collect(Collectors.toList());
        // 3. 远程调用商品服务根据获取所有商品信息
        Result<List<Product>> result = productFeignApi.getProductByIds(seckillProductIds);
        // 商品服务出错
        if (result == null || result.hasError()) {
            throw new BusinessException(SeckillCodeMsg.PRODUCT_SERVER_ERROR);
        }
        List<Product> productList = result.getData();
        Map<Long, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getId, product -> product));
        // 4. 封装vo，返回响应数据
        return seckillProductList.stream().map(seckillProduct -> {
            SeckillProductVo seckillProductVo = new SeckillProductVo();
            BeanUtils.copyProperties(productMap.get(seckillProduct.getProductId()), seckillProductVo);
            BeanUtils.copyProperties(seckillProduct, seckillProductVo);
            seckillProductVo.setCurrentCount(seckillProduct.getStockCount());
            return seckillProductVo;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillProductVo findDetails(Integer time, Long seckillId) {
        // 查询出的对应秒杀商品的信息
        SeckillProduct seckillProduct = seckillProductMapper.findDetails(seckillId);
        if (seckillProduct == null) {
            throw new BusinessException(SeckillCodeMsg.PRODUCT_SERVER_ERROR);
        }
        // 调用远程服务查出商品信息
        Result<List<Product>> result = productFeignApi.getProductByIds(Collections.singletonList(seckillProduct.getProductId()));
        if (result == null || result.hasError()) {
            throw new BusinessException(SeckillCodeMsg.PRODUCT_SERVER_ERROR);
        }
        SeckillProductVo seckillProductVo = new SeckillProductVo();
        BeanUtils.copyProperties(result.getData().get(0), seckillProductVo);
        BeanUtils.copyProperties(seckillProduct, seckillProductVo);
        seckillProductVo.setCurrentCount(seckillProduct.getStockCount());
        return seckillProductVo;
    }

    // 库存-1
    @Override
    public int decrStockCount(Long id) {
        return seckillProductMapper.decrStock(id);
    }

    @Override
    public List<SeckillProductVo> queryByTimeCache(Integer time) {
        return redisTemplate.opsForHash().values(SeckillRedisKey.SECKILL_PRODUCT_HASH.getRealKey(String.valueOf(time)))
                .stream()
                .map(object -> JSON.parseObject((String) object, SeckillProductVo.class))
                .collect(Collectors.toList());
    }

    @Override
    public SeckillProductVo findDetailsCache(Integer time, Long seckillId) {
        // 从redis集合中获取到缓存数据
        return JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(SeckillRedisKey.SECKILL_PRODUCT_HASH.getRealKey(String.valueOf(time)), String.valueOf(seckillId))), SeckillProductVo.class);
    }

    /**
     * 库存回补
     * @param time
     * @param seckillId
     */
    @Override
    public void syncStockToRedis(Integer time, Long seckillId) {
        // 查询数据库真正的剩余库存
        SeckillProduct seckillProduct = seckillProductMapper.findDetails(seckillId);
        int stockCount = seckillProduct.getStockCount();
        if (stockCount > 0) { // 还有商品没卖完才需要进行库存回补
            // 更新redis中的库存进行，继续放进来请求
            String key = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
            redisTemplate.opsForHash().put(key, String.valueOf(seckillId), String.valueOf(stockCount));
        }
    }

    /**
     * 更新数据库中的库存
     * @param seckillId
     */
    @Override
    public void incrStockCount(Long seckillId) {
        seckillProductMapper.incrStock(seckillId);
    }
}
