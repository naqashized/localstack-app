package com.localstack.app.dto;

public record CarDetails(
        int id,
        int model,
        String series,
        String imageUrl
) {
}

