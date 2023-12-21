package com.localstack.app.domain;

import com.localstack.app.domain.models.Car;
import com.localstack.app.domain.repositories.CarRepository;
import com.localstack.app.dto.CarDTO;
import com.localstack.app.dto.NewCarRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final FileUploadService s3BucketService;

    public CarDTO save(NewCarRequest newCarRequest) throws IOException {
        var car = transformDto(newCarRequest);
        var newCar = carRepository.save(car);
        return new CarDTO(newCar.getId(), newCar.getModel(), newCar.getSeries(), newCar.getImageUrl());
    }

    public List<CarDTO> findAll(){
        return carRepository.findAll()
                .stream()
                .map(car -> new CarDTO(
                        car.getId(),
                        car.getModel(),
                        car.getSeries(),
                        car.getImageUrl())
                ).toList();
    }

    private Car transformDto(NewCarRequest newCarRequest) throws IOException {
        var car = new Car();
        car.setSeries(newCarRequest.series());
        car.setModel(newCarRequest.model());
        car.setImageUrl(s3BucketService.upload(newCarRequest.image()));
        return car;
    }
}
