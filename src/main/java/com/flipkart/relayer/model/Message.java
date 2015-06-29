package com.flipkart.relayer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Created by saurabh.agrawal on 26/06/15.
 */
@Data
@AllArgsConstructor
public class Message {

    private String messageId;

    private String groupId;

    private String httpUri;

    private String httpMethod;

    private String exchangeName;

    private String messageBody;

    private Date createdAt;

    private ExchangeType exchangeType;
}
