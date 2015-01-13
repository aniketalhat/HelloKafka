package com.twitterstorm.kafka;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import com.twitterstorm.kafka.TwitterUtilities.*;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.kafka.*;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.testing.MemoryMapState;

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

            TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(zkHosts, "twitterFeed", "test-group");
            tridentKafkaConfig.forceFromStart = true;
            //tridentKafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
            
            LocalCluster localCluster = new LocalCluster();
            Config config = new Config();
            //config.setDebug(true);
            localCluster.submitTopology("tweetfeedprint", config, basicOperations(tridentKafkaConfig));
          }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static StormTopology basicOperations(TridentKafkaConfig tridentKafkaConfig) {

        TridentTopology tridentTopology = new TridentTopology();
        TransactionalTridentKafkaSpout transactionalTridentKafkaSpout = new TransactionalTridentKafkaSpout(tridentKafkaConfig);
        
        tridentTopology.newStream("twitterFeedStream", transactionalTridentKafkaSpout)
                                    .shuffle()
                                    .each(new Fields("bytes"), new ConstructTweet(), new Fields("tweet", "country"))
                                    .project(new Fields("tweet", "country"))

        
                                    //Construct tweet
                                    .each(new Fields("tweet", "country"), new CountryCheckFilter())
                                    .shuffle()
                    
                                    //Count Country
                                    .groupBy(new Fields("country"))

                                    .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))
                                    .newValuesStream()
                                    .each(new Fields("country", "count"), new Print());

                return tridentTopology.build();
        
    }
}
