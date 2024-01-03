package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByPostIdAndMemberUserName(Long postId, String userName);

    @Query("SELECT DISTINCT r.post FROM Report r WHERE r.post IS NOT NULL ORDER BY r.reportDate DESC")
    Page<Post> findPostsLinkedWithReportsOrderByReportDateDesc(Pageable pageable);

}
