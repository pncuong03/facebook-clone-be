package com.example.Othellodifficult.repository.chatrepo;

import com.example.Othellodifficult.base.adapter.BaseRepository;
import com.example.Othellodifficult.entity.ChatEntity;

public interface NewChatRepository extends BaseRepository<ChatEntity> {
    ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2);
}
