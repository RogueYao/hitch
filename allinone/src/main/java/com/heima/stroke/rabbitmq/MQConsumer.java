package com.heima.stroke.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.heima.modules.vo.StrokeVO;
import com.heima.configuration.RabbitConfig;
import com.heima.stroke.handler.StrokeHandler;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 行程消费者类
 *
 */
@Component
public class MQConsumer{
    private final static Logger logger = LoggerFactory.getLogger(MQConsumer.class);

    @Autowired
    private StrokeHandler strokeHandler;


    /**
     * 行程超时监听
     *
     * @param massage
     * @param channel
     * @param tag
     */
    @RabbitListener(
            bindings =
                    {
                            @QueueBinding(value = @Queue(value = RabbitConfig.STROKE_DEAD_QUEUE, durable = "true"),
                                    exchange = @Exchange(value = RabbitConfig.STROKE_DEAD_QUEUE_EXCHANGE), key = RabbitConfig.STROKE_DEAD_KEY)
                    })
    @RabbitHandler
    public void processStroke(Message massage, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        //TODO:任务4.3-接收死信队列消息(以下代码已完成，用于验证配置和发送无误)
        StrokeVO strokeVO = JSON.parseObject(massage.getBody(), StrokeVO.class);
        logger.info("get dead msg:{}",massage.getBody());
        if (null == strokeVO) {
            return;
        }
        try {
            strokeHandler.timeoutHandel(strokeVO);
            //手动确认机制
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
