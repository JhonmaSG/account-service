package com.finance.accountservice.security.currentuser;

/**
 * Service interface for retrieving the currently authenticated user's
 * information from the security context.
 */
public interface CurrentUserService {

    String getCurrentUsername();

    boolean isAdmin();
}
