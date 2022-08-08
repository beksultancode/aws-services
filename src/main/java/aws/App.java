package aws;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Paths;

public class App {

    public static void main(String[] args) {

        Region region = Region.EU_CENTRAL_1;

        S3Client s3Client = S3Client.builder().region(region).build();

        String bucketName =  "java-temp"; // "bucket" + System.currentTimeMillis();
        String key = "github.jpeg"; // key = fileName

        createNewBucket(s3Client, bucketName, region);

        System.out.println("Uploading object...");

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucketName)
                        .key(key)
                                .build();

        s3Client.putObject(por, Paths.get("images/1*DbJrZ73enNNmnB_UxBDcmQ.jpeg"));

        System.out.println("Upload complete");
        System.out.printf("%n");


//        +++==========================================================================+++

        cleanUp(s3Client, bucketName, key);

        deleteBucket(s3Client, bucketName);
    }
    static void createNewBucket(S3Client s3Client, String newBucketName, Region region) {
        try {
            s3Client.createBucket(builder -> builder.bucket(newBucketName)
                    .createBucketConfiguration(cbcb -> cbcb.locationConstraint(region.id()).build()).build()); // cbcb -> CreateBucketConfigurationBuilder

            System.out.println("Creating bucket: " + newBucketName + " ...");

            s3Client.waiter().waitUntilBucketExists(hbr -> hbr.bucket(newBucketName).build()); // hbr = HeadBucketRequest

            System.out.println(newBucketName + " is ready.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    static void cleanUp(S3Client s3Client, String bucketName, String keyName) {
        System.out.println("Cleaning up...");
        try {
            System.out.println("Deleting object: " + keyName);

            s3Client.deleteObject(dor -> dor.bucket(bucketName).key(keyName).build()); // dor = DeleteObjectRequest

            System.out.println(keyName + " has been deleted.");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    static void deleteBucket(S3Client s3Client, String bucketName) {

        try {
            System.out.println("Deleting bucket: " + bucketName);

            s3Client.deleteBucket(dbr -> dbr.bucket(bucketName)); // dbr = DeleteBucketRequest

            System.out.println(bucketName + " has been deleted.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
