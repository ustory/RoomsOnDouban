package me.zxx.redis;

import redis.clients.jedis.Jedis;

public class RedisClient {
    private static String host = "127.0.0.1";
    private static Jedis jedis = new Jedis(host);

    public static String get(String key) {
        return jedis.get(key);
    }

    public static void set(String key, String value) {
        jedis.set(key, value);
    }
}
