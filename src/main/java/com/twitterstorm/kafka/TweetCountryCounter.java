package com.twitterstorm.kafka;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Jedis;

import java.util.Map;


/**
 * Created by aniket on 12/9/14.
 */
public class TweetCountryCounter extends BaseRichBolt {
    private OutputCollector outputCollector;
    JedisPool pool;
    Integer count;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
        count = new Integer(0);
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            Jedis jedis = pool.getResource();
            String country = tuple.getString(0);

            String strValue = jedis.get(country);
            if(strValue != null) {
                count = 0;
                count = Integer.parseInt(strValue);
                }
            count += 1;
            jedis.set(country, count.toString());
            System.out.println("Key: "+ country + " Value: " + count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("tweets"));
    }
}
