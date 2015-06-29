package com.flipkart.relayer.dummy;

import lombok.Data;

/**
 * Created by saurabh.agrawal on 23/06/15.
 */
@Data
public class GeneratorConfiguration {
    public String redisHost = "localhost";

    public Integer redisPort = 6380;

    public String redisListKey = "bigfoot_main";

    public Integer qps = 5000;

    public String queueName = "temp_queue";

    public Integer payloadSizeBytes = 1000;

}
