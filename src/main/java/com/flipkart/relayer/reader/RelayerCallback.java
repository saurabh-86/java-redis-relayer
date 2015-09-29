package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.Message;
import lombok.AllArgsConstructor;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
@AllArgsConstructor
public abstract class RelayerCallback implements FutureCallback<Boolean> {

    protected static final Logger logger = LoggerFactory.getLogger(RelayerCallback.class);

    protected final String key;

    protected final Message message;

    protected abstract void relaySuccess();

    protected abstract void relayFailure();

    protected abstract void relayException(Throwable t);

    @Override
    public void completed(Boolean result) {
        logger.info("Relay of {} completed with result {}", message.getMessageId(), result);
        if (result) {
            relaySuccess();
        } else {
            relayFailure();
        }
    }

    @Override
    public void failed(Exception ex) {
        logger.error("Relayer threw exception " + ex.getMessage());
        relayException(ex);
    }

    @Override
    public void cancelled() {

    }
}
