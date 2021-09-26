package spark.kafkaconsumer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kafka.serializer.StringDecoder;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Runner
{
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception
	{
		SparkConf conf = new SparkConf()
				.setAppName("Spark Stream")
				.set("spark.dynamicAllocation.enabled", "false")
				.setMaster("local");
		
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.seconds(10));
		
		Map<String, String> kafkaParams = new HashMap<String, String>();
		
		kafkaParams.put("bootstrap.servers", "localhost:9092");
		kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "StringDeserializer");
		kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "StringDeserializer");
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		
		Set<String> topics = Collections.singleton("bitcoin");
	
				JavaPairInputDStream<String, String> stream = 
						KafkaUtils.createDirectStream(ssc, String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
		
		HiveContext sqlContext = new HiveContext(ssc.sparkContext());
		sqlContext.sql("CREATE TABLE IF NOT EXISTS bitcoin (id STRING, symbol STRING, name STRING, image STRING, current_price BIGINT, market_cap BIGINT ,market_cap_rank BIGINT, total_volume BIGINT, high_24h BIGINT, low_24h BIGINT, price_change_24h DOUBLE, price_change_percentage_24h DOUBLE, market_cap_change_24h BIGINT, market_cap_change_24h_percentage DOUBLE, circulating_supply BIGINT, total_supply BIGINT, ath BIGINT, ath_change_percentage DOUBLE, ath_date STRING, last_updated STRING, price_change_percentage_1h_in_currency DOUBLE)");
		
		stream.foreachRDD(rdd -> {
			
				JavaRDD<CoinMarkets> coinMarketsRDD = rdd.map(record -> new Gson().fromJson(record._2, CoinMarkets.class));
			
				DataFrame schemaCoinMarkets = sqlContext.createDataFrame(coinMarketsRDD, CoinMarkets.class);
				schemaCoinMarkets.registerTempTable("coinMarketsData");
				
				sqlContext.sql("INSERT OVERWRITE TABLE bitcoin SELECT id, symbol, name, image, currentPrice, marketCap, marketCapRank, totalVolume, high24h, low24h, priceChange24h, priceChange24h, priceChangePercentage24h, marketCapChange24h, marketCapChangePercentage24h, circulatingSupply, totalSupply, ath, athChangePercentage, athDate, lastUpdated, priceChangePercentage1hInCurrency FROM coinMarketsData");
			});
		
		//lines.print();
		ssc.start();
		ssc.awaitTermination();
	}
}

