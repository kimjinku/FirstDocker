package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
