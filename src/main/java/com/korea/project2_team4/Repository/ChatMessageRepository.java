package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository <ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByTime(Long chatRoomId);

    void deleteByChatRoomId(Long chatRoomId);
}
