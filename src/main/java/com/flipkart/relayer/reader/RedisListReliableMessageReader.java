package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.ExchangeType;
import com.flipkart.relayer.model.Message;
import com.flipkart.relayer.utils.MessageUtils;
import com.google.common.base.Strings;
import lombok.NonNull;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public class RedisListReliableMessageReader extends AbstractRedisMessageReader {

    public RedisListReliableMessageReader(@NonNull String host, int port, String redisKey, ExchangeType exchangeType) {
        super(host, port, redisKey, exchangeType);
    }

    private static String getProcessingListKey(String mainListKey) {
        return mainListKey.concat("_processing");
    }

    @Override
    public Message next() throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            Message message = null;
            final String reply = jedis.brpoplpush(redisKey, getProcessingListKey(redisKey), 0);
            if (!Strings.isNullOrEmpty(reply)) {
                String messageId = MessageUtils.getMessageId(reply);
                Date createdAt = MessageUtils.getMessageCreatedAt(reply);
                message = loadMessage(messageId, createdAt, jedis);
            }
            return message;
        }
    }

    @Override
    public RelayerCallback createCallbackHandler(Message message) {
        return new RelayerCallback(redisKey, message) {
            @Override
            protected void relaySuccess() {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.lrem(getProcessingListKey(key), 0,
                            MessageUtils.getMessageKey(message.getMessageId(), message.getCreatedAt()));
                    deleteOBMessage(message.getMessageId(), jedis);
                }
            }

            @Override
            protected void relayFailure() {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.rpush(key, MessageUtils.getMessageKey(message.getMessageId(), message.getCreatedAt()));
                }
            }

            @Override
            protected void relayException(Throwable t) {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.rpush(key, MessageUtils.getMessageKey(message.getMessageId(), message.getCreatedAt()));
                }
            }
        };
    }

}
