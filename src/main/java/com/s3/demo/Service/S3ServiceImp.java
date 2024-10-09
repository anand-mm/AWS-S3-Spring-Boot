package com.s3.demo.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.BucketCannedACL;
import software.amazon.awssdk.services.s3.model.BucketVersioningStatus;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Service {

    private final S3Client s3Client;

    // public boolean doesBucketExist(String bucketName) {
    // ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
    // List<Bucket> buckets = listBucketsResponse.buckets();

    // Optional<Bucket> matchedBucket = buckets.stream()
    // .filter(bucket -> bucket.name().equals(bucketName))
    // .findFirst();

    // return matchedBucket.isPresent();
    // }

    public String createBucket(String bucketName) {
        try {
            // if (!doesBucketExist(bucketName)) {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .acl(BucketCannedACL.PRIVATE)
                    .bucket(bucketName)
                    .build();

            CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);
            return "Bucket created: " + createBucketResponse.location();
            // } else {
            // return "Bucket already exists.";
            // }
        } catch (S3Exception exception) {
            return "Error creating bucket: " + exception.awsErrorDetails().errorMessage();
        } catch (SdkClientException e) {
            return "SDK Error: " + e.getMessage();
        }
    }

    public List<String> listAllBuckets() {
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
        return listBucketsResponse.buckets()
                .stream()
                .map(Bucket::name)
                .collect(Collectors.toList());
    }

    // public boolean doesBucketExist(String bucketName) {
    // HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
    // .bucket(bucketName)
    // .build();

    // try {
    // s3Client.headBucket(headBucketRequest);
    // return true;
    // } catch (NoSuchBucketException e) {
    // return false;
    // }
    // }

    // Delete a bucket (must be empty first)
    public String deleteBucket(String bucketName) {
        // if (!doesBucketExist(bucketName)) {
        // return "Bucket does not exist.";
        // }

        try {
            // Attempt to delete the bucket
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.deleteBucket(deleteBucketRequest);
            return "Bucket '" + bucketName + "' has been deleted successfully.";
        } catch (S3Exception e) {
            // Handle specific S3 exception like bucket not empty
            return "Error deleting bucket: " + e.awsErrorDetails().errorMessage();
        } catch (SdkClientException e) {
            return "SDK Error: " + e.getMessage();
        }
    }

    public String uploadFile(String bucketName, String key, MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Uploading file content as InputStream directly to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return "File uploaded successfully with key: " + key;
    }

    public List<String> listObjectsInBucket(String bucketName) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        return listObjectsV2Response.contents()
                .stream()
                .map(S3Object::key) // Get the object keys
                .collect(Collectors.toList());
    }

    public void downloadFile(String bucketName, String key, ServletOutputStream outputStream) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Fetch the file from S3
            try (ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(getObjectRequest)) {
                // Stream the content to the ServletOutputStream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = s3ObjectStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String deleteObject(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        return "Object with key '" + key + "' deleted from bucket '" + bucketName + "'";
    }

    public String deleteObjects(String bucketName, List<String> objectKeys) {
        List<ObjectIdentifier> objects = objectKeys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objects).build())
                .build();

        s3Client.deleteObjects(deleteObjectsRequest);

        return "Deleted " + objectKeys.size() + " objects from bucket '" + bucketName + "'";
    }

}
