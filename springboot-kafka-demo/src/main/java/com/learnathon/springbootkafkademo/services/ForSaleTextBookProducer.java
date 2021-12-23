package com.learnathon.springbootkafkademo.services;

import com.learnathon.entities.NewForSaleTextBookEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service // Allows Spring To Autowire the class into our RESTController
public class ForSaleTextBookProducer {
    @Autowired  // Tells Spring to Inject an instance of KafkaTemplate into our class.
    private KafkaTemplate<String, NewForSaleTextBookEvent> kafkaTemplate;

    public void sendMessage(String message){
        NewForSaleTextBookEvent newEvent = new NewForSaleTextBookEvent(message, 25.00);
        this.kafkaTemplate.send("learn_<uniqueId>",message, newEvent);
        
    }

}