package com.twitterstorm.kafka;


import backtype.storm.task.IMetricsContext;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

import java.util.Map;

/**
 * Created by aniket on 15/01/13.
 */
public class LocalFactory implements StateFactory {
    @Override
    public State makeState(Map map, IMetricsContext iMetricsContext, int partitionIndex, int numPartitions) {
        
        return new LocalState(new LocalBackingMap());
    }
}
