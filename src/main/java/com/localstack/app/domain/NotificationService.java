package com.localstack.app.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SesClient sesClient;

    public void send(){
        sesClient.sendEmail(notificationToEmail());
    }

    public SendEmailRequest notificationToEmail() {
        return SendEmailRequest.builder().applyMutation(email -> {
            email.message(msg -> {
                msg.body(body -> {
                    body.text(text -> {
                        text.data("Receieve email");
                    });
                }).subject(subject -> {
                    subject.data("Test from Local");
                });
            }).destination(dest -> {
                dest.toAddresses("tahirnaqashized@gmail.com");
            }).source("no-reply@localstack.cloud");
        }).build();
    }
}
