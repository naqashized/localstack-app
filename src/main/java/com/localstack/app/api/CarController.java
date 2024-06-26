package com.localstack.app.api;


import com.localstack.app.domain.CarService;
import com.localstack.app.domain.NotificationService;
import com.localstack.app.dto.CarDTO;
import com.localstack.app.dto.NewCarRequest;
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
    public List<CarDTO> findCars(){
        notificationService.send();
        return carService.findAll();
    }

    @PostMapping
    public CarDTO save(@RequestBody NewCarRequest newCarRequest) throws IOException {
        return carService.save(newCarRequest);
    }
}
