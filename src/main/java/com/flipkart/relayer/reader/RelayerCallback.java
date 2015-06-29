package com.flipkart.relayer.reader;

import com.flipkart.relayer.model.Message;
import lombok.AllArgsConstructor;
import org.apache.http.concurrent.FutureCallback;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */
@AllArgsConstructor
public abstract class RelayerCallback implements FutureCallback<Boolean> {
    protected final String key;

    protected final Message message;

    protected abstract void relaySuccess();

    protected abstract void relayFailure();

    protected abstract void relayException(Throwable t);

    @Override
    public void completed(Boolean result) {
        if (result) {
            relaySuccess();
        } else {
            relayFailure();
        }
    }

    @Override
    public void failed(Exception ex) {
        System.err.println("Relayer threw exception " + ex.getMessage());
        relayException(ex);
    }

    @Override
    public void cancelled() {

    }
}
