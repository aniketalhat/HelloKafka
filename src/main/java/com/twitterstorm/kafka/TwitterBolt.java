package com.twitterstorm.kafka;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by aniketalhat on 12/4/14.
 */
public class TwitterBolt extends BaseRichBolt {

    private OutputCollector outputCollector;
    private StringTokenizer strTok;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        //System.out.println("In bolt prepare()");
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        //System.out.println("In bolt execute() method");
        /*String tweet = tuple.getString(0);
        strTok = new StringTokenizer(tweet, " ");

        while(strTok.hasMoreTokens()) {
            String word = strTok.nextToken();

            if(word.contains("(")) {
                outputCollector.emit("country", new Values(word));
                System.out.println("Tweet Country: " + word);
            }
        }*/
        String country = tuple.getString(0);
        outputCollector.emit(new Values(country));
        //System.out.println(country);
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("tweets"));
    }
}
