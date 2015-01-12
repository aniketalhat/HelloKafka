package com.spnotes.kafka;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by user on 8/4/14.
 */
public class HelloKafkaProducer {
    final static String TOPIC = "pythontest";


    public static void main(String[] argv) throws IOException{
        Properties properties = new Properties();
        properties.put("metadata.broker.list","localhost:9092");
        properties.put("serializer.class","kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(properties);
      
        //Disable log4j warnings.
        Logger.getRootLogger().setLevel(Level.OFF);	
        
        kafka.javaapi.producer.Producer<String,String> producer = new kafka.javaapi.producer.Producer<String, String>(producerConfig);
        SimpleDateFormat sdf = new SimpleDateFormat();
        KeyedMessage<String, String> message =new KeyedMessage<String, String>(TOPIC,"Test message from java program " + sdf.format(new Date()));
        
        //showUser();
        producer.send(message);
        producer.close();
    }
    
    public static void showUser() throws IOException {
    	ConfigurationBuilder cb;
    	Properties authProperties = new Properties();
    	InputStream input = null;
    	BufferedReader reader =null;
    	try {
    			input = new FileInputStream(new File("src/main/java/config.properties"));
    			authProperties.load(input);
    			
		    	System.out.print("Enter username: ");
		    	reader = new BufferedReader(new InputStreamReader(System.in));
		    	String uname = reader.readLine();
		    	
		    	cb = new ConfigurationBuilder();
		    	cb.setDebugEnabled(true)
		    	.setOAuthConsumerKey(authProperties.getProperty("consumerKey"))
		    	.setOAuthConsumerSecret(authProperties.getProperty("consumerSecret"))
		    	.setOAuthAccessToken(authProperties.getProperty("accessToken"))
		    	.setOAuthAccessTokenSecret(authProperties.getProperty("accessTokenSecret"));
		    	
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
