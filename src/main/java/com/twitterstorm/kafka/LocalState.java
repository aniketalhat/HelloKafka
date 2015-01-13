package com.twitterstorm.kafka;

import storm.trident.state.map.IBackingMap;
import storm.trident.state.map.NonTransactionalMap;

/**
 * Created by aniket on 15/01/13.
 */
public class LocalState extends NonTransactionalMap<Long> {
    protected LocalState(IBackingMap backing) {
        super(backing);
    }
}
