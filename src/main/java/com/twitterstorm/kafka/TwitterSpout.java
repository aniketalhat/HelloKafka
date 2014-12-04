package com.twitterstorm.kafka;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public class TwitterSpout extends BaseRichSpout {
	Producer<String, String> producer = null;
	KeyedMessage<String, String> message =null;
	ConfigurationBuilder cb = null;
	Properties authProperties = null;
	Properties configKafka = null;
	ProducerConfig producerConfig = null;
	FileInputStream input = null;
	final String TOPIC = "twitterStream";
	private SpoutOutputCollector collector;
	private TopologyContext context;


	@Override
	public void open(Map map, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.context = context;

		configKafka = new Properties();
		configKafka.put("metadata.broker.list", "localhost:9092");
		configKafka.put("serializer.class", "kafka.serializer.StringEncoder");
		producerConfig = new ProducerConfig(configKafka);
		producer = new Producer<String, String>(producerConfig);
	}

	@Override
	public void nextTuple() {

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

			TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
			StatusListener listener = new StatusListener() {
				@Override
				public void onStatus(Status status) {
					String str = new String("@" + status.getUser().getScreenName() + " - " + status.getText());
					//System.out.println(str);
					collector.emit(new Values(str));
					message = new KeyedMessage<String, String>(TOPIC,  "@" + status.getUser().getScreenName() + " - " + status.getText());
					producer.send(message);

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

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

		declarer.declare(new Fields("user-tweet"));
	}

}
