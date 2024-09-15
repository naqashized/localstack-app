package com.localstack.app;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.localstack.LocalStackContainer;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SES;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import org.junit.Rule;

@Testcontainers
@DirtiesContext
public class AbstractTestContainer {
    @Rule
    public static LocalStackContainer localstack =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack:latest")
            )
            .withServices(S3,SNS,SES,SQS);
    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");
    public static final String S3_BUCKET= "cars-bucket";


    @DynamicPropertySource
    private static void registerProperties(
            DynamicPropertyRegistry registry
    ) throws IOException, InterruptedException {
        localstack.start();
        var endpoint = getLocalstackEndpoint(localstack);
        registry.add("aws.endpoint", () -> endpoint);
        registry.add("aws.region", () -> localstack.getRegion());
        registry.add("aws.accessKeyId", () -> localstack.getAccessKey());
        registry.add("aws.accessKeySecret", () -> localstack.getSecretKey());
        registry.add("aws.bucketName", () -> S3_BUCKET);
        localstack.execInContainer(
            "awslocal",
            "s3",
            "mb",
            "s3://" + S3_BUCKET
        );

        localstack.execInContainer(
            "awslocal",
            "ses",
            "verify-email-identity",
            "--email-address",
            "no-reply@localstack.cloud"
        );
    }
    private static URI getLocalstackEndpoint(LocalStackContainer localstack) {
        return URI.create(
           "http://" +  localstack.getHost() + ":" + localstack.getMappedPort(4566)
        );
    }

    @NotNull
    protected MediaType mediaType() {
        return new MediaType(
            "application",
            "json",
            Charset.forName("UTF-8")
        );
    }
}
