package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String image;

    private LocalDateTime time;

    @ManyToOne
    private MemberChatRoom chatRoom;

    @OneToMany(mappedBy = "chatImages", cascade = CascadeType.REMOVE)
    private List<Image> chatImages = new ArrayList<>();

    @Builder
    public ChatMessage (Long id, String message, Member sender,  String image, LocalDateTime time, MemberChatRoom chatRoom) {
        this.id = id;
        this.message = message;
        this.image = image;
        this.sender = sender;
        this.time = time;
        this.chatRoom = chatRoom;
    }

}
