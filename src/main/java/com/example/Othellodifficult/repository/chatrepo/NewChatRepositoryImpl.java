package com.example.Othellodifficult.repository.chatrepo;

import com.example.Othellodifficult.base.adapter.BaseRepositoryAdapter;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.repository.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class NewChatRepositoryImpl extends BaseRepositoryAdapter<ChatEntity> implements NewChatRepository {
    private final ChatRepository chatRepository;

    @Override
    public ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2) {
        return chatRepository.findByUserId1AndUserId2(userId1, userId2);
    }
}
