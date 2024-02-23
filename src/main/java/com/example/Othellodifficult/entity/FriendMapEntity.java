package com.example.Othellodifficult.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name ="tbl_friend_map")
@Entity
public class FriendMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId1;
    private Long userId2;
}
