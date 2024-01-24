package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.Comment;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Repository.CommentRepository;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class CommentService {
    private CommentRepository commentRepository;

    public Comment create(Post post, String content, Profile author) {
        Comment comment = new Comment();

        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setPost(post);
        comment.setAuthor(author);

       return this.commentRepository.save(comment);

    }
    public void createCommentReply(Long commentId ,String content, Profile author){
       Comment parentComment = commentRepository.findById(commentId).orElse(null);
       Comment comment = new Comment();
       comment.setParentComment(parentComment);
       comment.setPost(parentComment.getPost());
       comment.setCreateDate(LocalDateTime.now());
       comment.setContent(content);
       comment.setAuthor(author);

       this.commentRepository.save(comment);
    }

    public void updateCommentReply(Long replyId ,String newcontent){
        Comment replycommet = commentRepository.findById(replyId).get();
        replycommet.setContent(newcontent);
        this.commentRepository.save(replycommet);
    }

    //댓글 좋아요 기능
    public void Like(Comment comment, Member member) {
        comment.getLikeMembers().add(member);
        this.commentRepository.save(comment);
    }

    public void unLike(Comment comment, Member member) {
        comment.getLikeMembers().remove(member);
        this.commentRepository.save(comment);
    }

    public boolean isLiked(Comment comment, Member member) {
        if (comment == null) {
            return false;
        }
        return comment.getLikeMembers().contains(member);
    }

    public Comment getComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        return commentOptional.orElse(null); //값이 없으면 null 값으로 반환
    }

    public void deleteById(Long id) {
      commentRepository.deleteById(id);
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }
    public Page<Comment> getMyComments(int page, Profile author){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return commentRepository.findByAuthor(author,pageable);
    }
    public Page<Comment> getMyLikedComments(int page,Member member){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return commentRepository.findByLikeMembers(member,pageable);

    }
}
