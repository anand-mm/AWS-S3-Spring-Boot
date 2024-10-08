package com.s3.demo.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3.demo.Service.S3ServiceImp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/objects")
@RequiredArgsConstructor
@Tag(name = "S3 Object Operations", description = "APIs for interacting with AWS S3 Objects")
public class S3ObjectController {

    private final S3ServiceImp s3Service;

    @Operation(summary = "Upload a file to an S3 bucket", description = "Uploads a file to the specified bucket with a key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    // Upload a file to S3 bucket (MultipartFile support)
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("bucketName") String bucketName,
            @RequestParam("key") String key,
            @RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadFile(bucketName, key, file);
    }

    @Operation(summary = "List objects in an S3 bucket", description = "Lists all object keys in the specified bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of object keys returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Bucket not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/list-objects")
    public List<String> listObjectsInBucket(@RequestParam("bucketName") String bucketName) {
        return s3Service.listObjectsInBucket(bucketName);
    }

    @Operation(summary = "Download a file from S3 bucket", description = "Downloads the file from the specified S3 bucket and streams it to the client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/download")
    public void downloadFile(@RequestParam("bucketName") String bucketName,
            @RequestParam("key") String key,
            HttpServletResponse response) throws IOException {
        // Set response headers for file download
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + key + "\"");

        // Stream file content from S3 to the client via response's output stream
        s3Service.downloadFile(bucketName, key, response.getOutputStream());
    }

    @Operation(summary = "Delete a file from an S3 bucket", description = "Deletes a single object from the specified bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Object not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    // Delete a single object from S3 bucket
    @DeleteMapping("/delete")
    public String deleteObject(@RequestParam("bucketName") String bucketName,
            @RequestParam("key") String key) {
        return s3Service.deleteObject(bucketName, key);
    }

    @Operation(summary = "Delete multiple objects from an S3 bucket", description = "Deletes multiple objects from the specified bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objects deleted successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    // Delete multiple objects from S3 bucket
    @DeleteMapping("/delete-multiple")
    public String deleteMultipleObjects(@RequestParam("bucketName") String bucketName,
            @RequestParam("keys") List<String> keys) {
        return s3Service.deleteObjects(bucketName, keys);
    }
}
