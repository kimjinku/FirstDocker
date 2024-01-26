package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.*;
import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Repository.DmPageRepository;
import com.korea.project2_team4.Repository.SaveMessageRepository;
import com.korea.project2_team4.Service.DmPageService;
import com.korea.project2_team4.Service.MemberService;
import com.korea.project2_team4.Service.ProfileService;
import com.korea.project2_team4.Service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class DmController {
    private final SaveMessageRepository saveMessageRepository;
    private final DmPageRepository dmPageRepository;
    private final MemberService memberService;
    private final ProfileService profileService;
    private final DmPageService dmPageService;
    private final S3Service s3Service;

    @MessageMapping("/hello")
    @SendTo("/sub/messaging")
    public SaveMessageDTO messaging(SendMessage message, Principal principal) throws Exception {
        // 메시지에서 이름 추출
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile writer = sitemember.getProfile();
        System.out.println(message.getDmImageDto());

        String content = message.getContent();
        String receiver = message.getReceiver();
        String sendTime = message.getCreateDate();
        DmImageDto dmImageDto = message.getDmImageDto();
        String imgPath;

        if (message.getDmImageDto() != null) {
            DmImageDto dmImageDto2 = message.getDmImageDto();
            String fileName = s3Service.generateDmImageName(writer.getProfileName(),message.getCreateDate(), dmImageDto.getContentType());
            // s3에 이미지를 업로드하고 그 이미지 path 경로를 img에 저장
            imgPath = s3Service.uploadToS3(fileName, dmImageDto2.getData(), dmImageDto2.getContentType());
        } else {
            imgPath = null;
        }



        String author = writer.getProfileName();


        Profile partner = profileService.getProfileByName(receiver);

        LocalDateTime timenow = LocalDateTime.now();
//        방생성과동시엥ㅇ?
        //dmPage생성, 저장
        //상대메시지저장 이름불러와서 partner

//        Member sitemember = this.memberService.getMember(principal.getName());
//        Profile user = sitemember.getProfile();

        DmPage dmPage = dmPageService.getMyDmPage(writer, partner);
//        DmPage dmPage = new DmPage();
//       dmPageRepository.save(dmPage);
        SaveMessage saveMessage = new SaveMessage(HtmlUtils.htmlEscape(content), author, receiver, imgPath, timenow, dmPage); //
        saveMessageRepository.save(saveMessage);

        Profile profile = profileService.getProfileByName(saveMessage.getAuthor());

        SaveMessageDTO messageDTO = new SaveMessageDTO();
        messageDTO.setId(saveMessage.getId());
        messageDTO.setAuthorId(profile.getId());
        messageDTO.setAuthor(saveMessage.getAuthor());
        messageDTO.setContent(saveMessage.getContent());
        messageDTO.setImage(saveMessage.getImage());
        messageDTO.setCreateDate(saveMessage.getCreateDate());

//        dmPageService.addSaveMessages(dmPage, saveMessage);


        // 저장된 SaveMessage 엔터티 반환
        return messageDTO; //화면 출력하는거 JSON으로전달해서 ?
    }
}
