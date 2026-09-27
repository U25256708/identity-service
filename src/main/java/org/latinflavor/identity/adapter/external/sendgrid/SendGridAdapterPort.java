package org.latinflavor.identity.adapter.external.sendgrid;

import java.util.Map;

public interface SendGridAdapterPort {
    void notify(String email, Map<String, Object> templateData);
}
