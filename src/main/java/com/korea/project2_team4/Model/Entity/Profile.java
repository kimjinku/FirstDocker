package com.korea.project2_team4.Model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.korea.project2_team4.Repository.ProfileRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
//    @JoinColumn(name = "member_id") // 연관 관계의 주인을 명시
    private Member member;


    @OneToOne(mappedBy = "profileImage", cascade = CascadeType.REMOVE)
    private Image profileImage;

    @Column(unique = true, nullable = false)
    private String profileName;


    private String content;

    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE )
    private List<ResalePost> resalePostList;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE )
    private List<Pet> petList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE )
    private List<Post> postList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE )
    private List<Comment> commentList;



    @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE )
    private Set<FollowingMap> followerMaps;

    @OneToMany(mappedBy = "followee")
    private Set<FollowingMap> followeeMaps = new HashSet<>();




    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE )
    private List<Message> myMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE )
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "me",cascade = CascadeType.REMOVE)
    private List<DmPage> myDmpageList;

    @OneToMany(mappedBy = "partner")
    private List<DmPage> otherDmpageList;


    //    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE )
//    private List<SaveMessage> saveMessages;





}
