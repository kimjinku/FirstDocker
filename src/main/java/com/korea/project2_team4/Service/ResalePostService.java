package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Repository.MemberRepository;
import com.korea.project2_team4.Repository.ProfileRepository;
import com.korea.project2_team4.Repository.ResalePostRepository;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Service
public class ResalePostService {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final ImageService imageService;
    private final ResalePostRepository resalePostRepository;
    private final ProfileRepository profileRepository;
    public Page<ResalePost> resalePostList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return resalePostRepository.findAll(pageable);
    }

    public ResalePost getResalePost(Long id) {
        Optional<ResalePost> postOptional = resalePostRepository.findById(id);
        return postOptional.orElse(null);

    }

    public ResalePost getResalePostIncrementView(Long id) {
        Optional<ResalePost> postOptional = resalePostRepository.findById(id);
        if (postOptional.isPresent()) {
            ResalePost postView = postOptional.get();
            postView.setView(postView.getView() + 1);
            return resalePostRepository.save(postView);
        }
        return postOptional.orElse(null);
    }

}
