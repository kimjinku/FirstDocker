package com.korea.project2_team4;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.web.socket.config.annotation.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint( "/ws-stomp") // 여기는 연결된 엔드포인드를 말합니다.
//                .withSockJS(); // SockJS를 연결한다는 설정입니다.
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/sub"); //메시지를 구독하는 요청 url 입니다.
//        registry.setApplicationDestinationPrefixes("/pub"); //메시지를 발행하는 요청 url 입니다.
//    }



    // 간단한 설명
    // WebSocketConfig 는 엔드포인트를 설정하기 위해 만든 config 클래스입니다.
    // 여기서 엔드포인트는 통신의 도착지점을 말합니다.
    // 즉 통신이 어떤 엔드포인트에 도착 했을 때 어떤 행위를 하게 만들 것이다라는 것입니다.
    // 위에 처럼 엔드 포인틀를 /ws-stomp 로 설정해두면 웹소켓 통신이 /ws-stomp 로 도착할 때 우리는
    // 해당 통신이 웹 소켓 통신 중에서 stomp 통신인 것을 확인하고, 이를 연결 한다는 의미 입니다.

//////////////////////////황선영추가. 추후 위코드와 병합할 예정.////////////////////////////////////////


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint( "/aaa")
                .setAllowedOriginPatterns("*").withSockJS();
    }

}
