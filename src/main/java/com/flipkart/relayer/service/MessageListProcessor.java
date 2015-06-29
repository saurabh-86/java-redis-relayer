package com.flipkart.relayer.service;

import com.flipkart.relayer.model.Message;
import com.flipkart.relayer.reader.MessageReader;
import com.flipkart.relayer.reader.RelayerCallback;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.util.concurrent.RejectedExecutionException;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public class MessageListProcessor extends AbstractExecutionThreadService {
    private final MessageReader reader;

    private final RelayerService relayerService;

    public MessageListProcessor(MessageReader reader, RelayerService relayerService) {
        this.reader = reader;
        this.relayerService = relayerService;
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            final Message message = reader.next();
            final RelayerCallback relayerCallback = reader.createCallbackHandler(message);
            try {
                relayerService.relay(message, relayerCallback);
            } catch (RejectedExecutionException ex) {
                System.err.println("Dropping message. " + ex.getMessage());
            }
        }
    }
}
