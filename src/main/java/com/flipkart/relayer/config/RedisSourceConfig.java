package com.flipkart.relayer.config;

import com.flipkart.relayer.model.ExchangeType;
import lombok.Data;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
@Data
public class RedisSourceConfig {

    // The redis-server host
    public String host;

    // The redis-server port
    public int port;

    // The list key holding unrelayed message ids
    public String key;

    // The number of threads polling for unrelayed messages
    public long numThreads;

    // If false, messages are tried for relay at most once.
    // If true, failed messages are retried. ~15-20% lower throughput.
    public boolean reliable;

    // Queue or Topic - The exchange type where messages from this sources are posted to
    public ExchangeType exchangeType;

}
