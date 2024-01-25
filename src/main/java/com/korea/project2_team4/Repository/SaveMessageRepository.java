package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Dto.SaveMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaveMessageRepository extends JpaRepository<SaveMessage, Long> {

    @Query("SELECT s FROM SaveMessage s WHERE s.dmPage.id = :dmid AND s.author = :name")
    List<SaveMessage> findAllByNameAnddmPageId(@Param("name") String name, @Param("dmid") Long dmid);

    @Query("select s from SaveMessage s where s.dmPage.id = :dmid")
    List<SaveMessage> findAllByDmPageIdOrderByTime(@Param("dmid") Long dmid);


}
