package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private boolean admin;
    // 다대일 관계 설정

    @ManyToOne
    private Member member;

    // 다대일 관계 설정
    @ManyToOne
    private ChatRoom chatroom;

    @Builder
    public MemberChatRoom(Long id, boolean admin, Member member, ChatRoom chatroom){
        this.id = id;
        this.admin = admin;
        if (member != null) {
            this.changeMember(member);
        }

        if (chatroom != null) {
            this.changeChatRoom(chatroom);
        }
    }

    public void changeMember(Member member){
        this.member = member;
        member.getMemberChatRooms().add(this);
    }
    public void changeChatRoom(ChatRoom chatroom){
        this.chatroom = chatroom;
        chatroom.getMemberChatRooms().add(this);
    }
}
