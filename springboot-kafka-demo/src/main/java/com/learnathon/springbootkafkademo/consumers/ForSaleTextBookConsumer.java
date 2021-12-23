package com.learnathon.springbootkafkademo.consumers;

import java.io.IOException;

import com.learnathon.entities.NewForSaleTextBookEvent;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ForSaleTextBookConsumer {
    
    @KafkaListener(topics = "learn_<uniqueid>", groupId = "<uniqueId>" )
    public void receiveForSaleTextBookEvent(ConsumerRecord<String, NewForSaleTextBookEvent> msg) throws IOException{
        // normally I'd do something usefull with my message, but I'm lazy...
        NewForSaleTextBookEvent eventMessage = msg.value();
        System.out.println("Got my message! Book Name:" + eventMessage.getBookName());       
        System.out.println("Got my message! Book Price:" + eventMessage.getPrice());       

        
    }
}