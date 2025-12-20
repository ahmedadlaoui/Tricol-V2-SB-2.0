package com.example.tricolv2sb.Util.interfaces;

import com.example.tricolv2sb.Entity.UserApp;

public interface eventPublisherUtilInterface {
    public void triggerAuditLogEventPublisher(String action, UserApp user);
}
