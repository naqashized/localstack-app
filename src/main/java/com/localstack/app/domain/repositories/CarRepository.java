package com.localstack.app.domain.repositories;

import com.localstack.app.domain.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
}
