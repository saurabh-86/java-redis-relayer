package com.flipkart.relayer;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.flipkart.relayer.config.RelayerConfig;
import com.flipkart.relayer.executor.RelayExecutorFactory;
import com.flipkart.relayer.service.MessageListProcessorFactory;
import com.flipkart.relayer.service.RelayerService;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
public class Application {

    private static final String DEFAULT_CONFIG_FILE = "relayer.yml";

    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws FileNotFoundException {

        String configFilePath = DEFAULT_CONFIG_FILE;
        if (args.length > 0)
            configFilePath = args[0];

        InputStream input = new FileInputStream(new File(configFilePath));

        Yaml yaml = new Yaml(new Constructor(RelayerConfig.class));
        RelayerConfig relayerConfig = (RelayerConfig) yaml.load(input);

        ExecutorService relayerExecutor = RelayExecutorFactory.build(relayerConfig.getMaxRestbusConnections(),
                relayerConfig.getRelayerQueueType(), relayerConfig.getRelayerQueueSize(), metrics);

        RelayerService relayerService = new RelayerService(
                relayerConfig.getRestbusUrl(), relayerConfig.getMaxRestbusConnections(), relayerExecutor, metrics);

        Set<Service> services = Sets.newHashSet();
        services.add(relayerService);
        services.addAll(MessageListProcessorFactory.build(relayerConfig.getSources(), relayerService));

        final ServiceManager manager = new ServiceManager(services);
        manager.startAsync();

//        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
//                .convertRatesTo(TimeUnit.SECONDS)
//                .convertDurationsTo(TimeUnit.MILLISECONDS)
//                .build();
//        reporter.start(1, TimeUnit.SECONDS);

        final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
        reporter.start();

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                manager.stopAsync();
                System.out.println("Waiting for services to stop...");
                try {
                    manager.awaitStopped(10, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                System.out.println("Done!");
            }
        });
    }
}
