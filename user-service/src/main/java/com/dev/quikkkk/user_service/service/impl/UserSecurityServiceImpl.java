package com.dev.quikkkk.user_service.service.impl;

import com.dev.quikkkk.user_service.service.IUserSecurityService;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityServiceImpl implements IUserSecurityService {
    @Override
    public boolean canAccessUser(String targetUserId, String currentUserId) {
        return targetUserId.equals(currentUserId);
    }

    @Override
    public boolean canEditUser(String targetUserId, String currentUserId) {
        return targetUserId.equals(currentUserId);
    }
}
