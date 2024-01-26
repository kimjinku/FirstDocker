package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 게시물 제목, 내용, 프로필 이름, 댓글 내용 검색하여 조회
    @Query("SELECT p FROM Post p " + "LEFT JOIN p.author pr " + "LEFT JOIN p.comments c " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%',:kw,'%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%',:kw,'%')) " +
            "OR LOWER(pr.profileName) LIKE LOWER(CONCAT('%',:kw,'%')) " +
            "OR LOWER(c.content) LIKE LOWER(CONCAT('%',:kw,'%')) " +
            "ORDER BY p.id DESC")
    List<Post> findAllByKw(@Param("kw") String kw);

    // 게시물 제목으로 검색 조회
    @Query("SELECT p FROM Post p " + "LEFT JOIN p.author pr " + "LEFT JOIN p.comments c " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    List<Post> findByPostTitle(@Param("kw") String kw);

    // 게시물 내용으로 검색 조회
    @Query("SELECT p FROM Post p " + "LEFT JOIN p.author pr " + "LEFT JOIN p.comments c " +
            "WHERE LOWER(p.content) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    List<Post> findByPostContent(@Param("kw") String kw);

    // 프로필 이름으로 검색 조회
    @Query("SELECT p FROM Post p " + "LEFT JOIN p.author pr " + "LEFT JOIN p.comments c " +
            "WHERE LOWER(pr.profileName) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    List<Post> findByProfileName(@Param("kw") String kw);

    // 댓글 내용으로 검색 조회
    @Query("SELECT p FROM Post p " + "LEFT JOIN p.author pr " + "LEFT JOIN p.comments c " +
            "WHERE LOWER(c.content) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    List<Post> findByCommentContent(@Param("kw") String kw);

    // 태그 이름으로 검색 조회
    @Query("SELECT p FROM Post p" + " LEFT JOIN p.tagMaps tm " + "LEFT JOIN tm.tag t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    List<Post> findByTagName(@Param("kw") String kw);


     //태그명을 기준으로 해당 태그를 갖고 있는 포스트를 페이징하여 조회
    @Query("SELECT p FROM Post p JOIN p.tagMaps tm JOIN tm.tag t WHERE t.name = :tagName")
    Page<Post> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    Page<Post> findByCategory(String category,Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY SIZE(p.comments) DESC")
    Page<Post> findAllOrderByCommentsSizeDesc(Pageable pageable);
    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likeMembers) DESC")
    Page<Post> findAllOrderByLikeMembersSizeDesc(Pageable pageable);

    Page<Post> findByAuthor(Profile author,Pageable pageable);

    // 사용자가 좋아요 누른 글을 찾는 메서드
    Page<Post> findByLikeMembers(Member member, Pageable pageable);





//   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 선영 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    //메인페이지에 쓸 포스트리스트 좋아요순
    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likeMembers) DESC")
    List<Post> findAllByLikesDesc();

    //작성자 게시물 리스트all 반환
    @Query("SELECT p FROM Post p WHERE p.author.profileName LIKE %:name%")
    List<Post> findAllByauthor(@Param("name")String name);
    //작성자 게시물 리스트 페이징처리 반환
    @Query("SELECT p FROM Post p WHERE p.author.profileName LIKE %:name%")
    Page<Post> findAllByauthorPage(@Param("name")String name, Pageable pageable);

    //태그+카테고리, 작성일 내림차순
    @Query("SELECT p FROM Post p " +
            "JOIN p.tagMaps tm JOIN tm.tag t " +
            "WHERE (t.name = :tagName and (:category IS NOT NULL AND p.category LIKE %:category%))")
    Page<Post> findAllByTagNameAndCategory(
            @Param("tagName") String tagName,
            @Param("category") String category,
            Pageable pageable
    );

    //태그+카테고리, 좋아요내림차순
    @Query("SELECT p FROM Post p " +
            "JOIN p.tagMaps tm JOIN tm.tag t " +
            "WHERE (t.name = :tagName and (:category IS NOT NULL AND p.category LIKE %:category%)) " +
            "ORDER BY SIZE(p.likeMembers) DESC")
    Page<Post> findAllByTagNameAndCategoryOrderByLikeMembersSizeDesc(
            @Param("tagName") String tagName,
            @Param("category") String category,
            Pageable pageable
    );
    //태그+카테고리, 댓글수내림차순
    @Query("SELECT p FROM Post p " +
            "JOIN p.tagMaps tm JOIN tm.tag t " +
            "WHERE (t.name = :tagName and (:category IS NOT NULL AND p.category LIKE %:category%)) " +
            "ORDER BY SIZE(p.comments) DESC")
    Page<Post> findAllByTagNameAndCategoryOrderByCommentsSizeDesc(
            @Param("tagName") String tagName,
            @Param("category") String category,
            Pageable pageable
    );

    //태그, 작성일 내림차순
    @Query("SELECT p FROM Post p JOIN p.tagMaps tm JOIN tm.tag t WHERE t.name = :tagName")
    Page<Post> findAllByTagName(@Param("tagName") String tagName, Pageable pageable);

    //태그, 좋아요 내림차순
    @Query("SELECT p FROM Post p JOIN p.tagMaps tm JOIN tm.tag t WHERE t.name = :tagName ORDER BY SIZE(p.likeMembers) DESC")
    Page<Post> findAllByTagNameOrderByLikeMembersSizeDesc(@Param("tagName") String tagName, Pageable pageable);

    //태그, 댓글수 내림차순
    @Query("SELECT p FROM Post p JOIN p.tagMaps tm JOIN tm.tag t WHERE t.name = :tagName ORDER BY SIZE(p.comments) DESC")
    Page<Post> findAllByTagNameOrderByCommentsSizeDesc(@Param("tagName") String tagName, Pageable pageable);

    //카테고리, 작성일 내림차순
    @Query("SELECT p FROM Post p WHERE p.category IS NOT NULL AND p.category LIKE %:category%")
    Page<Post> findAllByCategory(@Param("category") String category, Pageable pageable);

    //카테고리, 좋아요 내림차순
    @Query("SELECT p FROM Post p WHERE p.category IS NOT NULL AND p.category LIKE %:category% ORDER BY SIZE(p.likeMembers) DESC")
    Page<Post> findAllByCategoryAndOrderByLikeMembersSizeDesc(@Param("category") String category, Pageable pageable);


    //카테고리, 댓글수 내림차순
    @Query("SELECT p FROM Post p WHERE p.category IS NOT NULL AND p.category LIKE %:category% ORDER BY SIZE(p.comments) DESC")
    Page<Post> findAllByCategoryAndOrderByCommentsSizeDesc(@Param("category") String category, Pageable pageable);


//   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 선영 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑



    @Query("SELECT p FROM Post p " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    Page<Post> findByPostTitleWithPaging(@Param("kw") String kw, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE LOWER(p.content) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    Page<Post> findByPostContentWithPaging(@Param("kw") String kw, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author.profileName LIKE %:kw%")
    Page<Post> findByProfileNameWithPaging(@Param("kw") String kw, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.comments c " +
            "WHERE LOWER(c.content) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    Page<Post> findByCommentWithPaging(@Param("kw") String kw, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE LOWER(p.tagMaps) LIKE LOWER(CONCAT('%',:kw,'%')) ")
    Page<Post> findByTagNameWithPaging(@Param("kw") String kw, Pageable pageable);

}
