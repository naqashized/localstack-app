package com.localstack.app.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localstack.app.domain.CarService;
import com.localstack.app.dto.AddCar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

    @Test
    public void should_add_a_car() throws Exception {
        var addCar = new AddCar("M2", 2008,encodeImage());
        var request = objectMapper.writeValueAsString(addCar);

        mockMvc.perform(post("/v1/cars").contentType(mediaType())
            .content(request)
        )
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.model").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").exists());
    }



    @Test
    public void should_find_all() throws Exception {
        var newCarRequest = new AddCar("M2", 2008,encodeImage());
        carService.save(newCarRequest);

        mockMvc.perform(get("/v1/cars"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray())
        .andDo(print());
    }

    private String encodeImage(){
        var imageLink =
                "https://www.logo.wine/a/logo/Amazon_Web_Services/Amazon_Web_Services-Logo.wine.svg";
        return Base64.getEncoder()
                .encodeToString(imageLink.getBytes());
    }
}
