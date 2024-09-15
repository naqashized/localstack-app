package com.localstack.app.api.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(value = AwsConfiguration.S3Configuration.class)
@RequiredArgsConstructor
public class AwsConfiguration {
    private final S3Configuration s3Configuration;
    private static final String CAR_QUEUE_NAME = "car-queue";
    @Bean
    public S3Client s3Client(){
        var s3Client = S3Client
                .builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(s3Configuration.getRegion()))
                .endpointOverride(URI.create(s3Configuration.getEndpoint()))
                .forcePathStyle(true)
                .build();
        return s3Client;
    }

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.of(s3Configuration.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Configuration.getAccessKeyId(),
                                        s3Configuration.getAccessKeySecret()
                                )
                        )
                )
               // .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .applyMutation(builder -> {
                    builder.endpointOverride(URI.create(s3Configuration.getEndpoint()));
                })
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient
                .builder()
                .region(Region.of(s3Configuration.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Configuration.getAccessKeyId(),
                                        s3Configuration.getAccessKeySecret()
                                )
                        )
                )
                .applyMutation(builder -> {
                    builder.endpointOverride(URI.create(s3Configuration.getEndpoint()));
                })
                .build();
    }

    @Bean
    public String carQueueUrl() {
        var createQueRequest = CreateQueueRequest.builder()
                .queueName(CAR_QUEUE_NAME)
                .build();
        sqsClient().createQueue(createQueRequest);
        return sqsClient()
                .getQueueUrl(builder -> builder.queueName(CAR_QUEUE_NAME))
                .queueUrl();
    }


    @ConfigurationProperties("aws")
    @Getter
    @Setter
    public static class S3Configuration{
        private String endpoint;
        private String region;
        private String bucketName;
        private String accessKeyId;
        private String accessKeySecret;
    }

}
