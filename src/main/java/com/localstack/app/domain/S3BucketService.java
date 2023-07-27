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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static com.localstack.app.api.config.AwsConfiguration.*;

@Service
@RequiredArgsConstructor
public class S3BucketService implements FileUploadService{
    private final S3Configuration s3Configuration;

    private static final String S3_STORAGE  = "cars/";
    private static final String FILE_FORMAT = ".jpg";

    @Autowired
    private final S3Client s3Client;

    @Override
    public String upload(String base64EncodedImage) throws IOException {
        String randomName = UUID.randomUUID().toString();
        String filePath = S3_STORAGE +randomName+".png";
        PutObjectRequest request = PutObjectRequest.builder().bucket(s3Configuration.getBucketName()).key(filePath)
                .build();
        System.out.println("Randome key "+filePath);
        File targetFile = new File("src/main/resources/targetFile.png");

        FileUtils.copyInputStreamToFile(base64InputStream(base64EncodedImage).get(), targetFile);
                //new PutObjectRequest(s3Configuration.getBucketName(), randomName, base64InputStream(base64EncodedImage).get(), null);
        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromInputStream(base64InputStream(base64EncodedImage).get(), 20000));
        var getUrlRequest = GetUrlRequest.builder().bucket(s3Configuration.getBucketName()).key(filePath).build();
        return s3Client.utilities().getUrl(getUrlRequest).toExternalForm();
    }

    public Optional<InputStream> base64InputStream(String base64EncodedImage)throws IOException {
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64EncodedImage);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        return Optional.ofNullable(inputStream);
    }
}
