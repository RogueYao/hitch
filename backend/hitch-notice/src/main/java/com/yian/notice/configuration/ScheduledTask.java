package com.yian.notice.configuration;


import com.alibaba.fastjson.JSON;
import com.yian.modules.po.NoticePO;
import com.yian.notice.service.NoticeService;
import com.yian.notice.socket.WebSocketServer;
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
            //获取所有在线的用户accountId，提示：WebSocketServer里有用户链接的池子
            Set<String> strings = WebSocketServer.sessionPools.keySet();
            //在MongoDB中获取需要推送的消息，noticeService里的方法研究一下，可以帮到你
            List<NoticePO> noticeByAccountIds = noticeService.getNoticeByAccountIds(new ArrayList<>(strings));
            //遍历所有消息，逐个发送消息到浏览器
            //方法：session.getBasicRemote().sendText(json);
            for (NoticePO noticePO : noticeByAccountIds) {
                Session session = WebSocketServer.sessionPools.get(noticePO.getReceiverId());
                if (session != null && session.isOpen()) {
                    try {
                        String json = JSON.toJSONString(noticePO);
                        session.getBasicRemote().sendText(json);
                    } catch (IOException e) {
                        logger.error("Failed to send message to user: {}", noticePO.getReceiverId(), e);
                    }
                }else {
                    logger.error("Failed to send message to user: {}", noticePO.getReceiverId());
                }
            }

        }, 0,1 , TimeUnit.SECONDS);
    }
    // private void pushUnreadMessages() {
    //     // 获取所有在线的用户accountId
    //     Map<String, Session> onlineSessions = WebSocketServer.getOnlineSessions();
    //     Set<String> accountIds = onlineSessions.keySet();
    //
    //     // 在MongoDB中获取需要推送的消息
    //     List<Notice> unreadNotices = noticeService.getUnreadNotices(accountIds);
    //
    //     // 遍历所有消息，逐个发送消息到浏览器
    //     for (Notice notice : unreadNotices) {
    //         String accountId = notice.getAccountId();
    //         Session session = onlineSessions.get(accountId);
    //         if (session != null && session.isOpen()) {
    //             try {
    //                 String json = new ObjectMapper().writeValueAsString(notice);
    //                 session.getBasicRemote().sendText(json);
    //             } catch (IOException e) {
    //                 logger.error("Failed to send message to user: {}", accountId, e);
    //             }
    //         }
    //     }
    // }
}
