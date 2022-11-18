package cn.chien.oss;

import cn.chien.oss.client.AwsS3Client;
import cn.chien.oss.client.OssClient;
import cn.chien.oss.properties.S3Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * @author qiandq3
 * @date 2022/11/17
 */
@Configuration
@EnableConfigurationProperties(S3Properties.class)
@ConditionalOnProperty(prefix = "aws.s3", value = "enable", havingValue = "true", matchIfMissing = true)
public class OssAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(OssClient.class)
    public OssClient ossClient(S3Client s3Client, S3Presigner s3Presigner, S3Properties s3Properties) {
        return new AwsS3Client(s3Client, s3Presigner, s3Properties.getBucketName(), s3Properties.isPrivateBucket());
    }
    
    @Bean
    @ConditionalOnMissingBean(S3Client.class)
    public S3Client s3Client(S3Properties s3Properties) {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getAccessSecret())))
                .endpointOverride(URI.create(s3Properties.getEndpoint())).serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(s3Properties.isPathStyleAccess())
                                .chunkedEncodingEnabled(false)
                                .build())
                .region(Region.of(s3Properties.getRegion()))
                .build();
    }
    
    @Bean
    @ConditionalOnMissingBean(S3Presigner.class)
    public S3Presigner s3Presigner(S3Properties s3Properties) {
        return S3Presigner.builder()
                .region(Region.of(s3Properties.getRegion()))
                .endpointOverride(URI.create(s3Properties.getEndpoint())).serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(s3Properties.isPathStyleAccess())
                                .chunkedEncodingEnabled(false)
                                .build())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getAccessSecret())))
                .build();
    }
    
}
