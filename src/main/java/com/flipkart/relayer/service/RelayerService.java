package com.flipkart.relayer.service;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.httpclient.InstrumentedHttpClientConnectionManager;
import com.codahale.metrics.httpclient.InstrumentedHttpRequestExecutor;
import com.flipkart.relayer.model.ExchangeType;
import com.flipkart.relayer.model.Message;
import com.flipkart.relayer.reader.RelayerCallback;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.AbstractIdleService;
import lombok.NonNull;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.httpclient.HttpClientMetricNameStrategies.METHOD_ONLY;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public class RelayerService extends AbstractIdleService {

    private final String restbusUrl;

    private final FutureRequestExecutionService futureRequestExecutionService;

    private final ResponseHandler<Boolean> restbusResponseHandler;

    private final MetricRegistry metrics;

    private final ExecutorService executorService;

    public RelayerService(String restbusUrl, int maxRestbusConnections,
                          ExecutorService executorService, MetricRegistry metrics) {
        this.restbusUrl = restbusUrl;
        this.executorService = executorService;
        this.metrics = metrics;

        final InstrumentedHttpClientConnectionManager connectionManager =
                new InstrumentedHttpClientConnectionManager(this.metrics);
        connectionManager.setMaxTotal(maxRestbusConnections);
        connectionManager.setDefaultMaxPerRoute(maxRestbusConnections);

        HttpClient httpClient = HttpClientBuilder.create()
                .setRequestExecutor(new InstrumentedHttpRequestExecutor(this.metrics, METHOD_ONLY))
                .setConnectionManager(connectionManager).build();

        this.futureRequestExecutionService =
                new FutureRequestExecutionService(httpClient, executorService);

        this.restbusResponseHandler = new ResponseHandler<Boolean>() {
            @Override
            public Boolean handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return response.getStatusLine().getStatusCode() == 200;
            }
        };

//        ConsoleReporter reporter = ConsoleReporter.forRegistry(this.metrics)
//                .convertRatesTo(TimeUnit.SECONDS)
//                .convertDurationsTo(TimeUnit.MILLISECONDS)
//                .build();
//        reporter.start(1, TimeUnit.SECONDS);
    }

    public void relay(@NonNull final Message message, final RelayerCallback relayerCallback) {

        HttpUriRequest restbusRequest = createRequest(message);

        futureRequestExecutionService.execute(
                restbusRequest,
                HttpClientContext.create(),
                restbusResponseHandler,
                relayerCallback);
    }

    private HttpUriRequest createRequest(final Message message) {
        String mqUrl = Joiner.on('/').join(restbusUrl,
                message.getExchangeType() == ExchangeType.QUEUE ? "queues" : "topics",
                message.getExchangeName(),
                "messages");

        String groupId = message.getGroupId();
        if (Strings.isNullOrEmpty((groupId)))
            groupId = message.getMessageId();

        HttpPost postRequest = new HttpPost(mqUrl);
        postRequest.addHeader("X_RESTBUS_MESSAGE_ID", message.getMessageId());
        postRequest.addHeader("X_RESTBUS_GROUP_ID", groupId);

        if (message.getExchangeType() == ExchangeType.QUEUE) {
            postRequest.addHeader("X_RESTBUS_HTTP_URI", message.getHttpUri());
            postRequest.addHeader("X_RESTBUS_HTTP_METHOD", message.getHttpMethod());
        }

        try {
            postRequest.setEntity(new StringEntity(message.getMessageBody()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return postRequest;
    }

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void shutDown() throws Exception {
        shutdownAndAwaitTermination(executorService);
        futureRequestExecutionService.close();
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
