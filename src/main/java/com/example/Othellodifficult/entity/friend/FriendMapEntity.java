package com.example.Othellodifficult.entity.friend;

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
    private Long id;
    private Long userId_1;
    private Long userId_2;
}
