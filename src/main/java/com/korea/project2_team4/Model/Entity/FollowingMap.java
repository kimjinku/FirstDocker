package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.mapping.ToOne;

@Entity
@Getter
@Setter

@Table(name = "following_map", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
public class FollowingMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile follower;

    @ManyToOne
    private Profile followee;

}
