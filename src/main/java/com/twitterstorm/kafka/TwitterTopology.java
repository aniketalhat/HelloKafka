package com.twitterstorm.kafka;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * Created by aniketalhat on 12/4/14.
 */
public class TwitterTopology {

    public static void main(String args[]) {
        try {
            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout("spout-reader", new TwitterSpout());
            builder.setBolt("bolt-reader", new TwitterBolt()).shuffleGrouping("spout-reader");

            //Configuration
            Config conf = new Config();
            conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
            conf.setDebug(false);

            LocalCluster lc = new LocalCluster();
            lc.submitTopology("Getting-started", conf, builder.createTopology());
            //Thread.sleep(1000);
            //lc.shutdown();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
