package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.ProfileDto;
import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Model.Form.ProfileForm;
import com.korea.project2_team4.Repository.MessageRepository;
import com.korea.project2_team4.Repository.SaveMessageRepository;
import com.korea.project2_team4.Service.*;

import jakarta.servlet.http.HttpSession;
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

    private final MessageRepository messageRepository; //서비스로 추후수정. 테스트용임
    private TagService tagService;
    private TagMapService tagMapService;

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
    public String profileupdate(ProfileForm profileForm, @RequestParam(value = "profileImage") MultipartFile newprofileImage,
                                BindingResult bindingResult, Principal principal) throws Exception {
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


    @GetMapping("/showallPostsBy")
    public String showAllMyPosts(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam("profileid") Long profileid, Principal principal) {
        if (principal == null) {
            List<Post> myposts = postService.getPostsbyAuthor(profileService.getProfileById(profileid));
            model.addAttribute("searchResults", myposts);
            return "search_form";
        } else {
            Member sitemember = this.memberService.getMember(principal.getName());
            List<Post> myposts = postService.getPostsbyAuthor(sitemember.getProfile());

            model.addAttribute("searchResults", myposts);
            return "search_form";
//        return "community_main";
        }
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



// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓펫 관리↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/addpet")
    public String addpet(@RequestParam("name") String name, @RequestParam("content") String content, Principal principal,
                         MultipartFile imageFile) throws Exception, NoSuchAlgorithmException {
        Member sitemember = this.memberService.getMember(principal.getName());
        Pet pet = new Pet();


        pet.setName(name);
        pet.setContent(content);
        pet.setOwner(sitemember.getProfile());
        petService.savePet(pet);
//
//        if (imageFile != null && !imageFile.isEmpty() && pet !=null) { //이미지 첨부를 했을때
//            imageService.saveImgsForPet(pet,imageFile); // 기존이미지잇으면 지우고 등록함.
//        }

        if (imageFile == null || imageFile.isEmpty()) {
            if (pet.getPetImage() == null) {
                imageService.saveDefaultImgsForPet(pet);
            }
        } else if (pet != null) {
            imageService.saveImgsForPet(pet, imageFile);
        }

        profileService.setPetforprofile(sitemember.getProfile(), pet);
        String encodedProfileName = URLEncoder.encode(sitemember.getProfile().getProfileName(), "UTF-8");
        return "redirect:/profile/detail/" + encodedProfileName;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deletepet")
    public String deletepet(@RequestParam("petid") Long petid, Principal principal)throws UnsupportedEncodingException {
        Member sitemember = this.memberService.getMember(principal.getName());
        Pet pet = petService.getpetById(petid);
        petService.deletePet(pet);

        String encodedProfileName = URLEncoder.encode(sitemember.getProfile().getProfileName(), "UTF-8");
        return "redirect:/profile/detail/" + encodedProfileName;
    }


    @GetMapping("/petprofile/{petName}")
    public String petprofile(Model model, @PathVariable("petName")String petName) {
        Pet pet = petService.getpetByname(petName);
//        List<Post> ownerposts = postService.getPostsbyAuthor(pet.getOwner()); //???

        List<Post> postList = new ArrayList<>();

        if (tagService.tagExists(petName)) {
            Tag tag = tagService.getTagByTagName(petName);
            List<TagMap> tagMapList = tagMapService.findTagMapsByTagId(tag.getId());
            for (TagMap tagMap : tagMapList) {
                if ( (tagMap.getTag().getName().equals(petName)) && (tagMap.getPost().getAuthor().equals(pet.getOwner()))) {
                    Post post = tagMap.getPost();
                    postList.add(post);
                }
            }
        }


        model.addAttribute("postList", postList);
        model.addAttribute("pet", pet);
        return "Profile/pet_profile";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updatepet")
    public String profileupdate(@RequestParam(value = "petImage") MultipartFile newpetImage, @RequestParam("petid") Long petid,
                                @RequestParam(value = "name") String name, @RequestParam(value = "content") String content,
                                Principal principal) throws Exception {
//        Member sitemember = this.memberService.getMember(principal.getName());
        Pet pet = petService.getpetById(petid);


        if (newpetImage != null && !newpetImage.isEmpty()) {
            imageService.saveImgsForPet(pet, newpetImage); // 이미지처리따로.
        }

        petService.updatePet(pet, name, content);

        return "redirect:/profile/petprofile?petid=" + petid;

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/createPetPhotos")
    public String createpetphotos(@RequestParam("petid") Long petid, Principal principal, HttpSession session) {

        Pet pet = petService.getpetById(petid);
        String petName = pet.getName();

        session.setAttribute("petName", petName);
        return "redirect:/post/createPost";

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


    @GetMapping("/detail/followers/{profileid}")
    public String followers(Model model, @PathVariable("profileid") Long profileid) {//@RequestParam(value = "profileId")Long profileId
        Profile targetprofile = profileService.getProfileById(profileid);
        List<Profile> followerList = followingMapService.getMyfollowers(targetprofile);

        model.addAttribute("followerList", followerList);
        return "Profile/followers";
    }

    @GetMapping("/detail/followings/{profileid}")
    public String followings(Model model, @PathVariable("profileid") Long profileid) {//@RequestParam(value = "profileId")Long profileId
        Profile targetprofile = profileService.getProfileById(profileid);
        List<Profile> followingList = followingMapService.getMyfollowings(targetprofile);

        model.addAttribute("followingList", followingList);
        return "Profile/followings";
    }


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 1:1 디엠 관리↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    @GetMapping("/dmTo/{profileName}")
    public String dmPage(Principal principal, Model model,@PathVariable("profileName") String profileName) {
        System.out.println(principal.getName());
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile partner = profileService.getProfileByName(profileName);
        Profile me = sitemember.getProfile();
        List<Message> messageList = me.getMyMessages();
        List<Message> receivedmessageList = me.getReceivedMessages();
        //위에 두개 메시지 리스트 붙여서 재조합해서, 시간순으로 정렬해서 리스트 새로 만들기

        model.addAttribute("me", me);
        model.addAttribute("partner", partner);
        model.addAttribute("messageList", messageList);
        model.addAttribute("receivedmessageList",receivedmessageList);
        return "Profile/dmPage";
    }


    @PostMapping("/sendmessageTo/{profileName}")
    public String sendmessage(Principal principal, Model model, @PathVariable("profileName") String profileName, @RequestParam(value = "message") String message) {
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile partner = profileService.getProfileByName(profileName);
        Message sendmessage = new Message();
        sendmessage.setAuthor(sitemember.getProfile());
        sendmessage.setReceiver(partner);
        sendmessage.setContent(message);
        sendmessage.setCreateDate(LocalDateTime.now());
        messageRepository.save(sendmessage);

        String encodedValue = URLEncoder.encode(profileName, StandardCharsets.UTF_8);
        return "redirect:/profile/dmTo/" + encodedValue;
    }

    @PostMapping("/sendmessage")
    public String sendmessage() {
        return "redirect:/Proflle/dmpage";
    }

////////////////////////웹소켓 테스트. 선영추ㅡ가//////////////////////////////////////////

    private final SaveMessageRepository saveMessageRepository;

    @MessageMapping("/hello")
    @SendTo("/topic/messaging")
    public SaveMessage greeting(SendMessage message, Principal principal) throws Exception {
        // 메시지에서 이름 추출
        String content = message.getContent();
        String user = principal.getName();
        LocalDateTime timenow = LocalDateTime.now();

//        Member sitemember = this.memberService.getMember(principal.getName());
//        Profile user = sitemember.getProfile();


        SaveMessage saveMessage = new SaveMessage(HtmlUtils.htmlEscape(content), user, timenow);
        SaveMessage savedSaveMessage = saveMessageRepository.save(saveMessage);

        DmPage dmPage = new DmPage();


        // 저장된 SaveMessage 엔터티 반환
        return savedSaveMessage;
    }


}