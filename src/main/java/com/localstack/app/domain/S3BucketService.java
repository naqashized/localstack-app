package com.localstack.app.domain;

import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static com.localstack.app.api.config.AwsConfiguration.S3Configuration;

@Service
@RequiredArgsConstructor
public class S3BucketService implements FileUploadService{
    private final S3Configuration s3Configuration;

    private static final String S3_STORAGE  = "cars/";
    private static final String FILE_FORMAT = ".png";

    @Autowired
    private final S3Client s3Client;

    @Override
    public String upload(String base64EncodedImage) throws IOException {
        var randomName = UUID.randomUUID().toString();
        var filePath = S3_STORAGE +randomName+FILE_FORMAT;
        var request = PutObjectRequest
                .builder()
                .bucket(s3Configuration.getBucketName())
                .key(filePath)
                .build();
        var targetFile = new File("src/main/resources/targetFile.png");

        FileUtils.copyInputStreamToFile(base64InputStream(base64EncodedImage).get(), targetFile);
        var inputStream = base64InputStream(base64EncodedImage)
                .orElseThrow(() -> new IOException("Inputstream error"));
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, inputStream.available()));
        var getUrlRequest = GetUrlRequest
                .builder()
                .bucket(s3Configuration.getBucketName())
                .key(filePath)
                .build();
        return s3Client
                .utilities()
                .getUrl(getUrlRequest)
                .toExternalForm();
    }

    public Optional<InputStream> base64InputStream(String base64EncodedImage) {
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64EncodedImage);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        return Optional.ofNullable(inputStream);
    }
}
