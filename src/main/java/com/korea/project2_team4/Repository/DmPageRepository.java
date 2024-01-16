package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.DmPage;
import com.korea.project2_team4.Model.Entity.Profile;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DmPageRepository extends JpaRepository<DmPage, Long> {
    boolean existsByMeAndPartner(Profile me, Profile partner);
    @Query("SELECT dm FROM DmPage dm WHERE dm.me.id = :myId AND dm.partner.id = :partnerId")
    DmPage findByMeAndPartner(@Param("myId")Long myId, @Param("partnerId")Long partnerId);

    @Query("SELECT dm FROM DmPage dm WHERE dm.me.id = :myId or dm.partner.id = :myId")
    List<DmPage> findAllMyDMPageList(@Param("myId")Long myId);
}
