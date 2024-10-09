package com.s3.demo.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretAccessKey}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

}
