package com.twitterstorm.kafka;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * Created by aniketalhat on 12/4/14.
 */
public class TwitterBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        //System.out.println("In bolt prepare()");
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        //System.out.println("In bolt execute() method");
        String string = tuple.getString(0);
        System.out.println("Tweet: " + string);
        outputCollector.emit(new Values(string));
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("declare-tweet"));
    }
}
