package com.heima.notice.configuration;


import com.alibaba.fastjson.JSON;
import com.heima.modules.po.NoticePO;
import com.heima.notice.service.NoticeService;
import com.heima.notice.socket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务 推送暂存消息
 */
@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private NoticeService noticeService;

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        //TODO:任务5.2-推送未读消息
        //定时调度，获取mongodb里的未读消息，推送给对应用户
        executorService.scheduleAtFixedRate(() -> {
            //获取最新需要推送的消息
            Set<String> accountKeys = WebSocketServer.sessionPools.keySet();
//            logger.info("msg task working,inline accounts:{}",accountIds);
            if (accountKeys == null || accountKeys.isEmpty()){
                return;
            }
            //在MongoDB中获取当前在线用户的暂存消息
            List<String> accountIds = new ArrayList<>(accountKeys.size());
            accountIds.addAll(accountKeys);
            List<NoticePO> pushMessagesList = noticeService.getNoticeByAccountIds(accountIds);
            //校验消息
            if (null != pushMessagesList && !pushMessagesList.isEmpty()) {
                logger.debug("推送消息线程工作中,推送数据条数:{}", pushMessagesList.size());
                //推送消息
                for (NoticePO noticePO : pushMessagesList) {
                    //获取当前会话
                    Session session = WebSocketServer.sessionPools.get(noticePO.getReceiverId());
                    if (null != session && null != noticePO) {
                        //获取消息体
                        try {
                            session.getBasicRemote().sendText(JSON.toJSONString(noticePO));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
            logger.debug("推送消息线程工作中,推送数据条数:{}", 0);
        }, 0,1 , TimeUnit.SECONDS);
    }

}
