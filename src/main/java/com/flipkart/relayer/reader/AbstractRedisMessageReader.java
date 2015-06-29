package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.ExchangeType;
import com.flipkart.relayer.model.Message;
import com.flipkart.relayer.utils.MessageUtils;
import lombok.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public abstract class AbstractRedisMessageReader implements MessageReader {

    protected final JedisPool jedisPool;

    protected final String redisKey;

    protected final ExchangeType exchangeType;

    public AbstractRedisMessageReader(@NonNull String host, int port, String redisKey, ExchangeType exchangeType) {
        this.redisKey = redisKey;
        this.exchangeType = exchangeType;
        this.jedisPool = new JedisPool(host, port);
    }

    protected Message loadMessage(String messageId, Date createdAt, Jedis jedis) {
        Map<String, String> m = jedis.hgetAll(MessageUtils.getOBMessageKey(messageId));
        return new Message(messageId, m.get("group_id"), m.get("uri"), m.get("method"), m.get("exchange"), m.get("message"), createdAt, exchangeType);
    }

    protected void deleteOBMessage(String messageId, Jedis jedis) {
        jedis.del(MessageUtils.getOBMessageKey(messageId));
    }
}
