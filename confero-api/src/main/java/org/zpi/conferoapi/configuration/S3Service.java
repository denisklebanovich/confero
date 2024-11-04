package org.zpi.conferoapi.configuration;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.ErrorReason;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.exception.ServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${supabase.s3.bucket}")
    private String bucket;
    private final S3Client s3Client;

    public String uploadFile(String key, MultipartFile file) {
        try {
            s3Client.putObject(request ->
                            request.bucket(bucket).key(key),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toExternalForm();
        } catch (IOException e) {
            throw new ServiceException(ErrorReason.S3_UPLOAD_ERROR);
        }
    }
}
