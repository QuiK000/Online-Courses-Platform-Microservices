package com.dev.quikkkk.user_service.service;

public interface IUserSecurityService {
    boolean canAccessUser(String targetUserId, String currentUserId);

    boolean canEditUser(String targetUserId, String currentUserId);
}
