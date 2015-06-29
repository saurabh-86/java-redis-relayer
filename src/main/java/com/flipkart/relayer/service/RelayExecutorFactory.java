package com.flipkart.relayer.service;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.MetricRegistry;
import com.flipkart.relayer.model.QueueType;

import java.util.concurrent.*;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
public class RelayExecutorFactory {

    public static ExecutorService build(int maxRestbusConnections, QueueType relayerQueueType, int relayerQueueSize, MetricRegistry metrics) {
        ExecutorService delegate = buildThreadPoolExecutor(maxRestbusConnections, relayerQueueType, relayerQueueSize);
        return new InstrumentedExecutorService(delegate, metrics);
    }

    private static ExecutorService buildThreadPoolExecutor(int maxRestbusConnections, QueueType relayerQueueType, int relayerQueueSize) {
        switch (relayerQueueType) {
            case QUEUE_THEN_ABORT:
                return new ThreadPoolExecutor(0, maxRestbusConnections,
                        60L, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(relayerQueueSize),
                        new ThreadPoolExecutor.AbortPolicy());
            case THROTTLE:
                return new ThreadPoolExecutor(0, maxRestbusConnections,
                        60L, TimeUnit.SECONDS,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());
            default:
                return null;
        }
    }
}
