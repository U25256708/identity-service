package org.latinflavor.identity.adapter.external.sendgrid;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.external.sendgrid.NotificationPort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationAdapter implements NotificationPort {

    private final SendGridAdapterPort sendGridAdapter;

    @Override
    public void notify(String email, String code) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("code", code);
        sendGridAdapter.notify(email, templateData);
    }
}
