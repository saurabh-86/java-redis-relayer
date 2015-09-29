package com.flipkart.relayer.service;

import com.flipkart.relayer.model.Message;
import com.flipkart.relayer.reader.MessageReader;
import com.flipkart.relayer.reader.RelayerCallback;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public class MessageListProcessor extends AbstractExecutionThreadService {
    private final MessageReader reader;

    private final RelayerService relayerService;

    private static final Logger logger = LoggerFactory.getLogger(MessageListProcessor.class);

    public MessageListProcessor(MessageReader reader, RelayerService relayerService) {
        this.reader = reader;
        this.relayerService = relayerService;
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            final Message message = reader.next();
            if (message != null) {
                final RelayerCallback relayerCallback = reader.createCallbackHandler(message);
                try {
                    logger.info("Relaying message {}", message.getMessageId());
                    relayerService.relay(message, relayerCallback);
                } catch (RejectedExecutionException ex) {
                    logger.error("Dropping message. " + ex.getMessage());
                }
            } else {
                logger.debug("No message found");
            }
        }
    }

    @Override
    protected void triggerShutdown() {
        Thread.currentThread().interrupt();
    }

}
