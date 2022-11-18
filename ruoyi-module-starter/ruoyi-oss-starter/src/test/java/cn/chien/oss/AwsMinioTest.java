package cn.chien.oss;

import cn.chien.oss.client.AwsS3Client;
import cn.chien.oss.client.OssClient;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

/**
 * @author qiandq3
 * @date 2022/11/17
 */
public class AwsMinioTest {
    
    private S3Client s3Client;
    
    private OssClient ossClient;
    
    @Before
    public void before() {
        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("eJcO8BxkpAgA7gWq", "XIjuFwTYoHpTQzGLIGHJc7rJx46kZUyx")))
                .endpointOverride(URI.create("http://127.0.0.1:9000")).serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .chunkedEncodingEnabled(false)
                                .build())
                .region(Region.of("us-east-1"))
                .build();
        S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of("us-east-1"))
                .endpointOverride(URI.create("http://127.0.0.1:9000"))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .checksumValidationEnabled(false)
                                .pathStyleAccessEnabled(true)
                                .chunkedEncodingEnabled(false)
                                .build())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("eJcO8BxkpAgA7gWq", "XIjuFwTYoHpTQzGLIGHJc7rJx46kZUyx")))
                .build();
        ossClient = new AwsS3Client(s3Client, s3Presigner, "ruoyi", false);
    }
    
    @Test
    public void test_put_object() throws IOException {
        FileInputStream inputStream = new FileInputStream(
                "D:\\upload_dir\\avatar\\2022\\11\\11\\blob_20221111153119A001.png");
        ossClient.putObject("ruoyi", "blob_20221111153119A001.png", inputStream);
    }
    
    @Test
    public void test_get_object_url() {
        String path = ossClient.getObjectUrl("ruoyi", "blob_20221111153119A001.png");
        System.out.println(path);
    }
    
}
