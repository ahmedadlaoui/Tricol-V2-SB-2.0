package com.example.tricolv2sb.Util;

import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Util.interfaces.currentUserGetterInterface;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class currentUserGetter implements currentUserGetterInterface {

    @Override
    public UserApp getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserApp) {
            return (UserApp) auth.getPrincipal();
        }

        return null;
    }
}