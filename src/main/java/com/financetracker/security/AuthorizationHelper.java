package com.financetracker.security;

import com.financetracker.entity.User;
import com.financetracker.entity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHelper {
    // Check if user id admin
    public boolean isAdmin(User user){
        return Role.ADMIN.equals(user.getRole());
    }

    // Check if user is the owner of the resource
    public boolean isOwner(User currentUser, Long resourceOwnerId){
        return currentUser.getId().equals(resourceOwnerId);
    }

    // Check if the user can access the resource
    public boolean canAccess(User currentUser, Long resourceOwnerId){
        return isOwner(currentUser, resourceOwnerId) || isAdmin(currentUser);
    }
}
