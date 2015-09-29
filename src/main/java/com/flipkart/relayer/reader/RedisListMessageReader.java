package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.ExchangeType;
import com.flipkart.relayer.model.Message;
import com.flipkart.relayer.utils.MessageUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public class RedisListMessageReader extends AbstractRedisMessageReader {

    private static final Logger logger = LoggerFactory.getLogger(RedisListMessageReader.class);

    public RedisListMessageReader(@NonNull String host, int port, String redisKey, ExchangeType exchangeType) {
        super(host, port, redisKey, exchangeType);
    }

    @Override
    public Message next() throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            Message message = null;
            final List<String> brpop = jedis.brpop(1, redisKey);

            if (Thread.interrupted()) {
                logger.error("Thread interrupted");
                throw new InterruptedException();
            }

            if (!brpop.isEmpty()) {
                String value = brpop.get(1);
                logger.debug("Raw redis value {} popped from {}", value, redisKey);
                String messageId = MessageUtils.getMessageId(value);
                Date createdAt = MessageUtils.getMessageCreatedAt(value);
                message = loadMessage(messageId, createdAt, jedis);
                deleteOBMessage(messageId, jedis);
            }
            return message;
        }
    }

    @Override
    public RelayerCallback createCallbackHandler(Message message) {
        return null;
    }

}
