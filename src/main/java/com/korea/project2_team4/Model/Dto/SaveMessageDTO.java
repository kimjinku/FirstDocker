package com.korea.project2_team4.Model.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SaveMessageDTO {

    private String author;
    private String receiver;
    private String content;
    private LocalDateTime createDate;

}
