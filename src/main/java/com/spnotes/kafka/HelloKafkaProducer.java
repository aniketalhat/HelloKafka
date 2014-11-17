package com.spnotes.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import scala.collection.Seq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by user on 8/4/14.
 */
public class HelloKafkaProducer {
    final static String TOPIC = "pythontest";


    public static void main(String[] argv){
        Properties properties = new Properties();
        properties.put("metadata.broker.list","localhost:9092");
        properties.put("serializer.class","kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        kafka.javaapi.producer.Producer<String,String> producer = new kafka.javaapi.producer.Producer<String, String>(producerConfig);
        SimpleDateFormat sdf = new SimpleDateFormat();
        KeyedMessage<String, String> message =new KeyedMessage<String, String>(TOPIC,"Test message from java program " + sdf.format(new Date()));
        
        // showUser
        producer.send(message);
        producer.close();
    }
    
    public void showUser() throws IOException {
    	ConfigurationBuilder cb;
    	try {
    	System.out.print("Enter username: ");
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	String uname = reader.readLine();
    	cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
    	.setOAuthConsumerKey("hT0ZtwZSKYb408iLOUO38w")
    	.setOAuthConsumerSecret("2gDRSPKDsdm2MX2rGny0jK8t8YfdiTZKxMiR7Nc82A")
    	.setOAuthAccessToken("296890880-urKZtaUymaf9HzJXlN4PzFV4mRzM9MyCmI6rw2mx")
    	.setOAuthAccessTokenSecret("vomL6G20pXzgLmb7UhXMPrmvbUhysu9jr3dwxggmJQ5Zu");
    	Twitter twitter = new TwitterFactory(cb.build()).getInstance();
    	User user = twitter.showUser(uname);
    	if (user.getStatus() != null) {
    	System.out.println("Basic details of user on twitter");
    	System.out.println("Latest Status update: @" + user.getScreenName() + " - " + user.getStatus().getText());
    	System.out.println("Followers count: "+ user.getFollowersCount());
    	System.out.println("Friends count: "+ user.getFriendsCount());
    	System.out.println("Location: "+ user.getLocation());
    	Paging paging = new Paging(2, 40);
    	List<Status> statuses = twitter.getHomeTimeline(paging);
    	System.out.println("Showing home timeline.");
    	for (Status status : statuses) {
    	System.out.println(status.getUser().getName() + ":" +status.getText());
    	}
    	}
    	else {
    	// the user is protected
    	System.out.println("@" + user.getScreenName());
    	}
    	}
    	catch (TwitterException te) {
    	te.printStackTrace();
    	}
    	
    	
    }
    
}
