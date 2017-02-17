OPEN SCHEMA kafka;
CREATE OR REPLACE JAVA SCALAR SCRIPT KAFKA_CONSUME_HIGHLEVEL (ZOOKEEPER_ADDR varchar(1000), CONSUMER_GROUP_ID varchar(100), TOPIC varchar(100), NUM_THREADS int) EMITS (val varchar(10000)) AS 
%jar kafka_consumer.jar.jar;
%jvmoption -Xms128m -Xmx128m;
/

CREATE OR REPLACE JAVA SCALAR SCRIPT KAFKA_CONSUME 
(BROKER_ADDR varchar(1000), BROKER_PORT int, TOPIC varchar(100), PARTITION_ID INT) EMITS (offset decimal(36,0), val varchar(10000)) AS 
%jar kafka_consumer.jar.jar;
%jvmoption -Xms128m -Xmx128m;

/

SELECT partition, KAFKA_CONSUME('10.44.2.98', 9092, 'logfile', partition) FROM (VALUES (0)) as T(partition);
select * from (values (1), (2)) as T(part);
-- Each Partition is consumed by exactly one consumer
SELECT KAFKA_CONSUME_HIGHLEVEL('10.44.2.98:2181', '1', 'logfile', 1);
