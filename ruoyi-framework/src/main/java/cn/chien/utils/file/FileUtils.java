package cn.chien.utils.file;

import cn.chien.exception.file.FileNameLengthLimitExceededException;
import cn.chien.exception.file.FileSizeLimitExceededException;
import cn.chien.exception.file.InvalidExtensionException;
import cn.chien.oss.client.OssClient;
import cn.chien.utils.StringUtils;
import cn.chien.utils.spring.SpringUtils;
import cn.chien.utils.uuid.Seq;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @author qiandq3
 * @date 2022/11/11
 */
public final class FileUtils {
    
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;
    
    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;
    
    private static final OssClient ossClient = SpringUtils.getBean(OssClient.class);
    
    public static String upload(MultipartFile file, String[] allowedExtension)
            throws IOException, InvalidExtensionException {
        return upload(null, file, allowedExtension);
    }
    
    public static String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws IOException, InvalidExtensionException {
        int fileNameLength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNameLength > DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(DEFAULT_FILE_NAME_LENGTH);
        }
        
        assertAllowed(file, allowedExtension);
        String fileName = extractFilename(file);
        if (StringUtils.isNotEmpty(baseDir)) {
            fileName = baseDir + "/" + fileName;
        }
        ossClient.putObject(fileName, file.getInputStream());
        return ossClient.getObjectUrl(fileName);
    }
    
    private static String extractFilename(MultipartFile file) {
        return StringUtils.format("{}_{}.{}",
                FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId(Seq.uploadSeqType),
                getExtension(file));
    }
    
    public static void assertAllowed(MultipartFile file, String[] allowedExtension)
            throws FileSizeLimitExceededException, InvalidExtensionException {
        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }
        
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension,
                        fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }
    
    private static boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    private static String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType()));
        }
        return extension;
    }
    
}
