package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    private Member sender;

    private LocalDateTime time;

    @ManyToOne
    private MemberChatRoom chatRoom;

    @Builder
    public ChatMessage (Long id, String message, Member sender, LocalDateTime time, MemberChatRoom chatRoom) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.time = time;
        this.chatRoom = chatRoom;
    }

}
