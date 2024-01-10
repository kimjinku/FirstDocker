package com.korea.project2_team4.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korea.project2_team4.Model.Dto.ChatDTO;
//import com.korea.project2_team4.Model.Dto.ChatRoom;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatMessage;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Model.Entity.MemberChatRoom;
import com.korea.project2_team4.Repository.ChatRepository;
import com.korea.project2_team4.Repository.ChatRoomRepository;
import com.korea.project2_team4.Repository.MemberChatRoomRepository;
import com.korea.project2_team4.Repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Data
@Service
public class ChatService {
    private Map<String, ChatRoom> chatRoomMap;
    private final MemberService memberService;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    // 전체 채팅방 조회
    // 채팅방 생성 순서를 최근 순으로 반환
    public List<ChatRoomListResponseDto> findAllRoom() {
        List<ChatRoomListResponseDto> chatRooms = new ArrayList<>();

        chatRoomRepository.findAll().forEach(chatRoom -> {
            chatRooms.add(ChatRoomListResponseDto.builder()
                    .id(chatRoom.getId())
                    .roomName(chatRoom.getRoomName())
                    .adminName(chatRoom.getAdmin().getUserName())
                    .build());
        });
        Collections.reverse(chatRooms);
        return chatRooms;
    }


    // roomName 으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, Principal principal) {

        Member admin = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        System.out.println(admin);
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .admin(admin)
                .build();

        chatRoomRepository.save(chatRoom);

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .chatroom(chatRoom)
                .member(admin)
                .build();

        memberChatRoomRepository.save(memberChatRoom);

        return chatRoom;
    }
    public void saveChatMessage(ChatDTO chatDTO) {
        //ChatDTO 에서 필요한 정보를 추출하여 ChatMessage 엔티티에 저장

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(chatDTO.getMessage());

        Optional<Member> optionalSender = memberRepository.findByUserName(chatDTO.getSender());
        Member sender = optionalSender.orElseThrow(() -> new RuntimeException("Member not found"));
        chatMessage.setSender(sender);


        chatMessage.setTime(LocalDateTime.parse(chatDTO.getTime()));

        chatRepository.save(chatMessage);
    }

}