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
public class ResalePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile seller;

    private String title;

    private String content;

    private String price;

    @Column(columnDefinition = "VARCHAR(100)")
    private String category;

    @OneToMany(mappedBy = "resalePostImages", cascade = CascadeType.REMOVE )
    private List<Image> resalePostImages;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToMany
    private Set<Profile> wishProfiles;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    private boolean soldItem;

    private String name;

    private String orderId;

}

