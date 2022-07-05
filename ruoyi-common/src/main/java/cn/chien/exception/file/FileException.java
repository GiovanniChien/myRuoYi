package cn.chien.exception.file;

import cn.chien.exception.base.BaseException;

/**
 * 文件信息异常类
 * 
 * @author ruoyi
 */
public class FileException extends BaseException
{
    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
