package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.ProfileDto;
import com.korea.project2_team4.Model.Dto.SaveMessageDTO;
import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Model.Form.PetProfileForm;
import com.korea.project2_team4.Model.Form.ProfileForm;
import com.korea.project2_team4.Repository.DmPageRepository;
import com.korea.project2_team4.Repository.MessageRepository;
import com.korea.project2_team4.Repository.SaveMessageRepository;
import com.korea.project2_team4.Service.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import lombok.Builder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
@Builder
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private HttpSession session;
    private final ProfileService profileService;
    private final PostService postService;
    private final MemberService memberService;
    private final PetService petService;
    private final ImageService imageService;
    private final FollowingMapService followingMapService;
    private TagService tagService;
    private TagMapService tagMapService;
    private final DmPageService dmPageService;

    @GetMapping("/my")
    public String myProfile(Model model, Principal principal) {
        Member siteUser = this.memberService.getMember(principal.getName());
        Profile myprofile = profileService.getProfileByName(siteUser.getProfile().getProfileName());
        List<Post> postList = postService.getPostsbyAuthor(myprofile);
        List<Profile> followers = followingMapService.getMyfollowers(myprofile);
        List<Profile> followings = followingMapService.getMyfollowings(myprofile);

        model.addAttribute("siteUser", siteUser);
        model.addAttribute("profile", myprofile);
        model.addAttribute("postList", postList);
        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);
        return "Profile/profile_detail";
    }

    @GetMapping("/detail/{profileName}") // @AuthenticationPrincipal //
    public String profileDetail(Model model, Principal principal, @PathVariable("profileName")String profileName) {
        Member siteUser = new Member();

        if (principal != null) {
            siteUser = this.memberService.getMember(principal.getName());
        }

        Profile targetProfile = profileService.getProfileByName(profileName);
        List<Post> postList = postService.getPostsbyAuthor(targetProfile);
        List<Profile> followers = followingMapService.getMyfollowers(targetProfile);
        List<Profile> followings = followingMapService.getMyfollowings(targetProfile);

        model.addAttribute("siteUser", siteUser);
        model.addAttribute("profile", targetProfile);
        model.addAttribute("postList", postList);
        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);

        return "Profile/profile_detail";
    }

    @GetMapping("/detail/{profileName}/showall")
    public String detailShowall(Model model, Principal principal, @PathVariable("profileName")String profileName) {
        Member siteUser = new Member();

        if (principal != null) {
            siteUser = this.memberService.getMember(principal.getName());
        }

        Profile targetProfile = profileService.getProfileByName(profileName);
        List<Post> postList = postService.getPostsbyAuthor(targetProfile);
        List<Profile> followers = followingMapService.getMyfollowers(targetProfile);
        List<Profile> followings = followingMapService.getMyfollowings(targetProfile);

        model.addAttribute("siteUser", siteUser);
        model.addAttribute("profile", targetProfile);
        model.addAttribute("postList", postList);
        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);

        return "Profile/profile_detail_showall";

    }

//    @GetMapping("/showallPostsBy")
//    public String showAllMyPosts(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam("profileid") Long profileid, Principal principal) {
//        if (principal == null) {
//            List<Post> myposts = postService.getPostsbyAuthor(profileService.getProfileById(profileid));
//            model.addAttribute("searchResults", myposts);
//            return "search_form";
//        } else {
//            Member sitemember = this.memberService.getMember(principal.getName());
//            List<Post> myposts = postService.getPostsbyAuthor(sitemember.getProfile());
//
//            model.addAttribute("searchResults", myposts);
//            return "search_form";
//        }
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String profileupdate(Model model, ProfileForm profileForm, Principal principal) {
        Member sitemember = this.memberService.getMember(principal.getName());

        profileForm.setProfileName(sitemember.getProfile().getProfileName());
        profileForm.setContent(sitemember.getProfile().getContent());
        return "Profile/profile_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String profileupdate(@Valid ProfileForm profileForm, @RequestParam(value = "profileImage") MultipartFile newprofileImage,
                                BindingResult bindingResult, Principal principal) throws Exception {
//        if (bindingResult.hasErrors()) {
//            return "Profile/profile_form";
//        }
        Member sitemember = this.memberService.getMember(principal.getName());

        if (profileForm.getProfileImage() != null && !profileForm.getProfileImage().isEmpty()) {
            imageService.saveImgsForProfile(sitemember.getProfile(), newprofileImage); // 기존 이미지 먼저 지우게 된다.
        }

        profileService.updateprofile(sitemember.getProfile(), profileForm.getProfileName(), profileForm.getContent());

        String encodedProfileName = URLEncoder.encode(sitemember.getProfile().getProfileName(), "UTF-8");
        return "redirect:/profile/detail/" + encodedProfileName;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteProfileImage")
    public String deleteProfileImage(@RequestParam("profileid") Long profileid)throws Exception {// 일단안씀.
        Profile profile = profileService.getProfileById(profileid);
        imageService.deleteProfileImage(profile); // 이미지 지우고
        imageService.saveDefaultImgsForProfile(profile); // 디폴트이미지 재설정

        String encodedProfileName = URLEncoder.encode(profile.getProfileName(), "UTF-8");
        return "redirect:/profile/detail/" + encodedProfileName;
    }




    @PreAuthorize("isAuthenticated()")
    @PostMapping("/search")
    public String profileSearch(@RequestParam(value = "name", defaultValue = "") String name, Model model) {

        List<Profile> searchProfile = profileService.getAllProfileBykw(name);
        System.out.println("test");
        System.out.println(searchProfile.size());


        model.addAttribute("profiles", searchProfile);
        model.addAttribute("name", name);
        return "Profile/search_profile";
    }


// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓마이페이지↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String myPage(Model model, Principal principal) {
        Member sitemember = this.memberService.getMember(principal.getName());

        model.addAttribute("siteMember", sitemember);
        return "Member/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/update")
    public String memberUpdate(Principal principal, Model model) {
        Member sitemember = this.memberService.getMember(principal.getName());
        model.addAttribute("siteMember", sitemember);
        return "/Member/updateMember_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/delete")
    public String memberDelete(Principal principal) {
        Member member = this.memberService.getMember(principal.getName());
        memberService.delete(member);
        session.invalidate();
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/update")
    public String memberUpdate(Principal principal, String nickName, String phoneNum, String email) {
        Member sitemember = this.memberService.getMember(principal.getName());
        sitemember.setNickName(nickName);
        sitemember.setEmail(email);
        sitemember.setPhoneNum(phoneNum);
        memberService.save(sitemember);

        return "redirect:/profile/myPage";
    }


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓팔로우 관리↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    @PostMapping("/addfollow")
    public String addfollow(Model model, Principal principal, @RequestParam(value = "profileId") Long profileId)throws UnsupportedEncodingException {
//        플필아디 받고
//                팔로잉맵에 추가 현재로그인 foller로 플필아디followee로
        Profile followee = profileService.getProfileById(profileId);
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile follower = sitemember.getProfile();
        followingMapService.savefollowingMap(follower, followee);


        String encodedProfileName = URLEncoder.encode(followee.getProfileName(), "UTF-8");
        return "redirect:/profile/detail/" + encodedProfileName;
    }

    @PostMapping("/unfollow")
    public String unfollow(Model model, Principal principal, @RequestParam(value = "profileId") Long profileId)throws UnsupportedEncodingException {
//        플필아디 받고
//                팔로잉맵에 추가 현재로그인 foller로 플필아디followee로
        Profile followee = profileService.getProfileById(profileId);
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile follower = sitemember.getProfile();
        followingMapService.deletefollowingMap(follower, followee);

        String encodedProfileName = URLEncoder.encode(followee.getProfileName(), "UTF-8");
        return "redirect:/profile/detail/" + encodedProfileName;
    }


    @GetMapping("/detail/followers/{profileName}")
    public String followers(Model model, @PathVariable("profileName") String profileName) {//@RequestParam(value = "profileId")Long profileId
        Profile targetprofile = profileService.getProfileByName(profileName);
        List<Profile> followerList = followingMapService.getMyfollowers(targetprofile);

        model.addAttribute("followerList", followerList);
        return "Profile/followers";
    }

    @GetMapping("/detail/followings/{profileName}")
    public String followings(Model model, @PathVariable("profileName") String profileName) {
        Profile targetprofile = profileService.getProfileByName(profileName);
        List<Profile> followingList = followingMapService.getMyfollowings(targetprofile);

        model.addAttribute("followingList", followingList);
        return "Profile/followings";
    }


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 1:1 디엠 관리↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private final SaveMessageDTOService saveMessageDTOService;
    @GetMapping("/dmTo/{profileName}")
    public String dmPage(Principal principal, Model model,@PathVariable("profileName") String profileName) {

        Member sitemember = this.memberService.getMember(principal.getName());
        Profile partner = profileService.getProfileByName(profileName);
        Profile me = sitemember.getProfile();


        DmPage dmPage = dmPageService.getMyDmPage(me,partner); //없으면새로추가함..
        List<SaveMessageDTO> dmPageMessages = saveMessageDTOService.getDmPageMessages(dmPage.getId());

        model.addAttribute("me", me);
        model.addAttribute("partner", partner);
        model.addAttribute("dmPageMessages",dmPageMessages);
        return "Profile/dmPage";
    }

    @GetMapping("/dmTo/{profileName}/delete")
    public String deleteDmPage(Principal principal,@PathVariable("profileName") String profileName) throws UnsupportedEncodingException {
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile partner = profileService.getProfileByName(profileName);
        Profile me = sitemember.getProfile();

        List<DmPage> myDmList = dmPageService.getMyDmPageList(me);
        DmPage dmPage = dmPageService.getMyDmPage(me,partner);
        myDmList.remove(dmPage); //어케할지,.

        String encodedProfileName = URLEncoder.encode(me.getProfileName(), "UTF-8");
        return "redirect:/profile/myDmPages/" + encodedProfileName;
    }


    @GetMapping("/myDmPages/{profileName}")
    public String myDmPages(Principal principal, Model model,@PathVariable("profileName") String profileName) {
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile me = sitemember.getProfile();
        List<DmPage> myDmList = dmPageService.getMyDmPageList(me);

        model.addAttribute("myDmList",myDmList);
        return "Profile/myDmPages";
    }


////////////////////////웹소켓 알림 테스트. 선영추ㅡ가//////////////////////////////////////////

    private final SaveMessageRepository saveMessageRepository;
    private final DmPageRepository dmPageRepository;

    @MessageMapping("/hello")
    @SendTo("/sub/messaging")
    public SaveMessageDTO messaging(SendMessage message, Principal principal) throws Exception {
        // 메시지에서 이름 추출
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile writer = sitemember.getProfile();

        String content = message.getContent();
        String receiver = message.getReceiver();
        String sendTime = message.getCreateDate();
//        String sender = message.getSender();

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
        SaveMessage saveMessage = new SaveMessage(HtmlUtils.htmlEscape(content), author, receiver, timenow, dmPage); //
        saveMessageRepository.save(saveMessage);

        Profile profile = profileService.getProfileByName(saveMessage.getAuthor());

        SaveMessageDTO messageDTO = new SaveMessageDTO();
        messageDTO.setAuthorId(profile.getId());
        messageDTO.setAuthor(saveMessage.getAuthor());
        messageDTO.setContent(saveMessage.getContent());
        messageDTO.setCreateDate(saveMessage.getCreateDate());
//        dmPageService.addSaveMessages(dmPage, saveMessage);


        // 저장된 SaveMessage 엔터티 반환
        return messageDTO; //화면 출력하는거 JSON으로전달해서 ?
    }

}