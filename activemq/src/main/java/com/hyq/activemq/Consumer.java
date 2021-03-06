package com.hyq.activemq;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;
import java.util.Set;

/**
 * @author dibulidohu
 * @classname Consumer
 * @date 2019/4/1016:52
 * @description
 */
@Slf4j
@Component
public class Consumer {

    @JmsListener(destination = "footballQueue", containerFactory = "queueListener")
    public void footballQueueConsumer(String message) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("footballQueueConsumer1:" + message);
    }

    @JmsListener(destination = "basketballQueue", containerFactory = "queueListener")
    @SendTo("receipt.basketballQueue")
    public String basketballQueueConsumer(String message) {
        log.info("basketballQueueConsumer1:" + message);
        return "basketballQueueConsumer1.receipt";
    }

    @JmsListener(destination = "receiveQueue", containerFactory = "queueListener")
    public Message receiveQueueConsumer(Message message) {
        Message message1 = new ActiveMQMessage();
        try {
            log.info("basketballQueueConsumer1:" + message.getStringProperty("receive"));
            message1.setStringProperty("back", "i got it");
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return message1;
    }


    @JmsListener(destination = "footballTopic", containerFactory = "topicListener")
    public void footballTopicConsumer(String message) {
        log.info("footballTopicConsumer1:" + message);
    }

    @JmsListener(destination = "basketballTopic", containerFactory = "topicListener")
    public void basketballTopicConsumer(String message) {
        log.info("basketballTopicConsumer1:" + message);
    }


    @JmsListener(destination = "ackQueue", containerFactory = "ackQueueListener")
    public void ackQueueConsumer(Message message) {
        try {
            log.info("ackQueueConsumer1:" + message.getStringProperty("value"));
            Thread.sleep(10000);
            message.acknowledge();
            log.info("already ack");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "objectQueue", containerFactory = "queueListener")
    public void objectQueueConsumer(Message message) {
        log.info("objectQueue:" + message);
        Gson gson = new Gson();
        try {
            if (message instanceof ActiveMQObjectMessage) {
                ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
                Car car = (Car) objectMessage.getObject();
                log.info("i have a car!!!!! --->{}", gson.toJson(car));
            } else if (message instanceof ActiveMQMapMessage) {
                ActiveMQMapMessage mapMessage = (ActiveMQMapMessage) message;
                Map<String, Object> contentMap = mapMessage.getContentMap();
                Set<String> strings = contentMap.keySet();
                for (String string : strings) {
                    Object o = contentMap.get(string);
                    log.info("this car in mine:{}, say:{}", string, o.toString());
                }
            }
        } catch (JMSException e) {
            log.error(e.toString());
        }
    }
}
