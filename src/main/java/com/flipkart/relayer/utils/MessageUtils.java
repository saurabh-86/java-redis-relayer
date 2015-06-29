package com.flipkart.relayer.utils;

import java.util.Date;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by saurabh.agrawal on 23/06/15.
 */
public class MessageUtils {
    private static final String OUTBOUND = "outbound";

    public static String getOBMessageKey(String messageId) {
        return OUTBOUND.concat(".").concat(messageId);
    }

    public static String createMessage(String messageId) {
        return messageId.concat("$").concat(String.valueOf(new Date().getTime()));
    }

    public static String getMessageId(String message) {
        return newArrayList(on("$").split(message)).get(0);
    }

    public static Date getMessageCreatedAt(String message) {
        return new Date(Long.parseLong(newArrayList(on("$").split(message)).get(1)));
    }

    public static String getMessageKey(String messageId, Date createdAt) {
        return messageId.concat("$").concat(String.valueOf(createdAt.getTime()));
    }
}
