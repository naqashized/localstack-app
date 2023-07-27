package com.localstack.app.domain;

import java.io.IOException;

public interface FileUploadService {
    String upload(String base64ImageEncoding) throws IOException;
}
