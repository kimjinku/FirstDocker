package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.ResalePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResalePostRepository extends JpaRepository<ResalePost, Long> {
    Page<ResalePost> findAll(Pageable pageable);
}
