package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Dto.ProfileDto;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Pet;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Repository.ImageRepository;
import com.korea.project2_team4.Repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Builder
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ImageService imageService;
    private final FollowingMapService followingMapService;
//    private final PostService postService;


    public boolean deleteExistingFile(String existingFilePath) {
        if (existingFilePath != null && !existingFilePath.isEmpty()) {
            File existingFile = new File(existingFilePath);
            if (existingFile.exists()) {
                return existingFile.delete();
            }
        }
        return false;
    }

    //    public void deletePet(Pet pet) {
//        String filepath = pet.getPetImage().getFilePath();
//        if (filepath != null && !filepath.isEmpty()) {
//            deleteExistingFile(filepath);
//        }
//        this.petRepository.delete(pet);
//    }

    public ProfileDto getDefaultProfileDto(Profile profile) {
        List<Profile> followers = followingMapService.getMyfollowers(profile);
        List<Profile> followings = followingMapService.getMyfollowings(profile);

        return new ProfileDto(null, null, followers, followings);
    }

//    public ProfileDto getProfileDtoByNotLogined(Post thispost) {
//        Profile targetprofile = thispost.getAuthor();
//        ProfileDto profileDto = getDefaultProfileDto(targetprofile);
//        List<Post> postList = postService.getPostsbyAuthor(thispost.getAuthor());
//        profileDto.setProfile(targetprofile);
//        profileDto.setPostList(postList);
//
//        return profileDto;
//    }
//
//    public ProfileDto getProfileDtoByLoginId(Member member) {
//        ProfileDto profileDto = getDefaultProfileDto(member.getProfile());
//        List<Post> postList = postService.getPostsbyAuthor(member.getProfile());
////        profileDto.setProfile(targetprofile);
//        profileDto.setPostList(postList);
//
//        return profileDto;
//    }


    public List<Profile> getProfilelist() {
        return this.profileRepository.findAll();
    }

    public Profile getProfileById(Long profileId) {
        return this.profileRepository.findById(profileId).get();
    }

    public Profile getProfileByName(String profileName) {
        return this.profileRepository.findByProfileName(profileName).get();
    }
    public Profile getProfile(String profileName) {
        return this.profileRepository.findByProfileName(profileName).orElse(null);
    }
    public void updateprofile(Profile profile, String profilename, String content) {
        profile.setProfileName(profilename);
        profile.setContent(content);
        this.profileRepository.save(profile);
    }

    public Profile setDefaultProfile(Member member) {
        Profile profile = new Profile();
        profile.setMember(member);
        String randomString = UUID.randomUUID().toString();
        profile.setProfileName(member.getNickName()+randomString);
        profile.setContent(" ");

        this.profileRepository.save(profile);
        try {
            imageService.saveDefaultImgsForProfile(profile);
        } catch(Exception e) {

        }
        return this.profileRepository.save(profile);

    }

    public void setPetforprofile(Profile profile, Pet pet) {
        profile.getPetList().add(pet);
        this.profileRepository.save(profile);
    }
    public void delete(Profile profile){
        profileRepository.delete(profile);
    }


    public List<Profile> getAllProfileBykw(String kw) {
        return this.profileRepository.findAllBykw(kw);
    }
}
