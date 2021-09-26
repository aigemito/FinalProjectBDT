
How to set the environment and run the project documenation


STEP 1: INSTALL KAFKA

Commands:
download the tar file from http://archive.cloudera.com/kafka/redhat/6/x86_64/kafka/ 
then unzip using command
tar -zvxf ./kafka_2.12.1.1.0.tgz
sudo service kafka-server start

-- Start zookeeper if not already running

sudo service zookeeper-server start

-- Go to Kafka location

cd /usr/lib/kafka/bin

-- Start Kafka topic

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic_name

-- List topics. You should be able to see the topic name when you run this command.

./kafka-topics.sh --list --zookeeper localhost:2181

-- Start Producer
./kafka-console-producer.sh --broker-list localhost:9092 --topic topic_name

-- Start consumer
./kafka-console-consumer.sh --zookeper localhost:2181 -topic topic_name --from-beginning

-- Type something in your producer console, if you receive the same text in the consumer console then your kafka server is running properly. 


STEP 2: START ALL THE REQUIRED SERVICES
-- Start hive:
> sudo service hive-metastore start
> sudo service hive-server2 start

--Start zookeeper: 
> sudo service zookeeper-server start

--Start kafka (First you need to install kafka to be able to start this. See the next guide.)
> sudo service kafka-server start

STEP 3: CREATE A KAFKA TOPIC called bitcoin
-- Creating kafka topic
> cd /usr/lib/kafka/bin
> ./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic bitcoin

-- List kafka topics to check you can see the topic you created:
> cd /usr/lib/kafka/bin
> ./kafka-topics.sh --list --zookeeper localhost:2181

STEP 4: START THE GIVEN kafkaproducer.jar 
> java -jar 'kafkaproducer.jar'

STEP 5: SPARK-SUBMIT THE GIVEN kafkaConsumer-0.0.1-SNAPSHOT.jar
 > spark-submit --class "spark.kafkaconsumer.Runner" --master yarn  'Desktop/kafkaConsumer-0.0.1-SNAPSHOT.jar'
