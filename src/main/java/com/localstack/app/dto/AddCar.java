package com.localstack.app.dto;

public record AddCar(
        String series,
        int model,
        String image
) {
}
