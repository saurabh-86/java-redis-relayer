package com.flipkart.relayer.executor;

import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by saurabh.agrawal on 30/06/15.
 */
class QueueAbortRelayExecutor extends RelayExecutor {

     QueueAbortRelayExecutor(int maxRestbusConnections, int relayerQueueSize, MetricRegistry metrics) {
        super(new ThreadPoolExecutor(0, maxRestbusConnections,
                    60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(relayerQueueSize),
                    new InstrumentedRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy(), metrics)),
                metrics);
    }
}
