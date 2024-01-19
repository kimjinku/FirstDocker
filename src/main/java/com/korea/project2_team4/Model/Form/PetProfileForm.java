package com.korea.project2_team4.Model.Form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PetProfileForm {
    @NotEmpty(message = "이름을 입력하세요.")
    private String name;
    private String content;
    private MultipartFile petImage;
}