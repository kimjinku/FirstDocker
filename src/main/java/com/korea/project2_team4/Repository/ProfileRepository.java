package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByProfileName(String profileName);
    Optional<Profile> findByMember(Member member);

    @Query("SELECT p FROM Profile p WHERE p.profileName LIKE %:name%")
    List<Profile> findAllBykw(@Param("name")String name);
}
