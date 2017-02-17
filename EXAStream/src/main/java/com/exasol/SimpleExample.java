package com.exasol;
 
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
 
public class SimpleExample {
	
    public static void main(String args[]) { 					// main method:  starts instance, processes args, calls run method
    	
        SimpleExample example = new SimpleExample();
        long maxReads = 100; 			// Long.parseLong(args[0]);
        String topic = 	"logfile"	;		// args[1];
        int partition = 0	;		// Integer.parseInt(args[2]);
        List<String> seeds = new ArrayList<String>();
        seeds.add(	"10.44.2.98");				// args[3]);
        int port = Integer.parseInt("9092");			// args[4]);
        //long start_time = Long.parseLong("1438137822");		//args[5]); 
        try {
            example.run(maxReads, topic, partition, seeds, port); // , start_time added start time
        } catch (Exception e) {
            System.out.println("Oops:" + e);
             e.printStackTrace();
        }
    }
 
    private List<String> m_replicaBrokers = new ArrayList<String>();
 
    public SimpleExample() {								// class constructor
        m_replicaBrokers = new ArrayList<String>();
    }
 
    public void run(long a_maxReads, String a_topic, int a_partition, List<String> a_seedBrokers, int a_port) throws Exception { // , long start_time add long start_time
        // find the meta data about the topic and partition we are interested in
        //
        PartitionMetadata metadata = findLeader(a_seedBrokers, a_port, a_topic, a_partition); 									// get metadata
        if (metadata == null) {
        	throw new RuntimeException("Can't find metadata for Topic and Partition. Exiting");
        }
        if (metadata.leader() == null) {
        	throw new RuntimeException("Can't find Leader for Topic and Partition. Exiting");
        }
        String leadBroker = metadata.leader().host();
        String clientName = "Client_" + a_topic + "_" + a_partition;
 
        SimpleConsumer consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName); // start consumer
        long eat = kafka.api.OffsetRequest.EarliestTime();
//        if (start_time < eat) {
//        	start_time = eat;
//        }
        long readOffset = getLastOffset(consumer,a_topic, a_partition,kafka.api.OffsetRequest.EarliestTime(), clientName);	// changed from kafka.api.OffsetRequest.EarliestTime() to arg start_time	// define offset - EarliestTime() finds the beginning of the data in the logs
 
        int numErrors = 0;
        while (a_maxReads > 0) { 												// do this as long as we still want more data
            if (consumer == null) {
                consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
            }
            FetchRequest req = new FetchRequestBuilder()						// formulate the request
                    .clientId(clientName)
                    .addFetch(a_topic, a_partition, readOffset, 100000) // Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
                    .build();
            FetchResponse fetchResponse = consumer.fetch(req);					// execute request & get response
 
            if (fetchResponse.hasError()) {				// request error handling
                numErrors++;
                // Something went wrong!
                short code = fetchResponse.errorCode(a_topic, a_partition);
                System.out.println("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
                if (numErrors > 5) break;
                if (code == ErrorMapping.OffsetOutOfRangeCode())  {
                    // We asked for an invalid offset. For simple case ask for the last element to reset
                    readOffset = getLastOffset(consumer,a_topic, a_partition, kafka.api.OffsetRequest.LatestTime(), clientName);
                    continue;
                }
                consumer.close();
                consumer = null;
                leadBroker = findNewLeader(leadBroker, a_topic, a_partition, a_port);
                continue;
            }
            numErrors = 0;
 
            long numRead = 0;
            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(a_topic, a_partition)) { // iterate through all messages received
                long currentOffset = messageAndOffset.offset();
                if (currentOffset < readOffset) {									// if the current set contains msgs. that are before the wanted offset...
                    System.out.println("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
                    continue;
                }
                readOffset = messageAndOffset.nextOffset(); 			// set the read offset to the one after the current msg 
                ByteBuffer payload = messageAndOffset.message().payload(); // msg. gets buffered before conversion to string
 
                byte[] bytes = new byte[payload.limit()];
                payload.get(bytes);
                if (ExaIteratorHolder.getIterator() != null) {
    				ExaIteratorHolder.getIterator().emit(messageAndOffset.offset(), new String(bytes, "UTF-8"));
                }
                System.out.println(String.valueOf(messageAndOffset.offset()) + ": " + new String(bytes, "UTF-8")); // here it prints out the msgs
                numRead++;
                a_maxReads--;
            }
 
            if (numRead == 0) {
            	// We read all messages so far.
            	// Don't wait for new messages coming in
            	break;
//            	// Wait a second and see if new messages are appended to our partition
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ie) {
//                }
            }
        } // end of while there are messages coming
        if (consumer != null) consumer.close();
    }
 
    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition,
                                     long whichTime, String clientName) { // get the last offset before "whichTime" which can be a unix timestamp, earliestTime(), latestTime() 
    	
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1)); // PartitionOffsetRequestInfo supplies a query criterion for the corresponding partition. time can be a unix timestamp or either one of the two special constants earliest/latestTime.
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);
 
        if (response.hasError()) {
            System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition) );
            return 0;
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }
 
    private String findNewLeader(String a_oldLeader, String a_topic, int a_partition, int a_port) throws Exception {
        for (int i = 0; i < 3; i++) {
            boolean goToSleep = false;
            PartitionMetadata metadata = findLeader(m_replicaBrokers, a_port, a_topic, a_partition);
            if (metadata == null) {
                goToSleep = true;
            } else if (metadata.leader() == null) {
                goToSleep = true;
            } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                // first time through if the leader hasn't changed give ZooKeeper a second to recover
                // second time, assume the broker did recover before failover, or it was a non-Broker issue
                //
                goToSleep = true;
            } else {
                return metadata.leader().host();
            }
            if (goToSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        System.out.println("Unable to find new leader after Broker failure. Exiting");
        throw new Exception("Unable to find new leader after Broker failure. Exiting");
    }
 
    private PartitionMetadata findLeader(List<String> a_seedBrokers, int a_port, String a_topic, int a_partition) {
        PartitionMetadata returnMetaData = null;
        loop:
        for (String seed : a_seedBrokers) {
            SimpleConsumer consumer = null;
//            try {
                consumer = new SimpleConsumer(seed, a_port, 100000, 64 * 1024, "leaderLookup");
                List<String> topics = Collections.singletonList(a_topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
 
                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        if (part.partitionId() == a_partition) {
                            returnMetaData = part;
                            break loop;
                        }
                    }
                }
//            } catch (Exception e) {
//                System.out.println("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic
//                        + ", " + a_partition + "] Reason: " + e);
//            } finally {
                if (consumer != null) consumer.close();
//            }
        }
        if (returnMetaData != null) {
            m_replicaBrokers.clear();
            for (kafka.cluster.Broker replica : returnMetaData.replicas()) {
                m_replicaBrokers.add(replica.host());
            }
        }
        return returnMetaData;
    }
}