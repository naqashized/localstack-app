package com.localstack.app.api;


import com.localstack.app.domain.CarService;
import com.localstack.app.domain.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final NotificationService notificationService;
    @GetMapping
    public List<com.localstack.app.dto.CarDetails> findCars(){
        notificationService.send();
        return carService.findAll();
    }

    @PostMapping
    public com.localstack.app.dto.CarDetails save(@RequestBody com.localstack.app.dto.AddCar addCar) throws IOException {
        return carService.save(addCar);
    }
}
