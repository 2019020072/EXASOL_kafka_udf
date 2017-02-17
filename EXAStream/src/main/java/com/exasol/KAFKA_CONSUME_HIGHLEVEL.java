package com.exasol;

public class KAFKA_CONSUME_HIGHLEVEL {
	static void run(ExaMetadata exa, ExaIterator ctx) throws Exception {
		
		ExaIteratorHolder.setIterator(ctx);
		
		String zookeeperAddr = ctx.getString("ZOOKEEPER_ADDR");
		String consumerGroupId = ctx.getString("CONSUMER_GROUP_ID");
		String topic = ctx.getString("TOPIC");
		int numThreads = ctx.getInteger("NUM_THREADS");
		
        ConsumerGroupExample example = new ConsumerGroupExample(zookeeperAddr, consumerGroupId, topic);
        example.run(numThreads);
 
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
 
        }
        example.shutdown();
	}
}
