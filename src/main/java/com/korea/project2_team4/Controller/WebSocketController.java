package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.SendMessage;
import lombok.Builder;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Builder
public class WebSocketController {
    private final SimpUserRegistry userRegistry;
    private Map<String, UserConnectionStatus> userConnectionStatusMap = new HashMap<>();
    @MessageMapping("/connect-status")
    @SendTo("/sub/connect-status")
    public String handleConnectionStatus(SendMessage message) {
        System.out.println("test");
        System.out.println(message.getReceiver());

        String receiverName = message.getReceiver();


        boolean isReceiverConnected = isReceiverConnected(receiverName);
        System.out.println("User2 Connected: " + isReceiverConnected);

//         사용자 2의 연결 상태 확인
//        String user2SessionId = getUserSessionId(receiverName);


//        messagingTemplate.convertAndSendToUser("user2", "/queue/connect-status", message);
//
//
//        if (user2SessionId != null) {
//            // 사용자 2에게 메시지 전송
//            messagingTemplate.convertAndSendToUser("user2", "/queue/connect-status", message);
//        } else {
//            System.out.println("User2 is not connected.");
//            // 사용자 2가 연결되어 있지 않은 경우에 대한 처리
//        }
        return message.getReceiver();
    }

    private void updateUserConnectionStatus(String receiverName, boolean status) {
        userConnectionStatusMap.computeIfAbsent(receiverName, k -> new UserConnectionStatus()).setConnected(status);
    }

    private boolean isReceiverConnected(String userName) {
        UserConnectionStatus userConnectionStatus = userConnectionStatusMap.get(userName);
        return userConnectionStatus != null && userConnectionStatus.isConnected();
    }


//    private String getUserSessionId(String userId) {
//        SimpUser user = userRegistry.getUser(userId);
//        if (user != null) {
//            return user.getSession();
//        } else {
//            return null;
//        }
//    }
}
