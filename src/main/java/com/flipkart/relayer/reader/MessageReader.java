package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.Message;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
public interface MessageReader {
    Message next() throws Exception;

    RelayerCallback createCallbackHandler(Message message);
}
