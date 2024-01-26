package com.korea.project2_team4.Model.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SaveMessageDTO {
    private Long id;
    private Long authorId;
    private String author;
    private String receiver;
    private String image;
    private String content;
    private LocalDateTime createDate;

}
