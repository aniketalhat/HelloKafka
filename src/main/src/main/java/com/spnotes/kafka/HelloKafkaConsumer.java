package com.spnotes.kafka;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import twitter4j.Status;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Created by user on 8/4/14.
 */
public class HelloKafkaConsumer extends  Thread {
    final static String clientId = "SimpleConsumerDemoClient";
    final static String TOPIC = "twitterFeed";
    ConsumerConnector consumerConnector;


    public static void main(String[] argv) throws UnsupportedEncodingException {
        HelloKafkaConsumer helloKafkaConsumer = new HelloKafkaConsumer();
        helloKafkaConsumer.start();

    }

    public HelloKafkaConsumer(){
        Properties properties = new Properties();
        properties.put("zookeeper.connect","localhost:2181");
        properties.put("group.id","test-group");
        ConsumerConfig consumerConfig = new ConsumerConfig(properties);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
    }

    @Override
    public void run() {
        Map<String, Integer> topicCount = new HashMap<String, Integer>();
        topicCount.put(TOPIC, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams =
                consumerConnector.createMessageStreams(topicCount);
        List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(TOPIC);
        for (final KafkaStream stream : streams) {
            ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();

            ObjectInputStream ins;
            ByteArrayInputStream bi;
            while (consumerIte.hasNext()) {
                try {
                    byte [] data = consumerIte.next().message();
                    bi = new ByteArrayInputStream(data);
                    ins = new ObjectInputStream(bi);
                    Status status = (Status) ins.readObject();
                    System.out.println("Retrieved: " + status.getText());
                    ins.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @SuppressWarnings("unused")
	private static void printMessages(ByteBufferMessageSet messageSet) throws UnsupportedEncodingException {
        for(MessageAndOffset messageAndOffset: messageSet) {
            ByteBuffer payload = messageAndOffset.message().payload();
            byte[] bytes = new byte[payload.limit()];
            payload.get(bytes);
            System.out.println(new String(bytes, "UTF-8"));
        }
    }
}
