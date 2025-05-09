package pdf_service.serivce;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IOException("MinIO client error", e);
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