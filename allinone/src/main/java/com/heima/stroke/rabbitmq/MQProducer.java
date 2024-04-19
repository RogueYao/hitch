package com.heima.stroke.rabbitmq;


import com.alibaba.fastjson.JSON;
import com.heima.modules.vo.StrokeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MQProducer {
    private final static Logger logger = LoggerFactory.getLogger(MQProducer.class);
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送延时订单MQ
     *
     * @param strokeVO
     */
    public void sendOver(StrokeVO strokeVO) {
        String mqMessage = JSON.toJSONString(strokeVO);
        logger.info("send timeout msg:{}",mqMessage);
        //TODO:任务4.2-发送邀请消息

    }


}
