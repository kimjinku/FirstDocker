package com.korea.project2_team4.Controller;

import lombok.Builder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Builder
@RequestMapping("/community")
public class CommunityController {

    @GetMapping("/main")
    public String main() {

        return "community_main";
    }
}
