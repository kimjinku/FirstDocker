package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Repository.*;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Service
public class PostService {
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final TagRepository tagRepository;
    private final TagMapRepository tagMapRepository;

    public void save(Post post) {
        postRepository.save(post);
    }


    public Page<Post> postList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findAll(pageable);
    }

    public Page<Post> getPostsByTagName(int page, String searchTagName) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByTagName(searchTagName, pageable);
    }

    public Page<Post> getPostsOrderByLikeCount(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findAllOrderByLikeMembersSizeDesc(pageable);
    }

    public Page<Post> getPostsOrderByCommentCount(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findAllOrderByCommentsSizeDesc(pageable);
    }

    public Page<Post> getMyPosts(int page, Profile author) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByAuthor(author, pageable);
    }

    public Page<Post> getMyLikedPosts(int page, Member member) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByLikeMembers(member, pageable);
    }
////    신고
//    public void report(Post post, Member member) {
//        post.getReportMembers().add(member);
//        this.postRepository.save(post);
//    }
//
//    public void cancelReport(Post post, Member member) {
//        post.getReportMembers().remove(member);
//        this.postRepository.save(post);
//    }
//
//    public boolean isReported(Post post, Member member) {
//        if (post == null) {
//            return false;
//        }
//        return post.getReportMembers().contains(member);
//    }
//

//    테스트 데이터
    @PostConstruct
    public void init() {
        saveTestPost();
    }


    //테스트 데이터
    @Transactional
    public void saveTestPost() {
        if (postRepository.findAll().isEmpty()) {

//            Member admin = memberRepository.findByUserName("admin").orElse(null);
//
//
//
//
//            Profile authorProfile = profileRepository.findByMember(admin)
//                    .orElseGet(() -> { // admin없으면 실행되는?
//                        Profile newProfile = new Profile();
//                        newProfile.setProfileName("관리자");
//                        return profileRepository.save(newProfile);
//                    });
//
            Tag etcTag = new Tag();
            etcTag.setName("기타");
            tagRepository.save(etcTag);



//            Tag etcTag = tagRepository.findByName("기타").orElse(null);

            if (etcTag == null) {
                Tag tag = new Tag();
                tag.setName("기타");
                etcTag = tagRepository.save(tag);
            }

            for (int i = 1; i <= 10; i++) {
                Post post = new Post();
                post.setTitle(String.format("테스트 데이터 제목 입니다:[%03d].", i));
                post.setContent("테스트 데이터 내용 입니다.");
                post.setCreateDate(LocalDateTime.now());
                post.setCategory("자유게시판");
                Profile authorProfile = profileRepository.findByProfileName("테스트유저1" ).get();
                post.setAuthor(authorProfile);
                postRepository.save(post);

                TagMap etcTagMap = new TagMap();
                etcTagMap.setPost(post);
                etcTagMap.setTag(etcTag);
                tagMapRepository.save(etcTagMap);

            }
        }
    }




    // 게시물 검색기능
    public List<Post> searchPosts(String kw) {
        return postRepository.findAllByKw(kw);
    }

    public List<Post> searchPostTitle(String kw) {
        return postRepository.findByPostTitle(kw);
    }

    public List<Post> searchPostContent(String kw) {
        return postRepository.findByPostContent(kw);
    }

    public List<Post> searchProfileName(String kw) {
        return postRepository.findByProfileName(kw);
    }

    public List<Post> searchCommentContent(String kw) {
        return postRepository.findByCommentContent(kw);
    }

    public List<Post> searchTagName(String kw) {
        return postRepository.findByTagName(kw);
    }

    // post를 optional타입으로 가져오기
    public Post getPost(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);

    }

    public Post getPostIncrementView(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            Post postView = postOptional.get();
            postView.setView(postView.getView() + 1);
            return postRepository.save(postView);
        }
        return postOptional.orElse(null);
    }

    //좋아요 기능
    public void Like(Post post, Member member) {
        post.getLikeMembers().add(member);
        this.postRepository.save(post);
    }

    public void unLike(Post post, Member member) {
        post.getLikeMembers().remove(member);
        this.postRepository.save(post);
    }

    public boolean isLiked(Post post, Member member) {
        if (post == null) {
            return false;
        }
        return post.getLikeMembers().contains(member);
    }

    public void     deleteById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            List<Image> postImages = post.getPostImages();

            if (postImages != null) {
                for (Image image : postImages) {
                    String filepath = image.getFilePath();

                    if (filepath != null && !filepath.isEmpty()) {
                        imageService.deleteExistingFile(filepath);
                    }
                }
            }

            this.postRepository.deleteById(id);
        }
    }

//   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 선영 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    //작성자게시글불러오기
    public List<Post> getPostsbyAuthor(Profile profile) {
        List<Post> targetPosts = this.postRepository.findAllByauthor(profile.getProfileName());
        return targetPosts;
    }

    //작성자 게시글 -> 페이징처리
    public Page<Post> myPostListPage(int page, Profile profile) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findAllByauthorPage(profile.getProfileName(), pageable);
    }

    public Page<Post> getPostsFreeboard(int page, String sort, String tagname) { //
        if (tagname == null) {
            tagname = "";  // 기본적으로 빈 문자열로 설정
        }
        if (sort == null) {
            sort = "latest";
        }

        Pageable pageable = PageRequest.of(page,10);

        if (tagname.equals("")) {
            if (sort.equals("likeCount")) {
                return this.postRepository.findAllByCategoryAndOrderByLikeMembersSizeDesc("자유게시판", pageable);
            } else if (sort.equals("commentCount")) {
                return this.postRepository.findAllByCategoryAndOrderByCommentsSizeDesc("자유게시판", pageable);
            } else { //최신순
                List<Sort.Order> sorts = new ArrayList<>();
                sorts.add(Sort.Order.desc("createDate"));
                Pageable pageable2 = PageRequest.of(page, 10, Sort.by(sorts));
                return this.postRepository.findAllByCategory("자유게시판", pageable2);
            }
        } else {
            if (sort.equals("likeCount")) {
                return this.postRepository.findAllByTagNameAndCategoryOrderByLikeMembersSizeDesc(tagname,"자유게시판",pageable);
            } else if (sort.equals("commentCount")) {
                return this.postRepository.findAllByTagNameAndCategoryOrderByCommentsSizeDesc(tagname,"자유게시판", pageable);
            } else { //최신순
                List<Sort.Order> sorts = new ArrayList<>();
                sorts.add(Sort.Order.desc("createDate"));
                Pageable pageable2 = PageRequest.of(page, 10, Sort.by(sorts));
                return this.postRepository.findAllByTagNameAndCategory(tagname,"자유게시판", pageable2);
            }
        }
    }

    public Page<Post> getAllPosts(int page, String sort, String tagname) {
        if (tagname == null) {
            tagname = "";  // 기본적으로 빈 문자열로 설정
        }
        if (sort == null) {
            sort = "latest";
        }

        Pageable pageable = PageRequest.of(page,10);

        if (tagname.equals("")) {
            if (sort.equals("likeCount")) {
                return this.postRepository.findAllByCategoryAndOrderByLikeMembersSizeDesc("", pageable);
            } else if (sort.equals("commentCount")) {
                return this.postRepository.findAllByCategoryAndOrderByCommentsSizeDesc("", pageable);
            } else { //최신순
                List<Sort.Order> sorts = new ArrayList<>();
                sorts.add(Sort.Order.desc("createDate"));
                Pageable pageable2 = PageRequest.of(page, 10, Sort.by(sorts));
                return this.postRepository.findAllByCategory("", pageable2);
            }
        } else {
            if (sort.equals("likeCount")) {
                return this.postRepository.findAllByTagNameAndCategoryOrderByLikeMembersSizeDesc(tagname,"",pageable);
            } else if (sort.equals("commentCount")) {
                return this.postRepository.findAllByTagNameAndCategoryOrderByCommentsSizeDesc(tagname,"", pageable);
            } else { //최신순
                List<Sort.Order> sorts = new ArrayList<>();
                sorts.add(Sort.Order.desc("createDate"));
                Pageable pageable2 = PageRequest.of(page, 10, Sort.by(sorts));
                return this.postRepository.findAllByTagNameAndCategory(tagname,"", pageable2);
            }
        }
    }

    public Page<Post> getPostsQnA(int page, String sort, String tagname) {
        if (tagname == null) {
            tagname = "";  // 기본적으로 빈 문자열로 설정
        }
        if (sort == null) {
            sort = "latest";
        }

        Pageable pageable = PageRequest.of(page,10);

        if (tagname.equals("")) {
            if (sort.equals("likeCount")) {
                return this.postRepository.findAllByCategoryAndOrderByLikeMembersSizeDesc("QnA", pageable);
            } else if (sort.equals("commentCount")) {
                return this.postRepository.findAllByCategoryAndOrderByCommentsSizeDesc("QnA", pageable);
            } else { //최신순
                List<Sort.Order> sorts = new ArrayList<>();
                sorts.add(Sort.Order.desc("createDate"));
                Pageable pageable2 = PageRequest.of(page, 10, Sort.by(sorts));
                return this.postRepository.findAllByCategory("QnA", pageable2);
            }
        } else {
            if (sort.equals("likeCount")) {
                return this.postRepository.findAllByTagNameAndCategoryOrderByLikeMembersSizeDesc(tagname,"QnA",pageable);
            } else if (sort.equals("commentCount")) {
                return this.postRepository.findAllByTagNameAndCategoryOrderByCommentsSizeDesc(tagname,"QnA", pageable);
            } else { //최신순
                List<Sort.Order> sorts = new ArrayList<>();
                sorts.add(Sort.Order.desc("createDate"));
                Pageable pageable2 = PageRequest.of(page, 10, Sort.by(sorts));
                return this.postRepository.findAllByTagNameAndCategory(tagname,"QnA", pageable2);
            }
        }
    }



    public List<Post> getPostslikes(){
        return this.postRepository.findAllByLikesDesc();
    }


//   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 선영 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    public Page<Post> pagingByTitle(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByPostTitleWithPaging(kw, pageable);
    }

    public Page<Post> pagingByContent(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByPostContentWithPaging(kw, pageable);
    }

    public Page<Post> pagingByProfileName(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByProfileNameWithPaging(kw, pageable);
    }


    public Page<Post> pagingByComment(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByCommentWithPaging(kw, pageable);
    }

    public Page<Post> pagingByTagName(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByTagNameWithPaging(kw, pageable);
    }

}
