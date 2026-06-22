package com.finance.accountservice.security.currentuser;

public interface CurrentUserService {

    String getCurrentUsername();

    boolean isAdmin();
}
