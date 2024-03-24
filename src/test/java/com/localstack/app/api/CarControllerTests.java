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
import com.localstack.app.AbstractTestContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.jpa.hibernate.ddl-auto:create"}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarControllerTests extends AbstractTestContainer {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CarService carService;

    @BeforeAll
    public static void start() throws IOException, InterruptedException {
        localstack.start();
        localstack.execInContainer("awslocal", "s3", "mb", "s3://" + AbstractTestContainer.S3_BUCKET);
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
