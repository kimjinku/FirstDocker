package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.DmPage;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Model.Entity.SaveMessage;
import com.korea.project2_team4.Repository.DmPageRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Builder
public class DmPageService {
    private final DmPageRepository dmPageRepository;

    public void setMyDmPage(Profile me, Profile partner){
        if (!dmPageRepository.existsByMeAndPartner(me, partner)) {
            DmPage dmPage = new DmPage();
            dmPage.setCreateDate(LocalDateTime.now());
            dmPage.setMe(me);
            dmPage.setPartner(partner);
            this.dmPageRepository.save(dmPage);
        } else {
            System.out.println("이미존재하는dm창입니다.");
        }
    }

    public DmPage getMyDmPage(Profile me, Profile partner) {
       return this.dmPageRepository.findByMeAndPartner(me.getId(), partner.getId());
    }

    public void addSaveMessages(DmPage dmpage, SaveMessage saveMessage) {
        this.dmPageRepository.findById(dmpage.getId()).get().getSaveMessages().add(saveMessage);
    }
}
