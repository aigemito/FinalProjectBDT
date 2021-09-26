package apiClient.kafkaproducer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class KProducer {
	
	KafkaProducer<String, String> kafkaMarketPriceProducer;

	public KProducer(){
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		
		kafkaMarketPriceProducer = new KafkaProducer<String, String>(properties);
	}
	
	public KafkaProducer<String, String> getKafkaMarketPriceProducer(){
		return kafkaMarketPriceProducer;
	}
	
}
