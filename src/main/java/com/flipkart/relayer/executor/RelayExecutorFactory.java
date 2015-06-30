package com.flipkart.relayer.executor;

import com.codahale.metrics.MetricRegistry;
import com.flipkart.relayer.model.QueueType;

import java.util.concurrent.ExecutorService;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
public class RelayExecutorFactory {

    public static ExecutorService build(int maxRestbusConnections, QueueType relayerQueueType,
                                        int relayerQueueSize, MetricRegistry metrics) {
        switch (relayerQueueType) {
            case QUEUE_THEN_ABORT:
                return new QueueAbortRelayExecutor(maxRestbusConnections, relayerQueueSize, metrics);
            case THROTTLE:
                return new ThrottleRelayExecutor(maxRestbusConnections, metrics);
        }
        return null;
    }
}
