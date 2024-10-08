package com.s3.demo.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.s3.demo.Service.S3ServiceImp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bucket")
@Tag(name = "S3 Bucket Operations", description = "APIs to interact with AWS S3 Buckets")
public class S3Controller {

    private final S3ServiceImp s3Service;

    @Operation(summary = "Create a new S3 bucket", description = "Creates a new S3 bucket with the specified name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bucket created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid bucket name provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/create-bucket")
    public String createBucket(@RequestParam("bucketName") String bucketName) {
        return s3Service.createBucket(bucketName);
    }

    @Operation(summary = "List all S3 buckets", description = "Returns a list of all S3 buckets available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of buckets returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/list-buckets")
    public List<String> listAllBuckets() {
        return s3Service.listAllBuckets();
    }

    @Operation(summary = "Delete an S3 bucket", description = "Deletes the specified S3 bucket by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bucket deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Bucket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete-bucket")
    public String deleteBucket(@RequestParam("bucketName") String bucketName) {
        return s3Service.deleteBucket(bucketName);
    }
}
