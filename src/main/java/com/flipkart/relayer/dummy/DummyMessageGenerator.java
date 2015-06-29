package com.flipkart.relayer.dummy;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by saurabh.agrawal on 23/06/15.
 */
public class DummyMessageGenerator {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        Yaml yaml = new Yaml(new Constructor(GeneratorConfiguration.class));

        InputStream input = new FileInputStream(new File(args[0]));
        GeneratorConfiguration configuration = (GeneratorConfiguration) yaml.load(input);

        Integer desiredQps = configuration.qps;

        while (true) {
            long start = new Date().getTime();

            try (Jedis jedis = new Jedis(configuration.redisHost, configuration.redisPort)) {
                for (int i = 0; i < desiredQps; i++) {
                    postMessageToRedis(configuration, jedis);
                }
            }
            long end = new Date().getTime();

            long sleepTime = 1000 - (end - start);
            if (sleepTime > 0)
                Thread.sleep(sleepTime);
        }
    }

    public static void postMessageToRedis(GeneratorConfiguration configuration, Jedis jedis) {
        String messageId = UUID.randomUUID().toString();

        Map<String, String> messageDetails = Maps.newHashMap();
        messageDetails.put("exchange", configuration.queueName);
        messageDetails.put("uri", "http://dummy-url.nm.flipkart.com");
        messageDetails.put("method", "post");
        messageDetails.put("message", RandomStringUtils.randomAscii(configuration.payloadSizeBytes));

        String messageKey = messageId.concat("$").concat(String.valueOf(new Date().getTime()));

        jedis.hmset("outbound.".concat(messageId), messageDetails);
        jedis.lpush(configuration.redisListKey, messageKey);

    }


}
