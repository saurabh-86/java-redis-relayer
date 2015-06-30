package com.flipkart.relayer.executor;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by saurabh.agrawal on 30/06/15.
 */
public class RelayExecutor extends InstrumentedExecutorService {

    private static final String METRIC_PREFIX = "com.flipkart.relayer.executor.RelayExecutor";

    public RelayExecutor(final ThreadPoolExecutor delegate, MetricRegistry registry) {
        super(delegate, registry, METRIC_PREFIX);

        registry.register(name(METRIC_PREFIX, "workQueue", "size"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return delegate.getQueue().size();
            }
        });
    }

    static class InstrumentedRejectedExecutionHandler implements RejectedExecutionHandler{

        private final RejectedExecutionHandler delegate;

        private final Meter rejected;

        InstrumentedRejectedExecutionHandler(RejectedExecutionHandler delegate, MetricRegistry metricRegistry) {
            this.delegate = delegate;
            this.rejected = metricRegistry.meter(name(METRIC_PREFIX, "rejected"));
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            rejected.mark();
            delegate.rejectedExecution(r, executor);
        }
    }
}
