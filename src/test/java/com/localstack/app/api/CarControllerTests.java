package com.localstack.app.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localstack.app.domain.CarService;
import com.localstack.app.dto.NewCarRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
properties = {"spring.jpa.hibernate.ddl-auto:create"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class CarControllerTests {
    @Rule
    public static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(S3);
    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgresContainer = new  PostgreSQLContainer<>("postgres:latest");
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CarService carService;
    private static final String S3_BUCKET= "cars-bucket";


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

    @BeforeAll
    public static void start() throws IOException, InterruptedException {
        localstack.start();
        localstack.execInContainer("awslocal", "s3", "mb", "s3://" + S3_BUCKET);
    }

    @Test
    public void should_add_a_car() throws Exception {
        NewCarRequest newCarRequest = new NewCarRequest("M2", 2008,encodeImage());
        String request = objectMapper.writeValueAsString(newCarRequest);

        mockMvc.perform(post("/v1/cars").contentType(getMediaTypeJsonUtf8())
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.model").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").exists())
                .andDo(print());
    }

    @NotNull
    private static MediaType getMediaTypeJsonUtf8() {
        return new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
    }

    @Test
    public void should_find_all() throws Exception {
        NewCarRequest newCarRequest = new NewCarRequest("M2", 2008,encodeImage());
        carService.save(newCarRequest);

        mockMvc.perform(get("/v1/cars").contentType(getMediaTypeJsonUtf8()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray())
                .andDo(print());
    }

    private String encodeImage(){
        String filePath = "src/main/resources/targetFile.png";
        return Base64.getEncoder().encodeToString(filePath.getBytes());
    }
}
