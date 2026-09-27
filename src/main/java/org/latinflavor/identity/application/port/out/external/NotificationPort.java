package org.latinflavor.identity.application.port.out.external;

public interface NotificationPort {
    void notify(String email, String code);
}
