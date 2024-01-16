package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Dto.SaveMessageDTO;
import com.korea.project2_team4.Model.Entity.DmPage;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Model.Entity.SaveMessage;
import com.korea.project2_team4.Repository.DmPageRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Builder
public class DmPageService {
    private final DmPageRepository dmPageRepository;

    public DmPage getMyDmPage(Profile me, Profile partner) {
        if ((!dmPageRepository.existsByMeAndPartner(me, partner)) && (!dmPageRepository.existsByMeAndPartner(partner, me)) && (me.getId() != partner.getId())) {
            DmPage dmPage = new DmPage();
            dmPage.setCreateDate(LocalDateTime.now());
            dmPage.setMe(me);
            dmPage.setPartner(partner);
            return this.dmPageRepository.save(dmPage);
        } else if (dmPageRepository.existsByMeAndPartner(me, partner)){
            return this.dmPageRepository.findByMeAndPartner(me.getId(), partner.getId());
        } else {
            return this.dmPageRepository.findByMeAndPartner(partner.getId(), me.getId());
        }
    }

    public List<DmPage> getMyDmPageList(Profile me) {
        List<DmPage> list = this.dmPageRepository.findAllMyDMPageList(me.getId());
        Profile temp = new Profile();
        for (DmPage d : list ) {
            if (d.getPartner().getId() == me.getId()) {

                temp = d.getMe();
                d.setPartner(temp);
                d.setMe(me);

            }
        }

        return list;
    }




}
