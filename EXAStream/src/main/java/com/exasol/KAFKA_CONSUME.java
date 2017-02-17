package com.exasol;

import java.util.ArrayList;
import java.util.List;

public class KAFKA_CONSUME {
	static void run(ExaMetadata exa, ExaIterator ctx) throws Exception {
		
		ExaIteratorHolder.setIterator(ctx);
		
		String brokerAddr = ctx.getString("BROKER_ADDR");
        List<String> seeds = new ArrayList<String>();
        seeds.add(brokerAddr);
		int brokerPort = ctx.getInteger("BROKER_PORT");
		String topic = ctx.getString("TOPIC");
		int partition = ctx.getInteger("PARTITION_ID");
        long maxReads = 100;
		
        SimpleExample example = new SimpleExample();
        example.run(maxReads, topic, partition, seeds, brokerPort); // added start time
	}
}
