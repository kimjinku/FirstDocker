package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Repository.ProfileRepository;
import com.korea.project2_team4.Repository.ResalePostRepository;
import com.korea.project2_team4.Repository.TagRepository;
import com.korea.project2_team4.Service.ResalePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final TagRepository tagRepository;
    private final ResalePostRepository resalePostRepository;

    private final ProfileRepository profileRepository;

    private final ResalePostService resalePostService;

    @Autowired
    public DataLoader(TagRepository tagRepository, ResalePostRepository resalePostRepository, ProfileRepository profileRepository, ResalePostService resalePostService) {
        this.tagRepository = tagRepository;
        this.resalePostRepository = resalePostRepository;
        this.profileRepository = profileRepository;
        this.resalePostService = resalePostService;
    }

    @Override
    public void run(String... args) throws Exception {
        createTags();
        saveTestResalePost();
    }

    private void createTags() {
        createTagIfNotExists("강아지");
        createTagIfNotExists("고양이");
        createTagIfNotExists("기타");
    }

    private void createTagIfNotExists(String tagName) {
        if (!tagRepository.existsByName(tagName)) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tagRepository.save(tag);
        }
    }
    public void saveTestResalePost() {
        if (resalePostRepository.findAll().isEmpty()) {

            for (int i = 1; i <= 30; i++) {
                ResalePost resalePost = new ResalePost();
                resalePost.setTitle(String.format("중고거래 제목 입니다:[%03d].", i));
                resalePost.setName("강아지 사료");
                resalePost.setContent("테스트 데이터 내용 입니다.");
                resalePost.setPrice("10000");
                resalePost.setCreateDate(LocalDateTime.now());
                String orderId = resalePostService.makeRandomCode(resalePost.getCreateDate());
                resalePost.setOrderId(orderId);
                Profile sellerProfile = profileRepository.findByProfileName("테스트유저1" ).get();
                resalePost.setSeller(sellerProfile);
                resalePostRepository.save(resalePost);

            }
        }
    }
}
