package com.example.tricolv2sb.Util.interfaces;

import com.example.tricolv2sb.Entity.UserApp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface currentUserGetterInterface {

    public UserApp getCurrentUser();
}
