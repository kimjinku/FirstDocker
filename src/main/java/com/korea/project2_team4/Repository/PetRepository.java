package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Pet;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    @Query("SELECT p FROM Pet p WHERE p.name LIKE %:name%")
    Pet findByPetName(@Param("name")String name);

//    @Query("SELECT p FROM Pet p WHERE p.likes.profileName LIKE %:name%")
//    List<Pet> findAllByLikes(@Param("name")String name);

    @Query("SELECT DISTINCT p FROM Pet p JOIN p.likes likeProfile WHERE likeProfile.profileName LIKE %:name%")
    List<Pet> findAllByLikes(@Param("name") String name);

    @Query("SELECT p FROM Pet p WHERE p.name LIKE %:kw%")
    List<Pet> findAllBykw(@Param("kw")String kw);

}
