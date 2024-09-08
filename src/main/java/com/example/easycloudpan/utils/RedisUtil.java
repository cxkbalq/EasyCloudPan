//package com.example.easycloudpan.utils;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//@Component
////这是redis工具，已对雪崩
//public class RedisUtil<T> {
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    //生成随机的时间，防止雪崩
//    public Integer randomTime(){
//        Random random = new Random();
//        // 生成 60 到 100 之间的随机整数
//        int min = 60;
//        int max = 100;
//        int randomtime = random.nextInt(max - min + 1) + min;
//        return randomtime;
//    }
//
//    //设置键值
//    public void redisSetKeyValue(String key,T data){
//        redisTemplate.opsForValue().set(key,data,randomTime(), TimeUnit.MINUTES);
//    }
//
//    //获取键值
//    public T redisGetKeyValue(String key){
//        return (T) redisTemplate.opsForValue().get(key);
//    }
//
//}
