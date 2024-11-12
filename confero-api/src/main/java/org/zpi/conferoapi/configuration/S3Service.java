package org.zpi.conferoapi.configuration;

import lombok.RequiredArgsConstructor;
import org.openapitools.model.ErrorReason;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zpi.conferoapi.exception.ServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

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

            return getFileUrl(key);
        } catch (IOException e) {
            throw new ServiceException(ErrorReason.S3_UPLOAD_ERROR);
        }
    }

    private String getFileUrl(String key) {
        return String.format("https://yerxopcahxybrxkyxayf.supabase.co/storage/v1/object/public/%s/%s", bucket, key);
    }
}
