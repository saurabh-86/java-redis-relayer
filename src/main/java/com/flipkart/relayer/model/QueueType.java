package com.flipkart.relayer.model;

/**
 * Created by saurabh.agrawal on 27/06/15.
 */

/**
 * The strategy to use when a new message relay request arrives and
 * all the relayer threads are busy, and no more threads can be created.
 */
public enum QueueType {
    // The relay request is added to a queue. Once the queue size
    // reaches it's configured maximum, all subsequent relay requests
    // are rejected until the queue frees up.
    QUEUE_THEN_ABORT,

    // The sender thread is used to relay the message. This effectively
    // throttles the sender.
    THROTTLE
}
