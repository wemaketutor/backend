package pdf_service.serivce;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioService(
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public boolean fileExists(String bucketName, String objectName) throws IOException {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new IOException("MinIO operation failed", e);
        } catch (InvalidKeyException | NoSuchAlgorithmException | 
                 MinioException | IllegalArgumentException e) {
            throw new IOException("MinIO operation failed", e);
        }
    }

    public void uploadFile(String bucketName, String objectName, String filePath)
            throws IOException, MinioException {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }

            // uploading
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(filePath)
                            .build());
        } catch (InvalidKeyException | NoSuchAlgorithmException | 
                 MinioException | IllegalArgumentException e) {
            throw new IOException("MinIO operation failed", e);
        }
    }

    public String getFileUrl(String bucketName, String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS) // expiration date
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate URL", e);
        }
    }
}