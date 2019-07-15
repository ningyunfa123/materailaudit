package com.audit.materialaudit;

import com.alibaba.fastjson.JSON;
import com.audit.materialaudit.common.RedisHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MaterialauditApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheErrorHandler errorHandler;

    @Autowired
    private RedisHelper<String, String> redisHelper;
    @Test
    public void contextLoads() {
        String key = "redisTest";
        redisHelper.hashSet("redisTest","mytest", "1234");
        System.out.println(JSON.toJSONString(redisHelper.hashFindAll("redisTest")));
    }

}
