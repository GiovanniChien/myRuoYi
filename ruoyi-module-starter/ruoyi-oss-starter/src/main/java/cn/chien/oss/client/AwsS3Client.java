package cn.chien.oss.client;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

/**
 * @author qiandq3
 * @date 2022/11/17
 */
public class AwsS3Client implements OssClient {
    
    private final S3Client s3Client;
    
    private final S3Presigner s3Presigner;
    
    public AwsS3Client(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }
    
    @Override
    public void createBucket(String bucketName) {
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3Client.createBucket(bucketRequest);
    }
    
    @Override
    public String getObjectUrl(String bucketName, String objectName) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();
    
        URL url = s3Client.utilities().getUrl(request);
        return url.toString();
    }
    
    @Override
    public String getObjectUrl(String bucketName, String objectName, boolean isPrivate) {
        if (!isPrivate) {
            return getObjectUrl(bucketName, objectName);
        }
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();
    
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();
        
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }
    
    @Override
    public ResponseInputStream<GetObjectResponse> getObjectInfo(String bucketName, String objectName) {
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(objectName)
                .bucket(bucketName)
                .build();
        return s3Client.getObject(objectRequest);
    }
    
    @Override
    public PutObjectResponse putObject(String bucketName, String objectName, InputStream stream, long size) {
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(stream, size);
    
        return s3Client.putObject(putOb, requestBody);
    }
}
