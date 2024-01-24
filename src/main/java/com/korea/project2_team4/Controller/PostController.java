package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Model.Form.PostForm;
import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Repository.ReportRepository;
import com.korea.project2_team4.Service.*;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@Builder
@RequestMapping("/post")
public class PostController {

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


    @GetMapping("/main")
    public String main() {

        return "community_main";
    }

    @GetMapping("/createPost")
    public String createPost(Model model, PostForm postForm, HttpSession session) {
        List<Tag> allTags = tagService.getAllTags();

        // 세션에서 petName을 가져와 모델에 추가
        String petName = (String) session.getAttribute("petName");
        if (petName != null) {
            model.addAttribute("petName", petName);
            // 세션에서 "petName" 속성 제거
            session.removeAttribute("petName");
        }


        model.addAttribute("allTags", allTags);
        return "Post/createPost_form";
    }

    //테스트 데이터
    @GetMapping("/TestPost")
    public String saveTestPost() {
        postService.saveTestPost();

        return "redirect:/";

    }




    @PreAuthorize("isAuthenticated()")
    @PostMapping("/createPost")
    public String createPost(Principal principal, PostForm postForm, BindingResult bindingResult,
                             @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                             @RequestParam(value = "selectedTagNames", required = false) List<String> selectedTagNames,
                             @RequestParam(value = "newTagNames", required = false) List<String> newTagNames,
                             @RequestParam(value = "petTagName", required = false) List<String> petTagNames) throws IOException, NoSuchAlgorithmException {
        //      Profile testProfile = profileService.getProfilelist().get(0);
//      profileService.updateprofile(testProfile,profileForm.getProfileName(),profileForm.getContent());
        Post post = new Post();
//        System.out.println(imageFiles.size());
        Member sitemember = this.memberService.getMember(principal.getName());
        System.out.println("회원 권환 : "+ sitemember.getRole());
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setCreateDate(LocalDateTime.now());
        post.setAuthor(sitemember.getProfile());
        post.setCategory(postForm.getCategory());
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageService.uploadPostImage(imageFiles, post);
        }
        if (newTagNames != null && !newTagNames.isEmpty()) {

            for (String newTagName : newTagNames) {
                if (!tagService.tagExists(newTagName)) {
                    Tag tag = new Tag();
                    tag.setName(newTagName);
                    tagService.save(tag);
                }
            }
        }
        postService.save(post);

        if (selectedTagNames != null && !selectedTagNames.isEmpty()) {
            for (String selectedTagName : selectedTagNames) {
                Tag tag = tagService.getTagByTagName(selectedTagName);
                TagMap tagMap = new TagMap();
                tagMap.setPost(post);
                tagMap.setTag(tag);
                tagMapService.save(tagMap);
//                if (newTagNames != null && !newTagNames.isEmpty()) {
//                    for (String newTagName : newTagNames) {
//                        tagService.deleteById(tagService.getTagByTagName(newTagName).getId());
//                    }
//                }
            }
        }
        if (petTagNames !=null && !petTagNames.isEmpty()) {
            for (String petTagName : petTagNames) {
                Tag tag = new Tag();
                tag.setName(petTagName);
                tagService.save(tag);

                TagMap tagMap = new TagMap();
                tagMap.setPost(post);
                tagMap.setTag(tag);
                tagMapService.save(tagMap);
            }
        }

        String encodedCategory;
        try {
            encodedCategory = URLEncoder.encode(postForm.getCategory(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 예외 처리 필요
            encodedCategory = "";
        }

        return String.format("redirect:/post/community/main?category=%s&sort=%s&TagName=%s", encodedCategory, "", "");
    }

    @GetMapping("/community/main")
    public String communityMain(Principal principal, Model model, @RequestParam(name = "category", required = false) String category,
                                @RequestParam(name = "sort", required = false) String sort,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(name = "TagName", required = false) String TagName) {

        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
        Page<Post> allPosts;
        allPosts = postService.postList(page);

        List<Tag> defaultTagList = tagService.getDefaultTags();

        if (category.equals("QnA")) {
            Page<Post> qnaPosts = postService.getPostsQnA(page, sort, TagName);

            model.addAttribute("category", category);
            model.addAttribute("TagName", TagName);
            model.addAttribute("sort", sort);
            model.addAttribute("paging", qnaPosts);
            model.addAttribute("defaultTagList", defaultTagList);
            return "community_main";

        } else if (category.equals("자유게시판")) {

            Page<Post> freeboardPosts = postService.getPostsFreeboard(page, sort, TagName);

            model.addAttribute("category", category);
            model.addAttribute("TagName", TagName);
            model.addAttribute("sort", sort);
            model.addAttribute("paging", freeboardPosts);
            model.addAttribute("defaultTagList", defaultTagList);
            return "community_main";
        } else {
            Page<Post> posts = postService.getAllPosts(page, sort, TagName);

            model.addAttribute("category", category);
            model.addAttribute("TagName", TagName);
            model.addAttribute("sort", sort);
            model.addAttribute("paging", posts);
            model.addAttribute("defaultTagList", defaultTagList);
            return "community_main";
        }

    }


    @GetMapping("/search")
    public String searchPosts(@RequestParam(value = "kw", defaultValue = "") String kw, Model model) {

        List<Post> searchResultsByPostTitle = postService.searchPostTitle(kw);
        List<Post> searchResultsByPostContent = postService.searchPostContent(kw);
        List<Post> searchResultsByProfileName = postService.searchProfileName(kw);
        List<Post> searchResultsByCommentContent = postService.searchCommentContent(kw);
        List<String> recentSearchKeywords = recentSearchService.getRecentSearchKeywords();

        Collections.reverse(searchResultsByPostTitle);
        Collections.reverse(searchResultsByPostContent);
        Collections.reverse(searchResultsByProfileName);
        Collections.reverse(searchResultsByCommentContent);

        searchResultsByPostTitle = searchResultsByPostTitle.subList(0, Math.min(5, searchResultsByPostTitle.size()));
        searchResultsByPostContent = searchResultsByPostContent.subList(0, Math.min(5, searchResultsByPostContent.size()));
        searchResultsByProfileName = searchResultsByProfileName.subList(0, Math.min(5, searchResultsByProfileName.size()));
        searchResultsByCommentContent = searchResultsByCommentContent.subList(0, Math.min(5, searchResultsByCommentContent.size()));

        model.addAttribute("searchResultsByPostTitle", searchResultsByPostTitle);
        model.addAttribute("searchResultsByPostContent", searchResultsByPostContent);
        model.addAttribute("searchResultsByProfileName", searchResultsByProfileName);
        model.addAttribute("searchResultsByCommentContent", searchResultsByCommentContent);
        model.addAttribute("recentSearchKeywords", recentSearchKeywords);
        model.addAttribute("kw", kw);

        recentSearchService.saveRecentSearch(kw);

        return "search_form";
    }

    @GetMapping("/showMoreTitle")
    public String showMoreTitle(@RequestParam(value = "kw", required = false) String kw,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {
        Page<Post> pagingByTitle = postService.pagingByTitle(kw, page);

//        model.addAttribute("pagingByTitle", pagingByTitle);
//        model.addAttribute("kw", kw);
//
//        return "Search/showMoreTitle_form";
        model.addAttribute("searchfor", "제목 검색 결과 조회");
        model.addAttribute("pagingBy", pagingByTitle);
        model.addAttribute("kw", kw);
        return "Search/showMore";
    }

    @GetMapping("/showMoreContent")
    public String showMoreContents(@RequestParam(value = "kw", defaultValue = "") String kw,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   Model model) {
        Page<Post> pagingByContent = postService.pagingByContent(kw, page);


//        model.addAttribute("pagingByContent", pagingByContent);
//        model.addAttribute("kw", kw);
//
//        return "Search/showMoreContent_form";

        model.addAttribute("searchfor", "내용 검색 결과 조회");
        model.addAttribute("pagingBy", pagingByContent);
        model.addAttribute("kw", kw);
        return "Search/showMore";
    }

    @GetMapping("/showMoreProfileName")
    public String showMoreProfileNames(@RequestParam(value = "kw", defaultValue = "") String kw,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       Model model) {
        Page<Post> pagingByProfileName = postService.pagingByProfileName(kw, page);

//        model.addAttribute("pagingByProfileName", pagingByProfileName);
//        model.addAttribute("kw", kw);
//
//        return "Search/showMoreProfileName_form";

        model.addAttribute("searchfor", "이름 검색 결과 조회");
        model.addAttribute("pagingBy", pagingByProfileName);
        model.addAttribute("kw", kw);
        return "Search/showMore";
    }

    @GetMapping("/showMoreComment")
    public String showMoreComments(@RequestParam(value = "kw", defaultValue = "") String kw,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   Model model) {
        Page<Post> pagingByComment = postService.pagingByComment(kw, page);

//        model.addAttribute("pagingByComment", pagingByComment);
//        model.addAttribute("kw", kw);
//
//        return "Search/showMoreComment_form";
        model.addAttribute("searchfor", "댓글 검색 결과 조회");
        model.addAttribute("pagingBy", pagingByComment);
        model.addAttribute("kw", kw);
        return "Search/showMore";
    }

    @GetMapping("/detail/{id}/{hit}")
    public String postDetail(Principal principal,HttpSession session, Model model, @PathVariable("id") Long id, @PathVariable("hit") Integer hit, PostForm postForm) {
        List<Tag> getPostTags = tagService.getTagListByPost(postService.getPost(id));
        List<Tag> allTags = tagService.getAllTags();

        List<Comment> commentList = postService.getPost(id).getComments();
        List<Comment> parentComments = new ArrayList<>();
        for (Comment c : commentList) {
            if (c.getParentComment() == null) {
                parentComments.add(c);
            }
        }


        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
        if (hit == 0) {
            Post post = postService.getPostIncrementView(id);
            model.addAttribute("post", post);
        } else {
            Post post = postService.getPost(id);
            model.addAttribute("post", post);
        }
        if (principal != null) {
            String username = principal.getName();
            // 이미 해당 사용자가 신고한 게시물인지 확인
            if (reportService.isAlreadyPostReported(id, username)) {
                // 이미 신고한 경우, 모델에 메시지 추가
                model.addAttribute("alreadyPostReportedMessage", "이미 신고한 게시물입니다.");
                return "Post/postDetail_form"; // 리다이렉트할 뷰 경로
            }
        }

        // 세션에서 anchorId를 가져옴
        if (session!=null) {
//            String anchorId = (String) session.getAttribute("anchorId");
            model.addAttribute("anchorId", session.getAttribute("anchorId"));
            System.out.println(session.getAttribute("anchorId"));
            // 사용이 끝난 데이터는 세션에서 제거할 수도 있음
            session.removeAttribute("anchorId");
        }

        model.addAttribute("parentComments", parentComments);
        model.addAttribute("getPostTags", getPostTags);
        model.addAttribute("allTags", allTags);
        model.addAttribute("postForm", postForm);

        return "Post/postDetail_form";
    }

    @PostMapping("/postLike")
    public String postLike(Principal principal, @RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            Post post = this.postService.getPost(id);
            Member member = this.memberService.getMember(principal.getName());
            Long postId = post.getId();
            boolean isChecked = false;
            if (postService.isLiked(post, member)) {
                postService.unLike(post, member);
            } else {
                postService.Like(post, member);
                isChecked = true;
            }
            redirectAttributes.addFlashAttribute("isChecked", isChecked);
            return "redirect:/post/detail/" + postId + "/1";
        } else {
            return "redirect:/member/login";
        }
    }

    @PostMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {

        String encodedCategory;
        try {
            encodedCategory = URLEncoder.encode(postService.getPost(id).getCategory(), "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // 예외 처리 필요
            encodedCategory = "";
        }
        postService.deleteById(id);

        return String.format("redirect:/post/community/main?category=%s&sort=%s&TagName=%s", encodedCategory, "", "");
    }
    @PostMapping("/deleteReportedPost/{id}")
    public String deleteReportedPost(@PathVariable("id") Long id) {
        String encodedCategory;
        try {
            encodedCategory = URLEncoder.encode(postService.getPost(id).getCategory(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 예외 처리 필요
            encodedCategory = "";
        }
        postService.deleteById(id);

        return "redirect:/report/posts";
    }

    @GetMapping("/updatePost/{id}")
    public String updatePost(Principal principal, Model model, @PathVariable("id") Long id) {
        List<Tag> getPostTags = tagService.getTagListByPost(postService.getPost(id));
        model.addAttribute("getPostTags", getPostTags);
        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }

        Post post = postService.getPost(id);
        model.addAttribute("post", post);

        return "Post/postUpdate_form";
    }
    @PostMapping("/updatePostByAdmin/{id}")
    public String updatePostByAdmin(@PathVariable("id") Long id, @ModelAttribute Post updatePost,
                             @RequestParam(value = "selectedTagNames", required = false) List<String> selectedTagNames
    ) throws IOException, NoSuchAlgorithmException {

        Post post = new Post();
        Post existingPost = postRepository.findById(id).orElse(null);

        if (existingPost != null) {
            existingPost.setModifyDate(LocalDateTime.now());
            existingPost.setCategory(updatePost.getCategory());
            tagMapService.deleteTagMapsByPostId(id);

            if (selectedTagNames != null && !selectedTagNames.isEmpty()) {
                for (String selectedTagName : selectedTagNames) {
                    Tag tag = tagService.getTagByTagName(selectedTagName);
                    TagMap tagMap = new TagMap();
                    tagMap.setPost(existingPost);
                    tagMap.setTag(tag);
                    tagMapService.save(tagMap);
                }
            }
            existingPost.setCategory(updatePost.getCategory());

            postRepository.save(existingPost);
        }

        return "redirect:/post/detail/{id}/1";
    }

    @PostMapping(value = "/updatePost/{id}", consumes = {"multipart/form-data"})
    public String updatePost(@PathVariable("id") Long id, @ModelAttribute Post updatePost,
                             @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                             @RequestParam(value = "selectedTagNames", required = false) List<String> selectedTagNames
    ) throws IOException, NoSuchAlgorithmException {

        Post post = new Post();
        Post existingPost = postRepository.findById(id).orElse(null);

        if (existingPost != null) {
            existingPost.setTitle(updatePost.getTitle());
            existingPost.setContent(updatePost.getContent());
            existingPost.setModifyDate(LocalDateTime.now());
            existingPost.setCategory(updatePost.getCategory());
            tagMapService.deleteTagMapsByPostId(id);

            if (imageFiles != null && !imageFiles.isEmpty()) {
                imageService.uploadPostImage(imageFiles, existingPost);
            }

            if (selectedTagNames != null && !selectedTagNames.isEmpty()) {
                for (String selectedTagName : selectedTagNames) {
                    Tag tag = tagService.getTagByTagName(selectedTagName);
                    TagMap tagMap = new TagMap();
                    tagMap.setPost(existingPost);
                    tagMap.setTag(tag);
                    tagMapService.save(tagMap);
                }
            }
            existingPost.setCategory(updatePost.getCategory());

            postRepository.save(existingPost);
        }

        return "redirect:/post/detail/{id}/1";
    }

    @PostMapping("/delete-image")
    public ResponseEntity<String> deleteImage(@RequestParam String saveName) {

        Image image = imageService.findBySaveName(saveName);

        if (image != null) {
            imageService.deleteImage(image);
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        }
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPosts")
    public String getMyPosts(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        Profile author = memberService.getMember(principal.getName()).getProfile();
        Page<Post> myPosts = postService.getMyPosts(page, author);
        model.addAttribute("paging", myPosts);
        return "Member/findMyPosts_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myLikedPosts")
    public String getMyLikedPosts(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        Member member = memberService.getMember(principal.getName());
        Page<Post> myLikedPosts = postService.getMyLikedPosts(page, member);
        model.addAttribute("paging", myLikedPosts);
        return "Member/findMyLikedPosts_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/report/{id}")
    public String reportPost(@PathVariable("id") Long id, @RequestParam(value = "reasons", required = false) List<String> categories,
                             @RequestParam("reportPostContent") String content, Principal principal) {
        // 현재 사용자 정보 가져오기
        if (principal != null) {
            String username = principal.getName();

            // 이미 해당 사용자가 신고한 게시물인지 확인
            if (reportService.isAlreadyPostReported(id, username)) {
                // 이미 신고한 경우, 여기에서 처리할 내용 추가
                return "redirect:/post/detail/{id}/1"; // 또는 적절한 경로로 리다이렉트
            }
        }
        Report report = new Report();
        Post post = postService.getPost(id);
        Member member = memberService.getMember(principal.getName());
        if (categories != null && !categories.isEmpty()) {
            report.setCategory(categories);
            System.out.println("카테고리 : " + categories);
        } else {
            report.setCategory(new ArrayList<>());
        }
        if (!content.isEmpty() && content != null) {
            report.setContent(content);
        } else {
            report.setContent("");
        }
        report.setMember(member);
        report.setPost(post);
        report.setReportDate(LocalDateTime.now());
        reportService.save(report);

        return "redirect:/post/detail/{id}/1";
    }


    //   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 선영 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓


    //   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 선영 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}

