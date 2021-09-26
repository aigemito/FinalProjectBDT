package apiClient.kafkaproducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.gson.Gson;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.CoinMarkets;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

public class Runner 
{
    public static void main( String[] args )
    {	
    	KProducer kProducer = new KProducer();
        
        final KafkaProducer<String, String> producer = kProducer.getKafkaMarketPriceProducer();
    	final CoinGeckoApiClient apiClient = new CoinGeckoApiClientImpl();
    	final List<CoinMarkets> markets = new ArrayList<CoinMarkets>();
    	
    	//Call api at a certain timer
        new Timer().schedule(new TimerTask() {
            public void run()  {
            	markets.clear();
            	List<CoinMarkets> markets = apiClient.getCoinMarkets("usd");
            	
            	for(CoinMarkets m: markets){
            		String marketsMessage = new Gson().toJson(m);
            		
            		System.out.println(marketsMessage);
            		
            		ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("bitcoin", "marketPrice", marketsMessage);
            		producer.send(producerRecord);
            	}
            	
            }
        }, 1, 10000);
    	//producer.close();
   }
}
