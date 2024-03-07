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
@Table(name = "tbl_post_group")
public class PostGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long groupId;
    private String content;
    @Column(name = "image_urls")
    private String imageUrlsString;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private LocalDateTime createdAt;
}
