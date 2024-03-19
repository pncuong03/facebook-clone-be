package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.event.NotificationOutput;
import com.example.Othellodifficult.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notification")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo")
    @GetMapping
    public Page<NotificationOutput> getNotifies(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                                @ParameterObject Pageable pageable){
        return notificationService.getNotifies(accessToken, pageable);
    }
}
