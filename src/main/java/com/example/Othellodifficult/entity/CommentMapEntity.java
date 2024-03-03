package com.example.Othellodifficult.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name ="tbl_comment_map")
@Entity
@Builder
public class CommentMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postId;
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;
    private Long commentId;
}
