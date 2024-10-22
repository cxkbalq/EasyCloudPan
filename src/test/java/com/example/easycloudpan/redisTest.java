//package com.example.easycloudpan;
//
//import lombok.var;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@SpringBootTest
//
//public class redisTest {
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    /**
//     * 测试设置键值
//     */
//    @Test
//    void testSetKey() {
//        // 向 Redis 设置键值对
//        redisTemplate.opsForValue().set("testKey", "testValue");
//        // 验证键是否成功设置
//        boolean keyExists = redisTemplate.hasKey("testKey");
//        System.out.println("键 'testKey' 是否存在: " + keyExists);
//
//        // 验证键的值是否正确
//        String value = (String) redisTemplate.opsForValue().get("testKey");
//        System.out.println("键 'testKey' 的值: " + value);
//    }
//
//    /**
//     * 测试通过模式匹配获取键
//     */
//    @Test
//    void testGetKeysByPattern() {
//        // 设置两个键值对用于模式匹配测试
//        redisTemplate.opsForValue().set("patternKey1", "value1");
//        redisTemplate.opsForValue().set("patternKey2", "value2");
//
//        // 获取所有符合模式 "patternKey*" 的键
//        var keys = redisTemplate.keys("patternKey*");
//        // 输出获取的键
//        System.out.println("匹配模式 'patternKey*' 的键: " + keys);
//    }
//
//    /**
//     * 测试删除键
//     */
//    @Test
//    void testDeleteKey() {
//        // 设置一个键值对用于测试删除操作
//        redisTemplate.opsForValue().set("keyToDelete", "value");
//        // 删除键
//        redisTemplate.delete("keyToDelete");
//        // 验证键是否被成功删除
//        boolean keyExists = redisTemplate.hasKey("keyToDelete");
//        System.out.println("键 'keyToDelete' 删除后是否存在: " + keyExists);
//    }
//
//    /**
//     * 测试获取旧值并设置新值
//     */
//    @Test
//    void testGetAndSet() {
//        // 设置初始值
//        redisTemplate.opsForValue().set("stringKey", "oldValue");
//        // 获取旧值并设置新值
//        String oldValue = (String) redisTemplate.opsForValue().getAndSet("stringKey", "newValue");
//
//        // 输出旧值和新值
//        System.out.println("键 'stringKey' 的旧值: " + oldValue);
//        System.out.println("键 'stringKey' 的新值: " + redisTemplate.opsForValue().get("stringKey"));
//    }
//
//
//
//
//    //操作字符串
//    //操作字符串
//    //操作字符串
//
//
//
//
//
//    /**
//     * 测试获取字符串值的长度
//     */
//    @Test
//    void testValueLength() {
//        // 设置字符串键值
//        redisTemplate.opsForValue().set("lengthKey", "12345");
//        // 获取字符串的长度
//        Long length = redisTemplate.opsForValue().size("lengthKey");
//
//        // 输出长度
//        System.out.println("键 'lengthKey' 的长度: " + length);
//    }
//
//    /**
//     * 测试向字符串末尾追加值
//     */
//    @Test
//    void testAppendToValue() {
//        // 设置初始字符串
//        redisTemplate.opsForValue().set("appendKey", "hello");
//        // 在字符串末尾追加内容
//        redisTemplate.opsForValue().append("appendKey", " world");
//
//        // 输出最终的字符串值
//        System.out.println("键 'appendKey' 的最终值: " + redisTemplate.opsForValue().get("appendKey"));
//    }
//
//
//
//    /**
//     * 测试向列表中推入元素
//     */
//    @Test
//    void testPushToList() {
//        // 向列表中推入多个元素
//        redisTemplate.opsForList().rightPushAll("listKey", "elem1", "elem2", "elem3");
//
//        // 输出列表中元素的个数
//        Long size = redisTemplate.opsForList().size("listKey");
//        System.out.println("列表 'listKey' 中的元素个数: " + size);
//    }
//
//    /**
//     * 测试获取列表指定范围内的元素
//     */
//    @Test
//    void testGetListRange() {
//        // 向列表中推入多个元素
//        redisTemplate.opsForList().rightPushAll("rangeListKey", "a", "b", "c", "d");
//
//        // 获取列表指定范围内的元素
//        var list = redisTemplate.opsForList().range("rangeListKey", 1, 3);
//        // 输出获取的元素
//        System.out.println("列表 'rangeListKey' 从索引 1 到 3 的元素: " + list);
//    }
//
//    /**
//     * 测试移除列表最左端的元素
//     */
//    @Test
//    void testPopLeft() {
//        // 向列表中推入两个元素
//        redisTemplate.opsForList().rightPushAll("popKey", "first", "second");
//        // 移除并获取列表最左端的元素
//        Object popped = redisTemplate.opsForList().leftPop("popKey");
//
//        // 输出移除的元素和剩余的元素数量
//        System.out.println("从列表 'popKey' 移除的元素: " + popped);
//        Long remainingSize = redisTemplate.opsForList().size("popKey");
//        System.out.println("列表 'popKey' 剩余的元素个数: " + remainingSize);
//    }
//
//    /**
//     * 测试移除列表中指定的元素
//     */
//    @Test
//    void testRemoveListElement() {
//        // 向列表中推入多个相同和不同的元素
//        redisTemplate.opsForList().rightPushAll("removeListKey", "elem", "elem", "elem2");
//
//        // 移除列表中第一个 "elem" 元素
//        redisTemplate.opsForList().remove("removeListKey", 1, "elem");
//
//        // 输出剩余的元素
//        var remainingList = redisTemplate.opsForList().range("removeListKey", 0, -1);
//        System.out.println("列表 'removeListKey' 剩余的元素: " + remainingList);
//    }
//}
