# kafka-udf

build with:
mvn clean package assembly:single

## Current status

#### 2015-07-30 - Both consumers work, but...

There is a high-level consumer and a simple consumer (the "simple" one is simply designed, but not simple to use). Basically, the first one has the following additional features [6, see comment]:

- Auto/Hidden Offset Management
- Auto(Simple) Partition Assignment
- Broker Failover => Auto Rebalance
- Consumer Failover => Auto Rebalance

The high-level consumer opens and assigns a thread to each partition. As we want to start one udf for each partition in order to load in parallel, we need the simple consumer. But here, offset handling is tricky [see 7] - even more if we want to make sure that each message is inserted into DB EXACTLY ONCE. Currently, our simple consumer implementation always fetches the whole partition.

According to the Kafka release plan there is a new consumer API to be released within the next days...


## Overview

This is a consumer for Apache Kafka for EXASOL DB. It is written in Java and supposed to be executed as a UDF.

## Kafka

Kafka is an open-source message broker, originally developed by LinkedIn and today maintained by the Apache Foundation. 
It provides a unified, high-throughput, low-latency platform for handling real-time data feeds and is typically used 
for processing transaction logs. [1] Its distributed architecture is scalable and fault-tolerant. Kafka employs a cluster-centric design and integrates itself 
into the Hadoop ecosystem -- Zookeeper is used as cluster-coordination service. It can and is however also used standalone (with a small Zookeeper instance running), 
or in non-hadoop real-time architectures. [2, S. 41]

Typical use cases include [3, Section 1.2]
- message brokering (collecting, buffering, providing),
- real-time website activity tracking & monitoring,
- log aggregation,
- stream processing,
- commit logging for distributed systems (for replication of data between nodes & re-syncing mechanism) [see also 4].

It works as a *publish-subscribe model*: Topics (user defined message categories) are being defined, for which *producers* (Kafka input interface) 
send messages into Kafka, which stores them as an ordered sequence. Clients employ *consumers* (Kafka output interface) that identify themselves as belonging to a *consumer group* and 
subscribe to a *topic*. If all consumers belong to the same consumer group, then it works like standard queuing: each message is received by one consumer. If there are more consumer groups,
messages are being published to all consumer groups. [3; Section 1.1]. 

A standalone Kafka service can be set up as described in the official documentation [3, Section 1.3]. 
The procedure is

1. downloading Kafka,
2. starting Zookeeper,
3. starting the Kafka Server,
4. Creating a topic,
5. Setting up a message producer,
6. Setting up the message consumer.

## EXASOL Kafka Consumer UDF

The JAR file must be made available as a library for UDFs via EXAOperation. Then, the sql can be used from the kafka.sql file provided.

The high level consumer is based on the High Level Consumer Group Example laid out in [4]. The high level consumer is multi-threaded, with each consumer thread being able to receive messages from one *partition* (a topic is subdivided into partitions which are distributed to Kafka brokers i.e. nodes).
The simple consumer is based on the example given in [8]. It is single threaded and reads one kafka partition per call.

## References

[1] https://en.wikipedia.org/wiki/Apache_Kafka  
[2] iX Developer 2015 - Big Data  
[3] http://kafka.apache.org/documentation.html  
[4] http://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying  
[5] https://cwiki.apache.org/confluence/display/KAFKA/Consumer+Group+Example  
[6] https://cwiki.apache.org/confluence/display/KAFKA/Consumer+Client+Re-Design  
[7] http://cfchou.github.io/blog/2015/04/23/a-closer-look-at-kafka-offsetrequest/  
[8] https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+SimpleConsumer+Example  




