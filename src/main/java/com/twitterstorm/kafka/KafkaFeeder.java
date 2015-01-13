package com.twitterstorm.kafka;

import com.esotericsoftware.kryo.Kryo;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.Properties;
import java.util.Random;

/**
 * Created by aniketalhat on 1/11/15.
 */
public class KafkaFeeder
{

    String stream = null;
    Integer partion = 0;
    Properties  properties;
    Properties  twitterAuthProperties;
    ConfigurationBuilder cb;
    FileInputStream fileInputStream;
    TwitterStream twitterStream;
    static final String TOPIC = "twitterFeed";

    ProducerConfig producerConfig;
    Producer<String, byte[]> producer;
    KeyedMessage<String, byte[]> keyedMessage;


    ByteArrayOutputStream bs;
    ObjectOutputStream ous;

    public static void main(String args[]) {
        KafkaFeeder kafkaFeeder = new KafkaFeeder();
        try {

            kafkaFeeder.feedKafka();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KafkaFeeder() {
        properties = new Properties();
        properties.put("metadata.broker.list","localhost:9092");
        properties.put("key.serializer.class", "kafka.serializer.StringEncoder");
        properties.put("partitioner.class", "kafka.producer.DefaultPartitioner");
        properties.put("request.required.acks","1");

        producerConfig = new ProducerConfig(properties);
        producer = new Producer<String, byte[]>(producerConfig);

    }


    public void feedKafka() throws Exception{
        Random random = new Random();
        partion = new Integer(random.nextInt(2));
        this.getTwitterStream();
    }

    public void getTwitterStream() throws Exception{

        cb = new ConfigurationBuilder();


        twitterAuthProperties = new Properties();
        fileInputStream = new FileInputStream(new File(getClass().getResource("/config.properties").getPath()));
        twitterAuthProperties.load(fileInputStream);

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterAuthProperties.getProperty("consumerKey"))
                .setOAuthConsumerSecret(twitterAuthProperties.getProperty("consumerSecret"))
                .setOAuthAccessToken(twitterAuthProperties.getProperty("accessToken"))
                .setOAuthAccessTokenSecret(twitterAuthProperties.getProperty("accessTokenSecret"))
        .setHttpConnectionTimeout(100000); // twitter time-out

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                stream = new String(status.getText());
                System.out.println("Partition: "+ partion +" Tweet: " + stream);
                try {
                    bs = new ByteArrayOutputStream();
                    ous = new ObjectOutputStream(bs);
                    ous.writeObject(status);
                    keyedMessage = new KeyedMessage<String, byte[]>(TOPIC, partion.toString(), bs.toByteArray());
                    ous.flush();
                    ous.close();

                    producer.send(keyedMessage);

                } catch (IOException e) {
                    e.printStackTrace();
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
        //producer.close();

    }

}
