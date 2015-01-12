package com.twitterstorm.kafka;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TwitterSpout extends BaseRichSpout {

	//Kafka Producer
	static Producer<String, String> producer = null;
	static KeyedMessage<String, String> message =null;
	static Properties kafkaProducerProperties = null;
	static ProducerConfig producerConfig = null;

	//Kafka Consumer
	static ConsumerConnector consumerConnector = null;
	static Properties kafkaConsumerProperties = null;
	
	//Twitter4J
	static final String TOPIC = "twitterStreamCountryNames";
	static TwitterStream twitterStream = null;
	static ConfigurationBuilder cb = null;
	static Properties authProperties = null;
	static FileInputStream input = null;

	private SpoutOutputCollector collector;
	private TopologyContext context;


	public static void configProducer() {

		//Configure Producer
		kafkaProducerProperties = new Properties();
		kafkaProducerProperties.put("metadata.broker.list", "localhost:9092");
		kafkaProducerProperties.put("serializer.class", "kafka.serializer.StringEncoder");
		producerConfig = new ProducerConfig(kafkaProducerProperties);
		producer = new Producer<String, String>(producerConfig);
	}

	public static void configConsumer() {

		//Configure Consumer
		kafkaConsumerProperties = new Properties();
		kafkaConsumerProperties.put("zookeeper.connect", "localhost:2181");
		kafkaConsumerProperties.put("group.id", "test-group");
		ConsumerConfig consumerConfig = new ConsumerConfig(kafkaConsumerProperties);
		consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
	}

	public static void openStream() {

		try {
				cb = new ConfigurationBuilder();
				authProperties = new Properties();
				input = new FileInputStream(new File("src/main/java/config.properties"));
				authProperties.load(input);

				cb.setDebugEnabled(true)
						.setOAuthConsumerKey(authProperties.getProperty("consumerKey"))
						.setOAuthConsumerSecret(authProperties.getProperty("consumerSecret"))
						.setOAuthAccessToken(authProperties.getProperty("accessToken"))
						.setOAuthAccessTokenSecret(authProperties.getProperty("accessTokenSecret"));

				twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
				StatusListener listener = new StatusListener() {
					@Override
					public void onStatus(Status status) {
						Place place = status.getPlace();
						if(place != null) {
							//String tweet = new String("@" + status.getUser().getScreenName() + " ("+ place.getCountry() + ") " + " - " + status.getText());
							String country = new String(place.getCountry());
							message = new KeyedMessage<String, String>(TOPIC, country);
							producer.send(message);
						}

					}
					@Override
					public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
						//System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
					}
					@Override
					public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
						//System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
					}
					@Override
					public void onScrubGeo(long userId, long upToStatusId) {
						//System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
					}
					@Override
					public void onStallWarning(StallWarning warning) {
						//System.out.println("Got stall warning:" + warning);
					}
					@Override
					public void onException(Exception ex) {
						ex.printStackTrace();
					}
				};
				twitterStream.addListener(listener);
				twitterStream.sample();
				//this.filterTweets(twitterStream);
				//producer.close();

				//Kafka Consumer
				configConsumer();
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void open(Map map, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.context = context;

		configProducer();
		openStream();
	}

	@Override
	public void nextTuple() {

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(TOPIC, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream =  consumerMap.get(TOPIC).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while(it.hasNext()) {
			String pulledTweeet = new String(it.next().message());
			System.out.println(pulledTweeet);
			collector.emit(new Values(pulledTweeet));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweets"));
	}
}
