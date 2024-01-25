package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TagMap> tagMaps;

    @Column(columnDefinition = "VARCHAR(100)")
    private String category;

    @ManyToOne
    private Profile author;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;


    @OneToMany(mappedBy = "postImages", cascade = CascadeType.REMOVE )
    private List<Image> postImages;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE )
    private List<Comment> comments;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToMany
    private Set<Member> likeMembers;

    public List<Tag> getDefaultTagsToPostTagList(List<Tag> defaultTags) {
        List<Tag> tagList = getTagList();
        for(Tag defaultTag : defaultTags) {
            if(!tagList.contains(defaultTag)) {
                tagList.add(defaultTag);
            }
        }

        return tagList;
    }
    public List<Tag> getTagList() {

        List<Tag> tagList = new ArrayList<>();

        for(TagMap tagmap : tagMaps) {
            Tag tag = tagmap.getTag();
            tagList.add(tag);
        }

        return tagList;
//          위와 동일 기능
//        return this.tagMaps.stream()
//                .map(TagMap::getTag) // 값 추출
//                .collect(Collectors.toList()); // 리스트로 변환
    }


    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE )
    private List<Report> reports;
//    public List<String> getPostReportCategories() {
//        List<String> getReportCategories = new ArrayList<>();
//        for (Report report : reports) {
//            getReportCategories.addAll(report.getCategory());
//        }
//        return getReportCategories;
//    }
//
//    public String getPostReportContent() {
//        String getPostReportContent = new String();
//        for (Report report : reports) {
//            getPostReportContent = report.getContent();
//        }
//        return getPostReportContent;
//    }


}
