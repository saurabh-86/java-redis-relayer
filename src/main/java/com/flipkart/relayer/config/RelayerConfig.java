package com.flipkart.relayer.config;

import com.flipkart.relayer.model.QueueType;
import lombok.Data;

import java.util.List;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
@Data
public class RelayerConfig {

    // Restbus URL
    public String restbusUrl;

    // List of source configs, each defining a source of unrelayed messages
    public List<RedisSourceConfig> sources;

    // Maximum number of HTTP connections the relayer could make to Restbus
    public int maxRestbusConnections;

    // The queueing strategy / mechanism to use when all relaying
    // threads are busy and a new message arrives for relay.
    public QueueType relayerQueueType;

    // The maximum length of queue which holds to-be-relayed messages.
    // Until the queue size is equal to relayerQueueSize, new messages
    // sent to relay will fail immediately. This is applicable only if
    // relayerQueueType is QUEUE_THEN_ABORT.
    public int relayerQueueSize;
}
