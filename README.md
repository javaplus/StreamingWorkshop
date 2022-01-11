# SpringBoot-Kafka-Tutorial

# Purpose?
This tutorial will show how to use modern development techniques and frameworks to create a Microservice that is capable of producing and consuming events from an Event Backplane.  The idea is to introduce some of the technology that enables [Event Streaming](https://tanzu.vmware.com/event-streaming).  The technology we will be using is [Spring Boot](https://spring.io/projects/spring-boot), [Kafka](https://kafka.apache.org/), and [JSON Schema](https://json-schema.org/).  If you want to know what it's like to actually develop a SpringBoot app and interact with Kafka, this tutorial is for you.  If you are scared of writing code, it may challenge you, but there are detailed instructions... So be courageous and sally forth!

**QUICK NOTE:** In order to allow this to tutorial to get you quickly working with the technologies, we cut out some best practices you'd typically follow. For example, when creating an Event Streaming/Notification architecture you'd want to start with a Top down approach by creating an [Event Streaming Specification](https://www.asyncapi.com/).  Of course you'd also want to have your REST API implement proper security typically verifying an Identity Token as well as have proper logging and monitoring.  So, be aware of these requirements when actually developing your own event streaming architectures, but for this tutorial we will bypass many of these so you can get some quick hands on practice and see stuff working today! :)

# Business Scenario
This tutorial is based off an app called College Cafe.
In short, the College Cafe is an online product that allows college students to list used books for sale and connect them with other college students that may want to buy those books.
For this tutorial, we will focus on the problem of how to allow a seller to submit books for sale and how to notify potential buyers that a specific book is for sale.

# Architecture
Below is a diagram and explanation of the architecture:

![Architecture](/images/ArchitectureHighLevel.PNG)

You will be coding the yellow and blue boxes (the microservices).  To simplify things, we will not actually be notifying or emailing customers.  Also, since our focus is really on the technology we require and not as much on the architecture, we will allow you to combine the code into one mini monolith just for this tutorial so you don't have to create two separate apps... though that may be a stretch goal for the adventurous.


# Prerequisites:
1. Admin Rights on your machine to install and configure software.
1. Java development Kit (JDK 11) How to setup Java and Maven on Windows Video: [https://www.youtube.com/watch?v=5Wscex9ufT4](https://www.youtube.com/watch?v=5Wscex9ufT4)
	- [Adopt Open JDK (11 LTS)](https://adoptopenjdk.net/)
1. Maven 3.x - [Download Maven here](https://maven.apache.org/download.cgi) - Choose **Binary zip archive - apache-maven-3.6.3-bin.zip**
1. An IDE or Text Editor. We recommend [Visual Studio Code](https://code.visualstudio.com/)
      -  If using Visual Studio Code, the following extensions are recommended when developing a Spring Boot microservice(To Install, open VS Code and click on the Extensions button on the left and search for the following):

		* Java IDE Pack
		* Java Extension Pack
   		* Maven for Java
		* Lombok Annotations Support
		* Spring Boot Dashboard
		* Spring Boot Tools
		* Checkstyle for Java

      -  Alternatively, you can install these extensions using the following commands at your favorite command prompt (works on Windows and Mac):

	```shell
	code --install-extension vscjava.vscode-java-pack
	code --install-extension vscjava.vscode-maven
	code --install-extension pverest.java-ide-pack
	code --install-extension gabrielbb.vscode-lombok
	code --install-extension vscjava.vscode-spring-boot-dashboard
	code --install-extension pivotal.vscode-spring-boot
	code --install-extension shengchen.vscode-checkstyle
    ```

1. Access to Kafka.
   - Info will be provided in class.
   
   

**This is the end of the Prerequisites, below this starts the actual labs.  So, you can wait till the instructor starts the session to continue on.** 


# Getting started

## Generate a SpringBoot starter project
Start by creating a SpringBoot application.  We will be using the [Spring Initializr](https://start.spring.io/).
Go to [https://start.spring.io/](https://start.spring.io/) to get started(Recommend to right click and open in a new tab). In the Spring Initiliazr, change the **Group** to **com.learnathon** and the **Artifact** to **springboot-kafka-demo**.  To add Dependencies click the **ADD DEPENDENCIES** and search for and add the **Web** (Spring Web) dependency and search for **Kafka** and add the **Spring for Apache Kafka** dependency.
(NOTE: These just create dependencies in the pom.xml file of the project.)

  ![Spring Initializer](/images/StartSpringIO.PNG)

Click the **Generate** button at the bottom and it will start a download of a zipped SpringBoot starter  project.

After downloading, unzip the project to your hard drive. It should look like the picture below once unzipped.

![Unzipped Project](/images/unzipped.PNG)

## Open Project in an IDE

Now, open the project in your favorite IDE. For VS Code, I just right click on the folder in my Windows Explorer and choose "Open with VS Code".  If you don't have the "Open with VS Code" option, another option for VS Code is to open a command prompt (or Git Bash)inside the unzipped project at the same level as the pom.xml file and type **code .** (Note the '.' is part of the command). This will open VS Code at the current folder and have all the files available in the "Explorer" in VS Code.

<details>
 <summary>Code in VS Code (Click Here for Screenshot)</summary>
  <img src=/images/ProjectInVSCode.PNG></img>
</details>


If using an eclipse based IDE, import the project as an existing Maven project.


## Creating Your First Rest Controller

We will start by creating a simple REST endpoint to accept the request.  We will do this by creating a simple class and annotating it with [@RestController](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html). This class will be entry point to your application.
(You will find that most of the key functionality and configuration is added to our classes and methods by [annotations](https://dzone.com/articles/how-annotations-work-java) instead of XML configuration.)

For convenience sake, you can create this class in the same package as the generated class. (I usually put my RestControllers in a package called "controllers".)

**VS Code Users** To create a new class in VS Code, right click on the folder you want to create the new class ("com/learnathon/springbootkafkademo" in our case) and choose **New**.  Then choose **Class** from the drop down.  Type in the class name and hit enter. Let's name this class: **ForSaleTextBookRestController**. This will create the class with the package name there.

![Starting_Package](/images/FirstRestControllerPackageNClass.png)

After creating the class, you need to add the [@RestController](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html) annotation right above the class declaration.  Then you will need to create a method that will receive requests that indicate a book is for sale. (The full code can be seen if you expand it just below this paragraph.)  In the real world, this would most likely be a POST request since it would be creating a for sale text book instance in our system.  However, to make our coding and testing simpler, we are just going to make this map to a simple GET request.  This allows us to simply use a browser to invoke our REST endpoint.  To map a GET request to our method we will need to annotate it with the [GetMapping](https://www.javaguides.net/2018/11/spring-getmapping-postmapping-putmapping-deletemapping-patchmapping.html) annotation.  So, just create a method called "createForSaleTextBook()" that returns a String and annotate the method with the @GetMapping.  We will set the path of the @GetMapping annotation to **"/for-sale-textbooks"**.  This method could just return a hard coded string to get started with.

<details>
 <summary>Click Here for the full source code of the Simple RestController(Spoiler Alert):</summary>


```
package com.learnathon.springbootkafkademo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForSaleTextBookRestController {

    @GetMapping(path="/for-sale-textbooks")
    public String createForSaleTextBook(){
        return "Easy Peasy, REST is EASY";

    }

}
```
</details>

**NOTE**- If you are coding along and not copying and pasting the whole examples (which is preferred), just be aware that you will have to import GetMapping and RestController for this first one.  Also for the rest of this tutorial, just be aware that you will have to import the new annotations and classes you reference.  In VS Code, you should be able to put your cursor after the class or annotation name and hit CTRL+Space to bring up content assist that will show you the fully qualified class name.  If you select the fully qualified class name, it should create the import for you.


Helpful Links:

[RESTController Tutorial](http://zetcode.com/springboot/restcontroller/)

[GetMapping Examples](https://www.javaguides.net/2018/11/spring-getmapping-postmapping-putmapping-deletemapping-patchmapping.html)

## Test your application

Once you've created your first RestController and GetMapping method, you need to start it.
The nice thing is that your generated project came with a POM file that included the [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/usage.html) which makes running your app with Maven easy.
You can just open up a command prompt **at the same location as the pom** and run the **mvn spring-boot:run** command to start the application

```
mvn spring-boot:run
```
**(VS Code tip)**:In VS Code, you can hit **CTRL + SHIFT + \`** to open a terminal or go to the "Terminal" menu and click "New Terminal" to get a terminal/command prompt in VS Code.

This utilizes the [spring boot maven plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/index.html)

If you are using the STS IDE (SpringSource Tool Suite), you can right click on the project and choose **Run As->Spring Boot App**.

**NOTE:** By default, Spring Boot using port 8080 to run.  So, be sure that port is free.

Once your app is running, you should be able to go to this endpoint to test it: [http://localhost:8080/for-sale-textbooks](http://localhost:8080/for-sale-textbooks).
Of course, this URL can change depending on how you define your @GetMapping.
You should see the hard coded string you returned displayed in the browser...

  ![Browser Image](/images/FirstRestTestBrowser.PNG)

## Accept Data In Your RestController

The next bit of functionality we want to add is the ability to pass information about the for sale text book to our RestController.  Since our RestController only accepts a GET request the only way to pass data is via Request Parameters like this **http://localhost:8080/for-sale-textbooks?bookname=Dune**.  We are only going to pass the name of the book for sale.

We need to modify our RestController to take a book name as a parameter. So, we need to add an input Parameter of type String to our createForSaleTextBook method. Let's call this input parameter **bookName**. Go ahead and add that now. Also,  concatenate the new bookName input parameter to the string you return. Let's modify the return string to be like this:   
```
return "The book for sale=" + bookName;
```
But how do we indicate to Spring that this parameter should come from a Request parameter called "bookname"?  It's easy... It's through the **@RequestParam** annotation.  We've already annotated our class and our method, but you can even annotate a method parameter.  So, by using the [@RequestParam annotation](https://www.baeldung.com/spring-request-param) we can tell spring to pull the book name from the incoming http request and map it to our method's input parameter.  Then we will update the method to return the book name in it's response so we can see it when we test.  So, we simply need to prefix the new input parameter with **@RequestParam("bookname")**. Remember, you will have to add the import for **org.springframework.web.bind.annotation.RequestParam** in order to use the @RequestParam annotation.


<details>
 <summary>See what the new method should look like by Clicking HERE </summary>

  ```
       @GetMapping(path="/for-sale-textbooks")
    public String createForSaleTextBook(@RequestParam("bookname") String bookName){
        return "The book for sale=" + bookName;

    }

```
</details>

### Restart and test

To stop your SpringBoot app, in the STS IDE, you can just click the stop button, or if you are simply running from the command line or a terminal (as I usually do), just hit **CTRL + C** a few times to break out back to a command prompt and then just rerun the **mvn spring-boot:run** command.

Test by using this URL in your browser:

[http://localhost:8080/for-sale-textbooks?bookname=Dune](http://localhost:8080/for-sale-textbooks?bookname=Dune)

You should see something like this:

![BrowserDataPass1.PNG](/images/BrowserDataPass1.PNG)

You can change the value of **bookname** in the url to test passing different values to your RestController.  You should see the bookname value reflected in the text in the browser that's returned.  If you see a "Whitelabel Error Page" in the browser, then look at the logs in the terminal or command prompt where you ran the app to find a more detailed message about what's wrong.  Double check the request parameter name **bookname** matches what you have in the **@RequestParam("bookName")**. NOTE: Case does matter.  **bookname** does not equal **bookName**.

## Time to Talk to Kafka

Now when we get our HTTP request in our RestController, we want to be able to broadcast a message to Kafka that a new for sale text book was added.  To do this we are going to create a message producer class that utilizes the [KafkaTemplate](https://docs.spring.io/spring-kafka/reference/html/#sending-messages) from the [Spring-Kafka Library](https://spring.io/projects/spring-kafka) to write messages to a Kafka topic.

You need to create a new class for this and I would put into a folder/package called "services" and call it "ForSaleTextBookProducer".  So, create a new folder/package called **services** in the "com\learnathon\springbootkafkademo" package and then create the class **ForSaleTextBoodProducer** in this services package.

![Producer Class](/images/ProducerClass.PNG)

For this part, I'm going to give you the full code to talk to Kafka and then explain it.

Here is the full code for the new class:

```
package com.learnathon.springbootkafkademo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service // Allows Spring To Autowire the class into our RESTController
public class ForSaleTextBookProducer {
    @Autowired  // Tells Spring to Inject an instance of KafkaTemplate into our class.
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message){
        this.kafkaTemplate.send("learn_<your unique id>", message, message);

    }

}

```
The [@Service](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Service.html) annotation above the ForSaleTextBookProducer allows Spring to recognize this class as a Service bean that it should manage and makes it available to inject into our RestController class.

We declare a private property for the KafkaTemplate and by adding the [@Autowired](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/annotation/Autowired.html) annotation, Spring will automatically search our classpath for a valid implementation of a KafkaTemplate to inject into our class.  The cool thing is that by just adding the maven dependency for Spring-Kafka(which we did when we generated our project), it will find one created by Spring for us.  So, we don't have to create this. Spring will inject it's own into our class.

#### Understanding the KafkaTemplate.send() method
So, from there we just have a sendMessage() method that will use the KafkaTemplate to send a message.  Notice that the [KafkaTemplate.send() method](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/core/KafkaTemplate.html#send-java.lang.String-K-V-) that we are using takes 3 parameters.  The first parameter is the topic name... **Be sure to replace the "\<your unique id>" in the code with your actual unique id, but prefix it with "learn_"** (example: learn_btarlton).  By having a unique topic name it will be easier for you to test.

The second parameter is the key for the message... we don't have time to talk about keys, but just know that this value can determine which partition the message goes to and can be important in certain scenarios.  [Here is a good article on how keys and partitions work](https://linuxhint.com/apache_kafka_partitions/).  For now, the key can be any string (I'm using the book name for now).

The third parameter to the send() method is our actual message. For now, this is just our book name which is of type String.

#### Update the RestController

In order to use our new ForSaleTextBookProducer, we need to inject it into our Rest Controller.  See the changes to the rest controller here:

```
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
```

Notice we simply add a property for our Producer class and let Spring Autowire it (Meaning Spring will inject an instance into it at runtime).

####  Restart the application

Now it's time to restart the application.  However, if you are thinking, "but wait we didn't tell it how to connect to Kafka", you'd be right.  If you start the application now and test it, you'd see several repeating errors like this:
```
2019-11-23 10:55:39.822  WARN 27248 --- [ad | producer-1] org.apache.kafka.clients.NetworkClient   : [Producer clientId=producer-1] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Broker may not be available.
2019-11-23 10:55:41.024  WARN 27248 --- [ad | producer-1] org.apache.kafka.clients.NetworkClient   : [Producer clientId=producer-1] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Broker may not be available.
2019-11-23 10:55:42.430  WARN 27248 --- [ad | producer-1] org.apache.kafka.clients.NetworkClient   : [Producer clientId=producer-1] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Broker may not be available.

```
This is because with no configuration specified it will try to connect to a local Kafka instance.  If you don't have Kafka running locally, then you will see these errors.  We will be connecting to our Confluent Kafka cluster.  Let's see how to do that...

### Configuring SpringBoot to talk to Kafka

In a less secure Kafka deployment, you would only have to specify the location of the Kafka brokers you wanted to use.  However, because of our secure Kafka environment, we also have to specify an API key and secret to talk to Kafka.  The Api key and secret can be found here: [https://github.com/javaplus/StreamingWorkshop/blob/76a836690b54db2666f3cb953550a4ec09036f8c/temp/api.txt](https://github.com/javaplus/StreamingWorkshop/blob/76a836690b54db2666f3cb953550a4ec09036f8c/temp/api.txt)

The short value is the key and the long value is the secret(aka password).


For SpringBoot, you can simply put this configuration in the **application.properties** file that is already under the **src/main/resources** folder

**application.properties**
```
# This one line is usually all you need to point to the right broker
spring.kafka.producer.bootstrap-servers=pkc-ymrq7.us-east-2.aws.confluent.cloud:9092

# Security configuration
spring.kafka.properties.security.protocol=SASL_SSL
spring.kafka.properties.sasl.mechanism=PLAIN
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<api_key>" password="<api_secret>";

```

### Your Kafka Topic

Before we can produce a message, we need to create a Topic in our Kafka cluster to write our messages to.
I've created topics for you and you should have been assigned a topic name.

#### Retest for Real

Now you should have all you need configured and coded to send a message to Kafka.  So, restart your SpringBoot application.  And refresh/resubmit your request to your RestController.

Look at your logs and you should hopefully see some logs, but no errors. You may see a warning message about **LEADER_NOT_AVAILABLE** the first time you run it and that's ok.


**NOTE**: If you get an error, please verify your topic name in the code first and then check all your application.properties settings

**Now it's time to celebrate!!**  You got your first messages that talks to Kafka... But there's more awesomeness to come.... Let's get to it!

### Creating a Kafka Consumer

Now, obviously, we don't have a well defined API or event message, but we are mostly focusing on the technology right now. We will come back shortly and come up with a Schema for our event messages going to Kafka.  But our REST API, will remain this simple for the rest of the tutorial. But, we do now have the very, very basics of the TextBookExchange Microservice from the architectural diagram above.  That is, we can receive an HTTP Request that indicates a new book is for sale and then publish an event to Kafka.

Now it's time to work on a message consumer.  Most likely, in the real world, we'd create a new microservice to consume the event message that our app is publishing, but for the sake of time, we will just build a message consumer in the existing app. Yes, it will be a little odd for our app to consume the very message it produces, but again it's just to keep this tutorial from getting longer than it already is! :smile:

**NOTE on "Consumers":** We use the word "Consumer" a lot in the messaging world, but something to be aware of from a Kafka perspective is that a Kafka client doesn't "consume" the message in a traditional messaging sense.  That is with Kafka, when a client reads the message from the topic, the message does not get removed from the topic.  Instead, the consumer's pointer or "offset" to which message in the topic they are currently reading gets incremented.  So, at any point a consumer could go back and re-read("replay") all the messages from a topic if they chose.  Here's a good article that quickly explains it: [Messages and Kafka in Plain English](https://www.iteachrecruiters.com/blog/message-queues-and-kafka-explained-in-plain-english/)

So, let's create our Kafka Consumer.  To do this we will create a new class in a "consumers" folder/package that will become our message consumer. Let's call this class the **ForSaleTextBookConsumer**.

![ConsumerClass](/images/ForSaleConsumer.PNG)


This new class will also need to be annotated with the @Service annotation so that Spring manages it as a Bean and loads it on startup.  We also need to create a method on this new class and let's call it **"receiveForSaleTextBookEvent"**.  It will return **void**, but take in a kafka **ConsumerRecord<String, String>** as a parameter.  To have our method actually receive message from a Kafka topic, all we need to do is annotate it with an [@KafkaListener annotation](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/annotation/KafkaListener.html) and pass in the **topics** we wish to listen on.

Here is the code:

```
package com.learnathon.springbootkafkademo.consumers;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ForSaleTextBookConsumer {

    @KafkaListener(topics = "learn_<your unique id here>", groupId = "learn_<your unique id>" )
    public void receiveForSaleTextBookEvent(ConsumerRecord<String, String> msg) throws IOException{
        // normally I'd do something useful with my message, but I'm lazy...
        System.out.println("Got my message! :" + msg.value());

    }
}

```
**NOTE**: Replace the parts of the code above where it says **"\<your unique id here>"** with your actual unique id.

When we specify the input parameter of type [ConsumerRecord<String, String>](https://kafka.apache.org/21/javadoc/org/apache/kafka/clients/consumer/ConsumerRecord.html), the KafkaListener will give us Kafka messages that contains a Key of type String and the Value is of type String.  To access the main body of the message we call the ConsumerRecord.value() method.  We are currently just logging out the message, but this is where you would normally do something useful, like search a DB, send an email, or produce another event.

Before we can test, we will need to add one line to our **application.properties** file to make tell our Consumer how to connect to the Kafka brokers.

Here's the one new line you need to add (Note it is very similar to a line you already have but this is for the **consumer** vs the previously config line you already have is for the **producer**.):
```
spring.kafka.consumer.bootstrap-servers=pkc-ymrq7.us-east-2.aws.confluent.cloud:9092
```
### Retest Sending and Consuming a Kafka Message.

Now restart your application and refresh your browser(submit your GET request) to trigger sending a Kafka message again.
This time you can simply watch the console output to see if your message was sent and received(If it's received, you can assume it was sent).
You should see a lot more logs when you first start up now.  These extra logs indicate the KafkaListener is connecting to the Kafka broker and your specified topic.
If all works well you should see something like this in your console after sending and receiving an event message.

```
2019-11-23 14:26:53.578  INFO 3964 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: T8NclBfKReiJez1KFMidaQ
Got my message! :Dune

```

If you have made it this far and it's working, then jump up and down, you've successfully published and subscribed to a Kafka event!  :metal:

### Well Crafted Event Messages with Event Streaming Specifications

Hopefully, you are seeing how easy, from a development standpoint, it is to build microservices that interact with Kafka.
One major hurdle to being able to subscribe to topics and consume messages is understanding what the message format will look like and what data will it contain.  **For all the same reasons it was important to have schemas for SOAP web services and REST API's, it's crucial to have schemas for messages streamed through Kafka.**

We've chosen [Async API](https://www.asyncapi.com/) as our mechanism for documenting our events.  This specification allows us to specify our Events and their Schema structure in a simple yaml file very similar to an OAS.  So just an OAS helps allows an API provider publish the details about the resources they are exposing and how to interact with them, the Async API allows producers to publish details about the events they are producing and their format.  We won't get into creating an entire Async API for this tutorial, but know that in the real world, **creating the Async API spec would be your starting point before serious development commences.**  We will however be using one of the building blocks of Async API which is [JSON Schema](https://json-schema.org/). This will allow us to have a standard way of describing the format of our event message.

We've simply been publishing a simple String message to Kafka up to this point.  But now we are going to publish a more proper event message based on this [JSON Schema](https://json-schema.org/):

```
$schema: "http://json-schema.org/draft/2019-09/schema#"
title: NewForSaleTextBookEvent
description: Represents the event when a text book has been put up for sale
# Java Class to create from this schema
javaType: com.learnathon.springbootkafkademo.entities.NewForSaleTextBookEvent
type: object
additionalProperties: false
required:
  - bookName
  - price
properties:
  bookName:
    type: string
  price:
    type: number

```

We will actually add the schema above to our SpringBoot app.  Copy the schema above into a file named **"schema.yaml"** in the **src/main/resources/models** folder. NOTE: You will have to create the "models" folder and the "schema.yaml" file.

![JSONSchema](/images/Schema.PNG)

To work with a message based on this Schema in our Java code, we want to have a Plain Old Java Object (POJO) that represents the message/event data we want to work with.  Instead of manually creating the Java class, we will use the [JSON Schema 2 Pojo Plugin](https://joelittlejohn.github.io/jsonschema2pojo/site/1.0.2/generate-mojo.html) to actually generate a Java class based on our schema.  To do this we need to update our **pom.xml** to add a new plugin configuration.

Here's the plugin configuration we need to add in our pom.xml in the <plugins> block:

```
	    <plugin>
		<groupId>org.jsonschema2pojo</groupId>
		<artifactId>jsonschema2pojo-maven-plugin</artifactId>
		<version>1.0.2</version>
		<configuration>
		  <sourceDirectory>${basedir}/src/main/resources/models/</sourceDirectory>
		  <!-- <targetPackage>${jsonschema2pojo.package}</targetPackage> -->
		  <sourceType>YAMLSCHEMA</sourceType>
		  <outputDirectory>${basedir}/src/main/java/</outputDirectory>
		  <includeConstructors>true</includeConstructors>
		</configuration>
		<executions>
		  <execution>
			<goals>
			  <goal>generate</goal>
			</goals>
		  </execution>
		</executions>
	  </plugin>

```
Here is what the <plugins> block should look like after modifying your **pom.xml** with the JSONSchema2Pojo plugin:

```
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.jsonschema2pojo</groupId>
				<artifactId>jsonschema2pojo-maven-plugin</artifactId>
				<version>1.0.2</version>
				<configuration>
				  <sourceDirectory>${basedir}/src/main/resources/models/</sourceDirectory>
				  <sourceType>YAMLSCHEMA</sourceType>
				  <outputDirectory>${basedir}/src/main/java/</outputDirectory>
				  <includeConstructors>true</includeConstructors>
				</configuration>
				<executions>
				  <execution>
					<goals>
					  <goal>generate</goal>
					</goals>
				  </execution>
				</executions>
			  </plugin>
		</plugins>
	</build>
```

At this point, if you have your JSON schema under your src/main/resources/models folder and you have the right configuration in your pom you can simply run **mvn compile** to compile the code and this will fire the JSON Schema to POjo plugin to generate the code.

After the **mvn compile** runs and generates the code from the schema you should see a new class like this:
![Schema Generated](/images/SchemaGeneratedClass.PNG)

If you look at the newly generated **NewForSaleTextBookEvent** class, you will notice it has 2 properties.
	* bookName
	* price

We will use this class to encapsulate the event data and use it in our code and have it to produce a JSON message for us.

### Using Schema Generated Class in our code

Now that we have a java class based off of the defined schema, we want to modify our Producer code to produce that event.

To do this, simply modify the ForSaleTextBookProducer class's KafkaTemplate to be of type
**KafkaTemplate<String, NewForSaleTextBookEvent>**

And then in the sendMessage() method, build a new NewForSaleTextBookEvent instance and pass that to the KafkaTemplate.send() method.

Here's the code:

```
@Service // Allows Spring To Autowire the class into our RESTController
public class ForSaleTextBookProducer {
    @Autowired  // Tells Spring to Inject an instance of KafkaTemplate into our class.
    private KafkaTemplate<String, NewForSaleTextBookEvent> kafkaTemplate;

    public void sendMessage(String message){
        NewForSaleTextBookEvent newEvent = new NewForSaleTextBookEvent(message, 25.00);
        this.kafkaTemplate.send("learn_btarlton",message, newEvent);
    }

}
```

With this code, our producer will now produce a message based on the schema... well almost... there's some configuration we need to do.  Before we modify the configuration, let's look at how we modify the Consumer code to now consume the new event message.

It's actually pretty similar... We simply modify **ForSaleTextBookConsumer**'s method to take a **ConsumerRecord** like this **ConsumerRecord<String, NewForSaleTextBookEvent>**.

Here's the code:

```
@Service
public class ForSaleTextBookConsumer {

    @KafkaListener(topics = "learn_<YOUR_ID_HERE>", groupId = "learn_<YOUR_ID_HERE>" )
    public void receiveForSaleTextBookEvent(ConsumerRecord<String, NewForSaleTextBookEvent> msg) throws IOException{
        // normally I'd do something useful with my message, but I'm lazy...
        NewForSaleTextBookEvent eventMessage = msg.value();
        System.out.println("Got my message! Book Name:" + eventMessage.getBookName());
        System.out.println("Got my message! Book Price:" + eventMessage.getPrice());

    }
}

```

Notice the **msg.value()** will now return a type of **NewForSaleTextBookEvent**.

### The Final Stretch

The final thing we need to make it all work is a few lines of configuration in our trusty **application.properties** to tell Spring that we want to use JSON serializers/deserializers.

Here is the last bit of lines you need to add to your **application.properties** file:

```
## Serialization/Deserialization configuration
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer


spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.auto-offset-reset=latest
spring.kafka.consumer.properties.spring.json.trusted.packages=*


```

### Now Kick Those Tires!

Now, I think you are ready to test it all together.

Restart the app and hit the REST endpoint one more time and you should see glorious output in your console like this:

```
2019-11-23 16:09:50.890  INFO 21020 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: T8NclBfKReiJez1KFMidaQ
Got my message! Book Name:Dune
Got my message! Book Price:25
```
If you got this, rejoice! :joy: You've successfully produced and consumed messages from Kafka using a JSON schema.  If it didn't work, just double check everything, and try, try again.

I'd encourage you to look back through the few classes you created and appreciate the concise nature of the code and the separation of the code and the configuration.  With only a few lines of code, some annotations, your application.properties handling the configuration, and your SpringBoot Mojo, you have a functioning Rest Endpoint that can produce and consume messages via Kafka... Ain't that cool!

Hope you've enjoyed this tutorial!

GLHF!
