package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.ChatDTO;
import com.korea.project2_team4.Model.Dto.ChatMessageResponseDto;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatMessage;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Service.ChatService;
import com.korea.project2_team4.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public String createRoom(Model model, Principal principal, @RequestParam("roomName") String roomName, @RequestParam("password") String password, @RequestParam("maxMember") int maxMember) {
        ChatRoom chatRoom = chatService.createChatRoom(roomName, password, maxMember,principal);
        model.addAttribute("chatRoom", chatRoom);
        return "redirect:/chat/chatRoomList";
    }

    @GetMapping("/chatRoomList")
    public String showChatRoomList(Model model, Principal principal) {
        List<ChatRoomListResponseDto> chatRooms = chatService.findAllRoom(principal);

        model.addAttribute("list", chatRooms);
        log.info("SHOW ALL ChatList{}", chatRooms);

        return "Chat/chatList_form";
    }

    @GetMapping("/searchChatRoom")
    public String searchChatRoom(@RequestParam("roomId") Long roomId, Model model, Principal principal) {

        List<ChatRoomListResponseDto> searchResult = chatService.searchRoomsById(roomId, principal);

        model.addAttribute("searchResult", searchResult);
        return "Chat/searchChatList_form";
    }




    @PostMapping("/chatRoom")
    public String goChatRoom(Model model,Principal principal, @RequestParam("roomId") Long id,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "inChat") Boolean inChat) {
        System.out.println("Received roomId: " + id);

        if (!inChat) {
            chatService.enterChatRoom(id, password, principal);
        }

        ChatRoom chatRoomId = chatService.findChatRoomById(id);

        Map<String, Object> chatData = chatService.showChatDate(id);

        model.addAttribute("chatRoomId", id);
        model.addAttribute("chatRoomName", chatRoomId.getRoomName());

        model.addAttribute("members", chatData.get("members"));
        model.addAttribute("messages", chatData.get("messages"));

        return "Chat/chatRoom_form";
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO, Principal principal) {

        try {

            chatDTO.setSender(principal.getName());

            ChatMessageResponseDto chatMessageResponseDto = chatService.saveChatMessage(chatDTO, principal);
            template.convertAndSend("/sub/chat/chatRoom/id/" + chatDTO.getRoomId(), chatMessageResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/deleteChatRoom/{id}")
    public String deleteChatRoom(@PathVariable Long id) {
        chatService.deleteChatRoom(id);
        return "redirect:/chat/chatRoomList";
    }

    @PostMapping("/leaveChatRoom/{id}")
    public String leaveChatRoom(@PathVariable Long id, Principal principal) {
        chatService.leaveChatRoom(id, principal);

        return "redirect:/chat/chatRoomList";
    }

    @GetMapping("/myChatRoom")
    public String myChatRoom(Model model, Principal principal) {
        List<ChatRoomListResponseDto> myChatRooms = chatService.findMyChatRooms(principal);
        model.addAttribute("myChatRooms", myChatRooms);
        return "Chat/myChatRoom_form";
    }

}

