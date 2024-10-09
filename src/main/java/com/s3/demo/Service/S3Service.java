package com.s3.demo.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;

public interface S3Service {
    public String createBucket(String bucketName);

    public List<String> listAllBuckets();

    // public boolean doesBucketExist(String bucketName);

    public String deleteBucket(String bucketName);

    public String uploadFile(String bucketName, String key, MultipartFile file);

    public List<String> listObjectsInBucket(String bucketName);

    public void downloadFile(String bucketName, String key, ServletOutputStream outputStream);

    public String deleteObject(String bucketName, String key);

    public String deleteObjects(String bucketName, List<String> objectKeys);
}
