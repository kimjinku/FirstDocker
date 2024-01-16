package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.ResalePost;
import com.korea.project2_team4.Model.Entity.Tag;
import com.korea.project2_team4.Model.Form.PostForm;
import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Service.*;
import lombok.Builder;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;


@Controller
@Builder
@RequestMapping("/resalePost")
public class ResalePostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ProfileService profileService;
    private final CommentService commentService;
    private final ImageService imageService;
    private final MemberService memberService;
    private final TagService tagService;
    private final TagMapService tagMapService;
    private final RecentSearchService recentSearchService;
    private final ReportService reportService;
    private final ResalePostService resalePostService;

    @GetMapping("/main")
    public String resalePost(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<ResalePost> resalePostList = resalePostService.resalePostList(page);
        model.addAttribute("resalePostList",resalePostList);
        return "resale_main";
    }

    @GetMapping("/detail/{id}/{hit}")
    public String postDetail(Principal principal, Model model, @PathVariable("id") Long id, @PathVariable("hit") Integer hit) {
        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
        if (hit == 0) {
            ResalePost resalePost = resalePostService.getResalePostIncrementView(id);
            model.addAttribute("post", resalePost);
        } else {
            ResalePost resalePost = resalePostService.getResalePost(id);
            model.addAttribute("post", resalePost);
        }
        return "ResalePost/resalePostDetail_form";
    }
}

