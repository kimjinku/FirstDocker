package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Model.Entity.ResalePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResalePostRepository extends JpaRepository<ResalePost, Long> {
    Page<ResalePost> findAll(Pageable pageable);

    @Query("SELECT r FROM ResalePost r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(r.content) LIKE LOWER(CONCAT('%', :kw, '%'))")
    Page<ResalePost> findByTitleOrContentContainingIgnoreCase(@Param("kw") String kw, Pageable pageable);

    Page<ResalePost> findByWishProfiles(Profile profile, Pageable pageable);

    Page<ResalePost> findBySeller(Profile seller, Pageable pageable);
}
