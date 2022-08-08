package aws;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.Map;

@Service
@Getter @Setter
@Slf4j
public class AppService {
    private final S3Client s3;
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.bucket.path}")
    private String bucketPath;

    public AppService(S3Client s3) {
        this.s3 = s3;
    }

    // upload
    Map<String, String> upload(MultipartFile file) throws IOException {

        log.info("Uploading file ...");
        String key = System.currentTimeMillis() + file.getOriginalFilename(); // 765435665465github.jpeg

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(por, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        log.info("Upload complete.");

        return Map.of(
                "link", bucketPath + key
        );
    }

    // delete
    Map<String, String> delete(String fileLink) { // fileLink = url/key

        log.info("Deleting file...");

        try {

            String key = fileLink.substring(bucketPath.length());

            log.warn("Deleting object: {}", key);

            s3.deleteObject(dor -> dor.bucket(bucketName).key(key).build()); // dor = DeleteObjectRequest

        } catch (S3Exception e) {
            throw new IllegalStateException(e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return Map.of(
                "message", fileLink + " has been deleted."
        );
    }

}
