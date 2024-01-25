package com.korea.project2_team4.Model.Dto;

import com.korea.project2_team4.Model.Entity.DmPage;
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

    private String content;
    private String author;
    private String receiver;
    private String image;
    private LocalDateTime createDate;

    @ManyToOne
    private DmPage dmPage;


    public SaveMessage() {
    }

    public SaveMessage(String content, String author, String receiver, String image, LocalDateTime timenow, DmPage dmPage) {

        this.content = content;
        this.author = author;
        this.receiver = receiver;
        this.image = image;
        this.createDate = timenow;
        this.dmPage = dmPage;
    }


}