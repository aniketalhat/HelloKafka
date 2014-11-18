package com.spnotes.kafka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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
	 
	public static void main(String[] args) throws IOException {
		TwitterClient producer = new TwitterClient();
		producer.fetchTweets();		
	}
	
	public void fetchTweets() {
		ConfigurationBuilder cb = null;
		Properties authProperties = null;
		FileInputStream input = null;
	
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
					 System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
					 }
					 @Override
					 public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
					 System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
					 }
					 @Override
					 public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
					 System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
					 }
					 @Override
					 public void onScrubGeo(long userId, long upToStatusId) {
					 System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
					 }
					 @Override
					 public void onStallWarning(StallWarning warning) {
					 System.out.println("Got stall warning:" + warning);
					 }
					 @Override
					 public void onException(Exception ex) {
					 ex.printStackTrace();
					 }
			 };
			 twitterStream.addListener(listener);
			 twitterStream.sample();
			
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}
}