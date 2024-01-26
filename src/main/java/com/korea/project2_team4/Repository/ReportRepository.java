package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Comment;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Report;
import com.korea.project2_team4.Model.Entity.ResalePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByPostIdAndMemberUserName(Long postId, String userName);
    boolean existsByCommentIdAndMemberUserName(Long commentId, String userName);

    boolean existsByResalePostIdAndMemberUserName(Long resalePostId, String userName);

//    @Query("SELECT DISTINCT r.post FROM Report r WHERE r.post IS NOT NULL ORDER BY r.reportDate DESC")
//    Page<Post> findPostsLinkedWithReportsOrderByReportDateDesc(Pageable pageable);
    @Query("SELECT DISTINCT r.post FROM Report r " +
        "LEFT JOIN FETCH r.post.reports " +
        "WHERE r.post IS NOT NULL " +
        "ORDER BY r.post.id, r.reportDate DESC")
    Page<Post> findPostsLinkedWithReportsOrderByReportDateDesc(Pageable pageable);

    @Query("SELECT DISTINCT r.comment FROM Report r " +
            "LEFT JOIN FETCH r.comment.reports " +
            "WHERE r.comment IS NOT NULL " +
            "ORDER BY r.comment.id, r.reportDate DESC")
    Page<Comment> findCommentsLinkedWithReportsOrderByReportDateDesc(Pageable pageable);

    @Query("SELECT DISTINCT r.resalePost FROM Report r " +
            "LEFT JOIN FETCH r.resalePost.reports " +
            "WHERE r.resalePost IS NOT NULL AND r.resalePost.soldItem = false " +
            "ORDER BY r.resalePost.id, r.reportDate DESC")
    Page<ResalePost> findResalePostsLinkedWithReportsOrderByReportDateDesc(Pageable pageable);


}
