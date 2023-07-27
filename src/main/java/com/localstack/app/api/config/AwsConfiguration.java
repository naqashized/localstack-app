package com.localstack.app.api.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(value = AwsConfiguration.S3Configuration.class)
@RequiredArgsConstructor
public class AwsConfiguration {
    private final S3Configuration s3Configuration;
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


    @ConfigurationProperties("aws.s3")
    @Getter
    @Setter
    public static class S3Configuration{
        private String endpoint;
        private String region;
        private String bucketName;
    }

}
