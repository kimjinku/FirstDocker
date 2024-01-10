package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name="chatroom")
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName; // 채팅방 이름

    @ManyToOne
    private Member admin; // 관리자

    @OneToMany(mappedBy = "chatroom")
    private Set<MemberChatRoom> memberChatRooms = new HashSet<>();

    @Builder
    public ChatRoom(Long id, String roomName, Member admin) {
        this.id = id;
        this.roomName = roomName;
        this.admin = admin;
    }
    public void changeAdmin(Member admin){
        this.admin = admin;
        admin.getAdminChatRooms().add(this);
    }

}