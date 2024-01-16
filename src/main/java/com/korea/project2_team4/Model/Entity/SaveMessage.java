package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SaveMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    private Profile user;
    private String author;
    private String receiver;


    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private DmPage dmPage;


    public SaveMessage() {
    }

    public SaveMessage(String content, String author, String receiver,LocalDateTime timenow, DmPage dmPage) {

        this.content = content;
        this.author = author;
        this.receiver = receiver;
        this.createDate = timenow;
        this.dmPage = dmPage;
    }


}