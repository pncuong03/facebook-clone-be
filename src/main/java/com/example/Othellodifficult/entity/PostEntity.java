package com.example.Othellodifficult.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tbl_post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // id = 1 ; 2
    private Long userId; // userId = 3; 2
    private String content;
    @Column(name = "image_urls")
    private String imageUrlsString;

    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;

    private Long shareId;
    private String state;

    private LocalDateTime createdAt;
}
