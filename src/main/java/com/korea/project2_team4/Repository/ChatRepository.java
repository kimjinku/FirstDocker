package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository <ChatMessage, Long> {
}
