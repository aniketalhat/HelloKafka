package com.twitterstorm.kafka;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import twitter4j.Status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by aniketalhat on 1/12/15.
 */
public class PrintBolt extends BaseBasicBolt {

    ByteArrayInputStream byteArrayInputStream;
    ObjectInputStream objectInputStream;

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {

        byteArrayInputStream = new ByteArrayInputStream(tuple.getBinary(0));
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Status status = (Status) objectInputStream.readObject();
            System.out.println("Print: " + status.getText());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
