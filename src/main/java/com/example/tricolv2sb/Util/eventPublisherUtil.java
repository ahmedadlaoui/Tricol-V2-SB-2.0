package com.example.tricolv2sb.Util;


import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Event.AuditLogEvent;
import com.example.tricolv2sb.Util.interfaces.eventPublisherUtilInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class eventPublisherUtil implements eventPublisherUtilInterface {

    private final ApplicationEventPublisher eventPublisher;

    public void triggerAuditLogEventPublisher(String action, UserApp user) {

        Map<String, String> requestInfos = getIpAndPathFromContextHolder();
        eventPublisher.publishEvent(
                new AuditLogEvent(user.getEmail(), action, Map.of("User_ID", user.getId(), "Role", user.getRole().getName(), "IP_adress", requestInfos.get("ip"), "path", requestInfos.get("path")))
        );
    }
}
