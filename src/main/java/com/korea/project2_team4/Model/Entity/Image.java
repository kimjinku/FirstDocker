package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName; //파일 원본명

    private String saveName;// 파일명을 랜덤화 한 저장할 때 쓰는 이름

    private String filePath; //파일 저장 경로

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    private Post postImages;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "resalePost_id")
    private ResalePost resalePostImages;

    @OneToOne
    private Profile profileImage;

    @OneToOne
    private Pet petImage;

}
