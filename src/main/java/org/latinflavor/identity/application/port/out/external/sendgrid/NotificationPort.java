package org.latinflavor.identity.application.port.out.external.sendgrid;

public interface NotificationPort {
    void notify(String email, String code);
}
