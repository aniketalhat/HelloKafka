package com.spnotes.kafka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Utilizes the Twitter Streaming API to collect messages.
 */
class TwitterClient{
	Producer<String, String> producer = null;
	KeyedMessage<String, String> message =null;
	
	public static void main(String[] args) throws IOException {
		TwitterClient client = new TwitterClient();
		client.fetchTweets();
		
	}
	
	public void fetchTweets() {
		ConfigurationBuilder cb = null;
		Properties authProperties = null;
		FileInputStream input = null;
		final String TOPIC = "twitterStream";
		
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
			
			
			//configure kafka
			this.configBroker();
			
						
			 TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
			 StatusListener listener = new StatusListener() {
				 	 @Override
					 public void onStatus(Status status) {
					 //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
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
	
	public void filterTweets(TwitterStream twitterStream) {
		 FilterQuery filtre = new FilterQuery();
		 String[] keywordsArray = { "aniketalhat" };
		 filtre.track(keywordsArray);
		 twitterStream.filter(filtre);
	}
	
	public void configBroker() {
		Properties configKafka = null;
		ProducerConfig producerConfig = null;
		try {
			configKafka = new Properties();
			configKafka.put("metadata.broker.list", "localhost:9092");
			configKafka.put("serializer.class", "kafka.serializer.StringEncoder");
			producerConfig = new ProducerConfig(configKafka);
			producer = new Producer<String, String>(producerConfig);
	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}