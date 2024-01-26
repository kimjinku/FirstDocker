package com.korea.project2_team4.Model.Entity;

import com.korea.project2_team4.Service.ReportService;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private List <String> category;

    private String content;

    @ManyToOne
//    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne
//    @JoinColumn(name="comment_id")
    private Comment comment;

    @ManyToOne
    private ResalePost resalePost;

    @ManyToOne
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    private LocalDateTime reportDate;




}
