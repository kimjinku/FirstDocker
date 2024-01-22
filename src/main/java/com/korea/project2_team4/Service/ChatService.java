package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Dto.ChatDTO;
//import com.korea.project2_team4.Model.Dto.ChatRoom;
import com.korea.project2_team4.Model.Dto.ChatImageDto;
import com.korea.project2_team4.Model.Dto.ChatMessageResponseDto;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatMessage;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Model.Entity.MemberChatRoom;
import com.korea.project2_team4.Repository.*;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class ChatService {
    private Map<String, ChatRoom> chatRoomMap;
    private final MemberService memberService;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    // 전체 채팅방 조회
    // 채팅방 생성 순서를 최근 순으로 반환
    public List<ChatRoomListResponseDto> findAllRoom(Principal principal) {
        Member member = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<ChatRoomListResponseDto> chatRooms = new ArrayList<>();

        chatRoomRepository.findAll().forEach(chatRoom -> {
            chatRooms.add(ChatRoomListResponseDto.builder()
                    .id(chatRoom.getId())
                    .roomName(chatRoom.getRoomName())
                    .adminName(chatRoom.getAdmin().getUserName())
                    .memberCount(chatRoom.getMemberChatRooms().size())
                    .maxMember(chatRoom.getMaxMember())
                    .inChatRoom(memberChatRoomRepository.findByChatroomAndMember(chatRoom, member).isPresent())
                    .build());
        });
        Collections.reverse(chatRooms);
        return chatRooms;
    }


    // 채팅방 만들 때 관리자와 방 번호, 이름 생성, 비빌번호 설정, 인원 수 설정
    public ChatRoom createChatRoom(String roomName, String password, int maxMember, Principal principal) {

        Member admin = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        System.out.println(admin);
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .admin(admin)
                .password(passwordEncoder.encode(password))
                .maxMember(maxMember)
                .build();

        chatRoomRepository.save(chatRoom);

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .chatroom(chatRoom)
                .member(admin)
                .build();

        memberChatRoomRepository.save(memberChatRoom);

        return chatRoom;
    }

    public void enterChatRoom(Long roomId, String password, Principal principal) {

        Member member = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        if (passwordEncoder.matches(password, chatRoom.getPassword())) {

            boolean isUserInChatRoom = chatRoom.getMemberChatRooms().stream()
                    .anyMatch(memberChatRoom -> memberChatRoom.getMember().getId().equals(member.getId()));

            if (!isUserInChatRoom) {
                if (chatRoom.getMemberChatRooms().size() + 1 <= chatRoom.getMaxMember()) {
                    MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                            .chatroom(chatRoom)
                            .member(member)
                            .build();

                    memberChatRoomRepository.save(memberChatRoom);
                } else {
                    throw new RuntimeException("채팅 방이 가득 찼습니다..");
                }

            }
        } else {
            throw new RuntimeException("비밀번호가 틀립니다.");
        }

    }

    @Transactional
    public ChatMessageResponseDto saveChatMessage(ChatDTO chatDTO, Principal principal) {
        Member sender = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        MemberChatRoom chatRoom = memberChatRoomRepository.findById(Long.parseLong(chatDTO.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));


        String img;

        if (chatDTO.getImage() != null) {
            ChatImageDto chatImage = chatDTO.getImage();
            // 파일명 생성
            String fileName =  s3Service.generateChatImageName(chatDTO.getRoomId(), chatDTO.getSender(),chatDTO.getTime(), chatImage.getContentType());
            // s3에 이미지를 업로드하고 그 이미지 path 경로를 img에 저장
            img = s3Service.uploadToS3(fileName, chatImage.getData(), chatImage.getContentType());
        } else
            img = null;

        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatDTO.getMessage())
                .sender(sender)
                .chatRoom(chatRoom)
                .time(LocalDateTime.now())
                // 그 경로를 image db에 저장
                .image(img)
                .build();

        chatMessageRepository.save(chatMessage);

        ChatMessageResponseDto chatMessageResponseDto = ChatMessageResponseDto.builder()
                .message(chatDTO.getMessage())
                .sender(chatDTO.getSender())
                .time(chatDTO.getTime())
                .image(img)
                .build();
        return chatMessageResponseDto;
    }

    public Map<String, Object> showChatDate(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        List<Member> members = chatRoom.getMemberChatRooms()
                .stream()
                .map(MemberChatRoom::getMember)
                .collect(Collectors.toList());

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTime(chatRoomId);


        Map<String, Object> chatData = new HashMap<>();
        chatData.put("members", members);
        chatData.put("messages", messages);

        return chatData;

    }

    public ChatRoom findChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    public void deleteChatRoom(Long id) {
        chatRoomRepository.deleteById(id);
    }
}