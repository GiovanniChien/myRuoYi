package cn.chien.oss.client;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author qiandq3
 * @date 2022/11/17
 */
public interface OssClient {
    
    /**
     * 创建bucket
     *
     * @param bucketName
     */
    void createBucket(String bucketName);
    
    /**
     * 获取url
     *
     * @param objectName
     * @return
     */
    String getObjectUrl(String objectName);
    
    /**
     * 获取url
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    String getObjectUrl(String bucketName, String objectName);
    
    /**
     *
     * @param bucketName
     * @param objectName
     * @param isPrivate 获取私有bucket的文件时需要拼接认证信息
     * @return
     */
    String getObjectUrl(String bucketName, String objectName, boolean isPrivate);
    
    /**
     * 获取存储对象信息
     *
     * @param objectName
     * @return
     */
    ResponseInputStream<GetObjectResponse> getObjectInfo(String objectName);
    
    /**
     * 获取存储对象信息
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    ResponseInputStream<GetObjectResponse> getObjectInfo(String bucketName, String objectName);
    
    /**
     * 上传文件
     *
     * @param objectName
     * @param stream
     * @param size
     * @return
     * @throws IOException
     */
    PutObjectResponse putObject(String objectName, InputStream stream, long size)
            throws IOException;
    
    /**
     * 上传文件
     *
     * @param bucketName
     * @param objectName
     * @param stream
     * @param size
     * @return
     * @throws IOException
     */
    PutObjectResponse putObject(String bucketName, String objectName, InputStream stream, long size)
            throws IOException;
    
    
    default PutObjectResponse putObject(String bucketName, String objectName, InputStream stream) throws IOException {
        return putObject(bucketName, objectName, stream, stream.available());
    }
    
    default PutObjectResponse putObject(String objectName, InputStream stream) throws IOException {
        return putObject(objectName, stream, stream.available());
    }
    
}
