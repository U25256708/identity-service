package org.latinflavor.identity.adapter.external.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.latinflavor.identity.config.properties.SendGridProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SendGridAdapter implements SendGridAdapterPort {

    private final SendGridProperties properties;
    private final SendGrid sendGrid;

    public SendGridAdapter(SendGridProperties properties) {
        this.properties = properties;
        this.sendGrid = properties.isEnabled() ? new SendGrid(properties.getApiKey()) : null;
    }

    @Override
    public void notify(String email, Map<String, Object> templateData) {
        if (!properties.isEnabled()) {
            return;
        }
        Mail mail = new Mail();
        mail.setFrom(new Email(properties.getFromEmail()));
        mail.setTemplateId(properties.getTemplateId());

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(email));
        templateData.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        try {
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new IllegalStateException("SendGrid rejected the email with status " + response.getStatusCode());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to send email through SendGrid", exception);
        }
    }
}
