package com.heima.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

//TODO:任务4.1-Rabbitmq配置
@Configuration
public class RabbitConfig {
    /**
     * 延迟时间 单位毫秒
     */
    private static final long DELAY_TIME = 1000 * 30;


    //行程超时队列
    public static final String STROKE_OVER_QUEUE = "STROKE_OVER_QUEUE";
    //行程死信队列
    public static final String STROKE_DEAD_QUEUE = "STROKE_DEAD_QUEUE";

    //行程超时队列交换器
    public static final String STROKE_OVER_QUEUE_EXCHANGE = "STROKE_OVER_QUEUE_EXCHANGE";

    //行程死信队列交换器
    public static final String STROKE_DEAD_QUEUE_EXCHANGE = "STROKE_DEAD_QUEUE_EXCHANGE";
    //行程超时交换器 ROUTINGKEY
    public static final String STROKE_OVER_KEY = "STROKE_OVER_KEY";

    //行程死信交换器 ROUTINGKEY
    public static final String STROKE_DEAD_KEY = "STROKE_DEAD_KEY";



    /**
     * 声明行程超时队列
     *
     * @return
     */
    @Bean
    public Queue strokeOverQueue() {
        //【重要配置】超时队列配置，死信队列的绑定在该方法中实现
        //需要用到以下属性：

        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机

        // x-dead-letter-routing-key  这里声明当前队列的死信路由key

        // x-message-ttl  声明队列的TTL

        return null;
    }


    /**
     * 声明行程死信队列
     *
     * @return
     */
    @Bean
    public Queue strokeDeadQueue() {
        return null;
    }

    /**
     * 创建行程超时队列交换器
     *
     * @return
     */
    @Bean
    DirectExchange strokeOverQueueExchange() {
        return null;
    }

    /**
     * 创建行程死信队列交换器
     *
     * @return
     */
    @Bean
    DirectExchange strokeDeadQueueExchange() {
        return null;
    }



    /**
     * 行程超时队列绑定
     *
     * @return
     */
    @Bean
    Binding bindingStrokeOverDirect() {
        return null;
    }

    /**
     * 行程死信队列绑定
     *
     * @return
     */
    @Bean
    Binding bindingStrokeDeadDirect() {
        return null;
    }



}
