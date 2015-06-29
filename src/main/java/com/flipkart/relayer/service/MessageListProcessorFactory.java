package com.flipkart.relayer.service;

import com.flipkart.relayer.config.RedisSourceConfig;
import com.flipkart.relayer.reader.MessageReader;
import com.flipkart.relayer.reader.RedisListMessageReader;
import com.flipkart.relayer.reader.RedisListReliableMessageReader;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
public class MessageListProcessorFactory {

    public static Set<MessageListProcessor> build(List<RedisSourceConfig> redisSourceConfigs, RelayerService relayerService) {
        Set<MessageListProcessor> messageListProcessors = Sets.newHashSet();
        for (RedisSourceConfig redisSourceConfig : redisSourceConfigs) {
            for (int i = 0; i < redisSourceConfig.getNumThreads(); i++) {
                MessageReader reader = redisSourceConfig.isReliable() ?
                        new RedisListReliableMessageReader(redisSourceConfig.getHost(), redisSourceConfig.getPort(),
                                redisSourceConfig.getKey(), redisSourceConfig.getExchangeType()) :
                        new RedisListMessageReader(redisSourceConfig.getHost(), redisSourceConfig.getPort(),
                                redisSourceConfig.getKey(), redisSourceConfig.getExchangeType());

                messageListProcessors.add(new MessageListProcessor(reader, relayerService));
            }
        }
        return messageListProcessors;
    }
}
