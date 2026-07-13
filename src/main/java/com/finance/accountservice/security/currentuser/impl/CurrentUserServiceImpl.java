package com.finance.accountservice.security.currentuser.impl;

import com.finance.accountservice.security.currentuser.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CurrentUserService}. Retrieves the current
 * username and role from Spring Security's SecurityContextHolder.
 */
@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public String getCurrentUsername() {
        return getAuthentication().getName();
    }

    @Override
    public boolean isAdmin() {

        return getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Authentication getAuthentication() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authenticated user");
        }

        return authentication;
    }
}