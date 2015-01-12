package com.twitterstorm.kafka;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.*;

/**
 * Created by aniketalhat on 1/12/15.
 */
public class KafkaTopology {
    public static void main(String args[]){

        try {


            //zookeeper host for Kafka cluster
            BrokerHosts zkHosts = new ZkHosts("localhost:2181");

            // Create the KafkaSpout configuration
            // Second argument is the topic name
            // Third argument is the ZooKeeper root for Kafka
            // Fourth argument is consumer group id
            SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, "twitterFeed", "", "test-group");
            //kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
            kafkaConfig.forceFromStart = true;

            TopologyBuilder topologyBuilder = new TopologyBuilder();
            topologyBuilder.setSpout("kafka-spout", new KafkaSpout(kafkaConfig), 1);
            topologyBuilder.setBolt("print-bolt", new PrintBolt(), 1).globalGrouping("kafka-spout");


            LocalCluster localCluster = new LocalCluster();
            Config config = new Config();
            //config.setDebug(true);
            localCluster.submitTopology("tweetfeedprint", config, topologyBuilder.createTopology());


                // Wait for some time before exiting
                System.out.println("Waiting to consume from kafka");
                //Thread.sleep(10000);

            // kill the KafkaTopology
            //localCluster.killTopology("tweetfeedprint");
            // shut down the storm test cluster
            //localCluster.shutdown();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
