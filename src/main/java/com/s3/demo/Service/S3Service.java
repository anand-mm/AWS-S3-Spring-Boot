package com.s3.demo.Service;

import java.util.List;

public interface S3Service {
    public String createBucket(String bucketName);

    public List<String> listAllBuckets();

    public boolean doesBucketExist(String bucketName);

    public String deleteBucket(String bucketName);
}
