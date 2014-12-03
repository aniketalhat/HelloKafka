package com.twitterstorm.kakfa;


import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.topology.base.BasePartitionedTransactionalSpout;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IComponent;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.transactional.partitioned.IPartitionedTransactionalSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class TwitterSpout extends BaseRichSpout {

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextTuple() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}


}
