package cn.chien.service.impl;

import cn.chien.constant.Constants;
import cn.chien.domain.SysLoginInfo;
import cn.chien.mapper.SysLoginInfoMapper;
import cn.chien.service.ISysLoginInfoService;
import cn.chien.utils.AddressUtils;
import cn.chien.utils.IpUtils;
import cn.chien.utils.LogUtils;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysLoginInfoServiceImpl implements ISysLoginInfoService {
    
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");
    
    @Autowired
    private SysLoginInfoMapper loginInfoMapper;
    
    /**
     * 新增系统登录日志
     *
     * @param loginInfo 访问日志对象
     */
    @Override
    @Async
    public void insertLoginInfo(SysLoginInfo loginInfo) {
        loginInfo.setLoginTime(new Date());
        loginInfoMapper.insert(loginInfo);
    }
    
    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLoginInfo> selectLoginInfoList(SysLoginInfo loginInfo) {
        LambdaQueryWrapper<SysLoginInfo> wrapper = new LambdaQueryWrapper<>(loginInfo);
        return loginInfoMapper.selectList(wrapper);
    }
    
    /**
     * 批量删除系统登录日志
     *
     * @param ids 需要删除的数据
     * @return 结果
     */
    @Override
    public int deleteLoginInfoByIds(String ids) {
        return loginInfoMapper.deleteBatchIds(Arrays.asList(Convert.toStrArray(ids)));
    }
    
    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLoginInfo() {
        loginInfoMapper.cleanLoginInfo();
    }
    
    @Override
    @Async
    public void recordLoginInfo(String userName, String loginResult, String msg, HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        String address = AddressUtils.getRealAddressByIP(ip);
        String s = LogUtils.getBlock(ip) + address + LogUtils.getBlock(userName) + LogUtils.getBlock(loginResult)
                + LogUtils.getBlock(msg);
        // 打印信息到日志
        sys_user_logger.info(s);
        // 获取客户端操作系统
        String os = userAgent.getOs().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        SysLoginInfo loginInfo = new SysLoginInfo();
        loginInfo.setLoginName(userName);
        loginInfo.setIpaddr(ip);
        loginInfo.setLoginLocation(address);
        loginInfo.setBrowser(browser);
        loginInfo.setOs(os);
        loginInfo.setMsg(msg);
        // 日志状态
        if (StringUtils.equalsAny(loginResult, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            loginInfo.setStatus(Constants.SUCCESS);
        } else if (Constants.LOGIN_FAIL.equals(loginResult)) {
            loginInfo.setStatus(Constants.FAIL);
        }
        insertLoginInfo(loginInfo);
    }
}
