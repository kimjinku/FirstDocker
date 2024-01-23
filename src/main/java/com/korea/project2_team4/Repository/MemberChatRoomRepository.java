package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

    Optional<MemberChatRoom> findByChatroomAndMember(ChatRoom chatRoom, Member member);

    Iterable<MemberChatRoom> findByMember(Member member);
}
