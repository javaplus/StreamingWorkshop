package com.learnathon.springbootkafkademo;

import com.learnathon.entities.NewForSaleTextBookEvent;
import com.learnathon.springbootkafkademo.services.ForSaleTextBookProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForSaleTextBookRestController {

    @Autowired
    private ForSaleTextBookProducer producerService;

    @GetMapping(path="/for-sale-textbooks")
    public String createForSaleTextBook(@RequestParam("bookname") String bookName){
        producerService.sendMessage(bookName);
        return "The book for sale=" + bookName;

    }
    
}