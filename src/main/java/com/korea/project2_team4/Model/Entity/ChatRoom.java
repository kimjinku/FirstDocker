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

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.REMOVE)
    private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

    private String password;

    @Builder
    public ChatRoom(Long id, String roomName, Member admin, String password) {
        this.id = id;
        this.roomName = roomName;
        this.admin = admin;
        this.password = password;
    }
    public void changeAdmin(Member admin){
        this.admin = admin;
        admin.getAdminChatRooms().add(this);
    }

}