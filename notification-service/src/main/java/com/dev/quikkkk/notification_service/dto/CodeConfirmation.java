package com.dev.quikkkk.notification_service.dto;

public record CodeConfirmation(
        String code,
        String email,
        String userId
) {
}
