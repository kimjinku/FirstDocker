package com.korea.project2_team4.Controller;


import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Service.PostService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Builder
public class MainController {
    private final PostService postService;
    @GetMapping("/")
    public String root(Model model) {
        List<Post> postList = postService.getPostslikes();

        model.addAttribute("postList", postList);
        return "main_form";
    }


}
