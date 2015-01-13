package com.twitterstorm.kafka;

import storm.trident.state.map.IBackingMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by aniket on 15/01/13.
 */
public class LocalBackingMap implements IBackingMap<Long> {
    Map<String, Long> storage= new ConcurrentHashMap<String, Long>();
    @Override
    public List<Long> multiGet(List<List<Object>> keys) {
        List<Long> values = new ArrayList<Long>();
        Iterator iterator = keys.iterator();
        
        while(iterator.hasNext()) {
            List key = (List)iterator.next();
            values.add(storage.get(key));
            
        }
        return values;
    }

    @Override
    public void multiPut(List<List<Object>> keys, List<Long> values) {
        for (int i = 0; i < keys.size(); i++) {
            List<Object> key = keys.get(i);
            Long val = values.get(i);
            storage.put(key.toString(), val);
        }
        
    }
}
