package com.korea.project2_team4.Model.Form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ResalePostForm {

    private String title;

    private String name;

    private String content;

    private String price;

    private String category;

    private List<MultipartFile> fileList; // 이미지 업로드를 위한 필드
}
