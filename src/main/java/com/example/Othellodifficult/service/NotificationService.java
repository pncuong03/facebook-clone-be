package com.example.Othellodifficult.service;

import com.example.Othellodifficult.base.filter.Filter;
import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.event.NotificationOutput;
import com.example.Othellodifficult.dto.user.UserOutput;
import com.example.Othellodifficult.entity.NotificationEntity;
import com.example.Othellodifficult.entity.UserChatMapEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.mapper.NotificationMapper;
import com.example.Othellodifficult.repository.EventNotificationRepository;
import com.example.Othellodifficult.repository.NotificationRepository;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.EventHelper;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final EventNotificationRepository eventNotificationRepository;

    // thread != method thread
    @Transactional
    public Page<NotificationOutput> getNotifies(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        eventNotificationRepository.deleteAllByUserIdAndEventType(userId, Common.NOTIFICATION);

        Page<NotificationEntity> notificationEntities = Filter.builder(NotificationEntity.class, entityManager)
                .filter()
                .isEqual("userId", userId)
                .orderBy("createdAt", Common.DESC)
                .getPage(pageable);

        if (notificationEntities.isEmpty()) {
            return Page.empty();
        }

        Set<Long> userIds = new HashSet<>();
        List<NotificationEntity> noSeenNotifyEntities = new ArrayList<>();
        for (NotificationEntity notificationEntity : notificationEntities) {
            if (Objects.nonNull(notificationEntity.getInteractId())) {
                userIds.add(notificationEntity.getInteractId());
            }
            if (Boolean.FALSE.equals(notificationEntity.getHasSeen())) {
                noSeenNotifyEntities.add(notificationEntity);
            }
        }
        Map<Long, UserEntity> interactMap = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        if (!noSeenNotifyEntities.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                for (NotificationEntity notificationEntity : noSeenNotifyEntities) {
                    notificationEntity.setHasSeen(true);
                    notificationRepository.save(notificationEntity);
                }
            });
        }

        return notificationEntities.map(notificationEntity -> {
            NotificationOutput notificationOutput = notificationMapper.getOutputFromEntity(notificationEntity);
            if (Objects.nonNull(notificationEntity.getInteractId())) {
                UserEntity interact = interactMap.get(notificationEntity.getInteractId());
                notificationOutput.setInteract(
                        UserOutput.builder()
                                .id(interact.getId())
                                .fullName(interact.getFullName())
                                .imageUrl(interact.getImageUrl())
                                .build()
                );
            }
            return notificationOutput;
        });
    }
}
