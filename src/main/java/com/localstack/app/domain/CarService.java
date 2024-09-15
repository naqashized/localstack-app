package com.localstack.app.domain;

import com.localstack.app.domain.models.Car;
import com.localstack.app.domain.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final FileUploadService s3BucketService;

    public com.localstack.app.dto.CarDetails save(com.localstack.app.dto.AddCar addCar) throws IOException {
        var car = transformDto(addCar);
        var newCar = carRepository.save(car);
        return new com.localstack.app.dto.CarDetails(newCar.getId(), newCar.getModel(), newCar.getSeries(), newCar.getImageUrl());
    }

    public List<com.localstack.app.dto.CarDetails> findAll(){
        return carRepository.findAll()
                .stream()
                .map(car -> new com.localstack.app.dto.CarDetails(
                        car.getId(),
                        car.getModel(),
                        car.getSeries(),
                        car.getImageUrl())
                ).toList();
    }

    private Car transformDto(com.localstack.app.dto.AddCar addCar) throws IOException {
        var car = new Car();
        car.setSeries(addCar.series());
        car.setModel(addCar.model());
        car.setImageUrl(s3BucketService.upload(addCar.image()));
        return car;
    }
}
