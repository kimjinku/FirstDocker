package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Dto.SaveMessageDTO;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Model.Dto.SaveMessage;
import com.korea.project2_team4.Repository.SaveMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveMessageDTOService {
    private final SaveMessageRepository saveMessageRepository;
    private final ProfileService profileService;

//    public List<SaveMessageDTO> getAllDMList(List<SaveMessage> saveMessages) {
//
//        List<SaveMessageDTO> mylist = new ArrayList<>();
//        List<SaveMessage> beforeDTOlist = saveMessageRepository.findAllByNameAnddmPageId(myName , dmPageId);
//        for (SaveMessage s : beforeDTOlist) {
//            SaveMessageDTO d = new SaveMessageDTO();
//            d.setAuthor(s.getAuthor());
//            d.setReceiver(s.getReceiver());
//            d.setContent(s.getContent());
//            d.setCreateDate(s.getCreateDate());
//
//            mylist.add(d);
//        }
//        return mylist;
//    }


    public List<SaveMessageDTO> getMyDMList(String myName, Long dmPageId) {
        List<SaveMessageDTO> mylist = new ArrayList<>();
        List<SaveMessage> beforeDTOlist = saveMessageRepository.findAllByNameAnddmPageId(myName , dmPageId);
        for (SaveMessage s : beforeDTOlist) {
            SaveMessageDTO d = new SaveMessageDTO();
            Profile profile = profileService.getProfileByName(s.getAuthor());
            d.setAuthorId(profile.getId());
            d.setAuthor(s.getAuthor());
            d.setReceiver(s.getReceiver());
            d.setContent(s.getContent());
            d.setCreateDate(s.getCreateDate());

            mylist.add(d);
        }
        return mylist;
    }

    public List<SaveMessageDTO> getDmPageMessages(Long dmPageId) {
        List<SaveMessageDTO> newList = new ArrayList<>();
        List<SaveMessage> beforeDTOlist = saveMessageRepository.findAllByDmPageIdOrderByTime(dmPageId);
        for (SaveMessage s : beforeDTOlist) {
            SaveMessageDTO d = new SaveMessageDTO();
            Profile profile = profileService.getProfileByName(s.getAuthor());
            d.setAuthorId(profile.getId());
            d.setAuthor(s.getAuthor());
            d.setReceiver(s.getReceiver());
            d.setContent(s.getContent());
            d.setCreateDate(s.getCreateDate());

            newList.add(d);
        }

        return newList;
    }

}
