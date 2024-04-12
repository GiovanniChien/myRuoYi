package cn.chien.service;

import cn.chien.domain.SysLoginInfo;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 日志记录功能
 *
 * @author qian.diqi
 * @date 2022/8/18
 */
public interface ISysLoginInfoService {
    
    /**
     * 新增系统登录日志
     *
     * @param loginInfo 访问日志对象
     */
    void insertLoginInfo(SysLoginInfo loginInfo);
    
    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    List<SysLoginInfo> selectLoginInfoList(SysLoginInfo loginInfo);
    
    /**
     * 批量删除系统登录日志
     *
     * @param ids 需要删除的数据
     * @return 结果
     */
    int deleteLoginInfoByIds(String ids);
    
    /**
     * 清空系统登录日志
     */
    void cleanLoginInfo();
    
    void recordLoginInfo(String userName, String loginResult, String msg, HttpServletRequest request);
}
