package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.Comment;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Report;
import com.korea.project2_team4.Model.Entity.ResalePost;
import com.korea.project2_team4.Repository.ReportRepository;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
public class ReportService {
    private final ReportRepository reportRepository;
    public void save(Report report){
        reportRepository.save(report);
    }
    public boolean isAlreadyPostReported(Long postId, String username) {
        // 해당 postId와 username에 해당하는 Report가 존재하는지 확인
        return reportRepository.existsByPostIdAndMemberUserName(postId, username);
    }
    public boolean isAlreadyCommentReported(Long commentId, String username) {

        return reportRepository.existsByCommentIdAndMemberUserName(commentId, username);
    }
    public boolean isAlreadyResalePostReported(Long commentId, String username) {

        return reportRepository.existsByResalePostIdAndMemberUserName(commentId, username);
    }

    public Page<Post> findPostsLinkedWithReports(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return reportRepository.findPostsLinkedWithReportsOrderByReportDateDesc(pageable);
    }
    public Page<Comment> findCommentsLinkedWithReports(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return reportRepository.findCommentsLinkedWithReportsOrderByReportDateDesc(pageable);
    }
    public Page<ResalePost> findResalePostsLinkedWithReports(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return reportRepository.findResalePostsLinkedWithReportsOrderByReportDateDesc(pageable);
    }
    public void deleteReports(List<Report> reports){
        for (Report report : reports) {
            reportRepository.delete(report);
        }
    }
}
