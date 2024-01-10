package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.ChatDTO;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Service.ChatService;
import com.korea.project2_team4.Service.MemberService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// 채팅을 수신(sub) 하고 송신(pub) 하기위한 Controller
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {
    private final SimpMessageSendingOperations template;

    private final ChatService chatService;
    private final MemberService memberService;



    @PostMapping("/createRoom")
    public String createRoom(Model model, Principal principal, @RequestParam("roomName") String roomName) {
        ChatRoom chatRoom = chatService.createChatRoom(roomName, principal);
        model.addAttribute("chatRoom", chatRoom);
        return "redirect:/chat/chatRoomList";
    }

    @GetMapping("/chatRoomList")
    public String showChatRoomList(Model model) {
        List<ChatRoomListResponseDto> chatRooms = chatService.findAllRoom();

        model.addAttribute("list", chatRooms);
        log.info("SHOW ALL ChatList{}", chatRooms);

        return "Chat/chatList_form";
    }

    @GetMapping("/chatRoom/{id}")
    public String goChatRoom(Model model, @PathVariable("id") Long id) {

        return "Chat/chatRoom_form";
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDTO chat) {
        log.info("Chat {}", chat);

        chat.setMessage(chat.getMessage());

        template.convertAndSend("/sub/chat/room?roomId=" + chat.getRoomId(), chat);

    }




}

