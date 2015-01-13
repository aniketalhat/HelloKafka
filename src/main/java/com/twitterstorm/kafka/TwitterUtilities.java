package com.twitterstorm.kafka;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFilter;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import twitter4j.Place;
import twitter4j.Status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by aniket on 15/01/13.
 */
public class TwitterUtilities {
    
    public static class ConstructTweet extends BaseFunction {

        ByteArrayInputStream byteArrayInputStream;
        ObjectInputStream objectInputStream;
        
        @Override
        public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
           
            Status status = null;
            String countryName = null;
            try {
                byteArrayInputStream  = new ByteArrayInputStream(tridentTuple.getBinary(0));
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                
                status = (Status) objectInputStream.readObject();
                Place place = status.getPlace();
                if(place!=null)
                    countryName = place.getCountry();
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            tridentCollector.emit(new Values(status.getText(), countryName));
        }
    }
    
    public static class CountryCheckFilter extends BaseFilter {

        @Override
        public boolean isKeep(TridentTuple tridentTuple) {
            
            String countryName = tridentTuple.getString(1);
            
            if(countryName != null)
                return true;
            return false;
        }
    }
    
    public static class Print extends BaseFilter{

        @Override
        public boolean isKeep(TridentTuple tridentTuple) {

            String countryName = tridentTuple.getString(0);
            long countryCount = tridentTuple.getLong(1);
            System.out.println("Country: "+ countryName + " Count: "+ countryCount);
            return true;
        }
    }
}
