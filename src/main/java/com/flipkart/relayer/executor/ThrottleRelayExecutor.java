package com.flipkart.relayer.executor;

import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by saurabh.agrawal on 30/06/15.
 */
class ThrottleRelayExecutor extends RelayExecutor {

     ThrottleRelayExecutor(int maxRestbusConnections, MetricRegistry metrics) {
        super(new ThreadPoolExecutor(0, maxRestbusConnections,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    new InstrumentedRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy(), metrics)),
                metrics);
    }
}
