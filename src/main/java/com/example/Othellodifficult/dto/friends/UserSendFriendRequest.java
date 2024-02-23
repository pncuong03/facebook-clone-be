package com.example.Othellodifficult.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSendFriendRequest {
    private Long senderId;
    private Long receiverId;
}
