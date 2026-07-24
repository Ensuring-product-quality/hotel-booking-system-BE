package com.hotelnow.backend.service;

import com.hotelnow.backend.entity.Role;
import com.hotelnow.backend.entity.User;
import com.hotelnow.backend.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class CurrentUserService {
    private static final EnumSet<Role> STAFF_ROLES =
            EnumSet.of(Role.STAFF, Role.MANAGER, Role.ADMIN);
    private static final EnumSet<Role> MANAGEMENT_ROLES =
            EnumSet.of(Role.MANAGER, Role.ADMIN);

    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Authentication is required");
        }
        return principal.getUser();
    }

    public boolean isStaff(User user) {
        return STAFF_ROLES.contains(user.getRole());
    }

    public boolean isManagement(User user) {
        return MANAGEMENT_ROLES.contains(user.getRole());
    }

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public void requireSelfOrStaff(Long userId) {
        User current = requireCurrentUser();
        if (!current.getId().equals(userId) && !isStaff(current)) {
            throw new AccessDeniedException("You cannot access another user's data");
        }
    }

    public void requireSelfOrAdmin(Long userId) {
        User current = requireCurrentUser();
        if (!current.getId().equals(userId) && !isAdmin(current)) {
            throw new AccessDeniedException("You cannot modify another user's account");
        }
    }
}
