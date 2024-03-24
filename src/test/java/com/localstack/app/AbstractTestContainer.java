package com.localstack.app;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.localstack.LocalStackContainer;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import java.net.URI;
import org.junit.Rule;

@Testcontainers
@DirtiesContext
public class AbstractTestContainer {
    @Rule
    public static LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(S3,SNS);
    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");
    public static final String S3_BUCKET= "cars-bucket";


    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        var endpoint = getLocalstackEndpoint(localstack);
        registry.add("aws.s3.endpoint", () -> endpoint);
        registry.add("aws.s3.region", () -> localstack.getRegion());
        registry.add("aws.s3.accessKeyId", () -> localstack.getAccessKey());
        registry.add("aws.s3.secretAccessKey", () -> localstack.getSecretKey());
        registry.add("aws.s3.bucketName", () -> S3_BUCKET);
    }
    private static URI getLocalstackEndpoint(LocalStackContainer localstack) {
        return URI.create("http://" +  localstack.getHost() + ":" + localstack.getMappedPort(4566));
    }
}
