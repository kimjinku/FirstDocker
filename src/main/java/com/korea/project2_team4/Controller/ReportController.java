package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.Comment;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.ResalePost;
import com.korea.project2_team4.Model.Entity.Tag;
import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Service.*;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Builder
@RequestMapping("/report")
public class ReportController {

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
    @GetMapping("/posts")
    public String reportedPosts(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Post> reportedPosts = reportService.findPostsLinkedWithReports(page);
        List<Tag> defaultTagList = tagService.getDefaultTags();
        model.addAttribute("defaultTagList", defaultTagList);
        model.addAttribute("paging",reportedPosts);
        return "Member/findReportedPosts_form";
    }
    @GetMapping("/comments")
    public String reportedComments(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Comment> reportedComments = reportService.findCommentsLinkedWithReports(page);
        List<Tag> defaultTagList = tagService.getDefaultTags();
        model.addAttribute("defaultTagList", defaultTagList);
        model.addAttribute("paging",reportedComments);
        return "Member/findReportedComments_form";
    }
    @GetMapping("/resalePosts")
    public String reportedResalePosts(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<ResalePost> reportedPosts = reportService.findResalePostsLinkedWithReports(page);
        List<Tag> defaultTagList = tagService.getDefaultTags();
        model.addAttribute("defaultTagList", defaultTagList);
        model.addAttribute("paging",reportedPosts);
        return "Member/findReportedResalePosts_form";
    }


}
