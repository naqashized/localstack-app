package com.localstack.app.domain;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@lombok.AllArgsConstructor
public class CarConsumer {
    private SqsClient sqsClient;
    private String carQueueUrl;

    @Scheduled(fixedRate = 100)
    public void pullMessagesFromQueue() {
        System.out.println("Pulling messages from queue");
        var receiveMessageRequest = ReceiveMessageRequest
                .builder()
                .queueUrl(carQueueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .build();

        var messages = sqsClient.receiveMessage(receiveMessageRequest);
        messages.messages().forEach(message -> {
            System.out.println("Message received: " + message.body());
        });
    }
}

