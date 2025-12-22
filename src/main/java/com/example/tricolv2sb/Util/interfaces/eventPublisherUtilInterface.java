package com.example.tricolv2sb.Util.interfaces;

import com.example.tricolv2sb.Entity.Enum.ActionName;
import com.example.tricolv2sb.Entity.UserApp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface eventPublisherUtilInterface {
    void triggerAuditLogEventPublisher(ActionName action, UserApp user,Map<String, String> additionalDetails);

    void triggerAuditLogEventPublisher(ActionName action, UserApp user);



    default Map<String, String> getIpAndPathFromContextHolder() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }

            String path = request.getRequestURI();

            Map<String, String> data = new HashMap<>();
            data.put("ip", ip != null ? ip : "unknown");
            data.put("path", path != null ? path : "unknown");

            return data;
        }

        return Collections.emptyMap();
    }
}
