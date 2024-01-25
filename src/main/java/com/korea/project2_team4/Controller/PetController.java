package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Model.Form.PetProfileForm;
import com.korea.project2_team4.Repository.MessageRepository;
import com.korea.project2_team4.Repository.PetRepository;
import com.korea.project2_team4.Service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Builder
@RequestMapping("/profile/petprofile")
public class PetController {
    private final PetService petService;
    private final ProfileService profileService;
    private final PostService postService;
    private final MemberService memberService;
    private final ImageService imageService;
    private TagService tagService;
    private TagMapService tagMapService;


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
    @GetMapping("/createPetPhotos")
    public String createpetphotos(@RequestParam("petid") Long petid, Principal principal, HttpSession session) {

        Pet pet = petService.getpetById(petid);
        String petName = pet.getName();

        session.setAttribute("petName", petName);
        return "redirect:/post/createPost";

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/petLike")
    public String petLike(Principal principal, @RequestParam("id") Long id, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (principal != null) {
            Pet pet = this.petService.getpetById(id);
            Member member = this.memberService.getMember(principal.getName());
            Profile profile = member.getProfile();

            boolean isChecked = false;

            if (petService.isLiked(pet, profile)) {
                petService.unLike(pet, profile);
            } else {
                petService.Like(pet, profile);
                isChecked = true;
            }
            redirectAttributes.addFlashAttribute("isChecked", isChecked);
            String encodedPetName = URLEncoder.encode(pet.getName(), "UTF-8");
            String encodedOwnerName = URLEncoder.encode(pet.getOwner().getProfileName(), "UTF-8");

            return "redirect:/profile/petprofile/" + encodedPetName + "/" + encodedOwnerName + "/1";
        } else {
            return "redirect:/member/login";
        }
    }


    @GetMapping("/{petName}/{ownerName}/{hit}")
    public String petprofile(Principal principal, Model model, @PathVariable("petName")String petName, @PathVariable("ownerName")String ownerName) {
        Profile owner = profileService.getProfileByName(ownerName);
        Pet pet = petService.getpetBynameAndowner(petName,owner);
//        Pet pet = petService.getpetByname(petName);



//        List<Post> ownerposts = postService.getPostsbyAuthor(pet.getOwner()); //???
        if (principal !=null ) {
            Member member = memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
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

    @GetMapping("/petList")
    public String petList(Principal principal,Model model) {
        Member sitemember = this.memberService.getMember(principal.getName());
        Profile me = sitemember.getProfile();
        List<Pet> petList = petService.getMyLikePets(me);

        model.addAttribute("petList", petList);
        return "Profile/petList";

    }

    @PostMapping("/search")
    public String searchpet(@RequestParam(value = "name", defaultValue = "") String name, Model model) {

        List<Pet> searchpets = petService.getAllPetsBykw(name);
        System.out.println("test");
        System.out.println(searchpets.size());


        model.addAttribute("pets", searchpets);
        model.addAttribute("name", name);
        return "Profile/search_pet";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{petname}/{integer}/update")
    public String petupdate(Model model, @PathVariable("petname")String petname, PetProfileForm petProfileForm, Principal principal) {

        Pet pet = petService.getpetByname(petname);
        petProfileForm.setName(pet.getName());
        petProfileForm.setContent(pet.getContent());
        return "Profile/pet_profile_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{petname}/{integer}/update")
    public String petupdate(@PathVariable("petname")String petname, @Valid PetProfileForm petProfileForm, @RequestParam(value = "petImage") MultipartFile newpetImage,
                            BindingResult bindingResult, Principal principal) throws Exception {
        Pet pet = petService.getpetByname(petname);

        if (petProfileForm.getPetImage() != null && !petProfileForm.getPetImage().isEmpty()) {
            imageService.saveImgsForPet(pet, petProfileForm.getPetImage());
        }

        petService.updatePet(pet, petProfileForm.getName(), petProfileForm.getContent());

        String encodedPetName = URLEncoder.encode(pet.getName(), "UTF-8");
        String encodedOwnerName = URLEncoder.encode(pet.getOwner().getProfileName(), "UTF-8");

        return "redirect:/profile/petprofile/" + encodedPetName + "/" + encodedOwnerName + "/1";
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




}
