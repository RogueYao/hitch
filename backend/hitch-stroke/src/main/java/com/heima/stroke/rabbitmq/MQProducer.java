package com.heima.stroke.rabbitmq;


import com.heima.stroke.configuration.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//TODO:任务4.2-发送邀请消息
@Component
public class MQProducer {
    private final static Logger logger = LoggerFactory.getLogger(MQProducer.class);
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送延时订单MQ
     *
     * @param mqMessage
     */
    public void sendOver(String mqMessage) {

        logger.info("send timeout msg:{}",mqMessage);
        rabbitTemplate.convertAndSend(RabbitConfig.STROKE_OVER_QUEUE_EXCHANGE, RabbitConfig.STROKE_OVER_KEY, mqMessage);
    }


}
