package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String user;

    private String content;

    private LocalDateTime createDate;



    public SaveMessage() {
    }

    public SaveMessage(String content, String user, LocalDateTime timenow) {

        this.content = content;
        this.user = user;
        this.createDate = timenow;
    }


}