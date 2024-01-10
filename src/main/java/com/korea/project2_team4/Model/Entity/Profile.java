package com.korea.project2_team4.Model.Entity;

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
    private Member member;


    @OneToOne(mappedBy = "profileImage", cascade = CascadeType.REMOVE)
    private Image profileImage;

    private String profileName;
    private String content;

    private LocalDateTime modifyDate;


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

    @OneToOne(mappedBy = "me", cascade = CascadeType.REMOVE)
    private DmPage myDm;


    //    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE )
//    private List<SaveMessage> saveMessages;





}
