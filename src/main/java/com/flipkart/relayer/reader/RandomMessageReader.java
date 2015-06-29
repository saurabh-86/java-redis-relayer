package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.ExchangeType;
import com.flipkart.relayer.model.Message;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.UUID;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public class RandomMessageReader implements MessageReader {
    @Override
    public Message next() throws Exception {
        String messageId = UUID.randomUUID().toString();
        String exchange = "temp_queue";
        String uri = "http://dummy-url.nm.flipkart.com";
        String method = "post";
        String body = RandomStringUtils.randomAscii(1000);

        return new Message(messageId, null, uri, method, exchange, body, new Date(), ExchangeType.QUEUE);
    }

    @Override
    public RelayerCallback createCallbackHandler(Message message) {
        return null;
    }
}