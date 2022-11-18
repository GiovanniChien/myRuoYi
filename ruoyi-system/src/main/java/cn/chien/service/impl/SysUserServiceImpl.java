package cn.chien.service.impl;

import cn.chien.constant.UserConstants;
import cn.chien.domain.SysPost;
import cn.chien.domain.entity.SysRole;
import cn.chien.domain.entity.SysUser;
import cn.chien.mapper.SysPostMapper;
import cn.chien.mapper.SysRoleMapper;
import cn.chien.mapper.SysUserMapper;
import cn.chien.service.ISysUserService;
import cn.chien.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qian.diqi
 * @date 2022/7/3
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
    
    @Resource
    private SysUserMapper sysUserMapper;
    
    @Resource
    private SysRoleMapper sysRoleMapper;
    
    @Resource
    private SysPostMapper postMapper;
    
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return null;
    }
    
    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return null;
    }
    
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return null;
    }
    
    @Override
    public SysUser selectUserByLoginName(String userName) {
        return sysUserMapper.selectUserByLoginName(userName);
    }
    
    @Override
    public SysUser selectUserByPhoneNumber(String phoneNumber) {
        return null;
    }
    
    @Override
    public SysUser selectUserByEmail(String email) {
        return null;
    }
    
    @Override
    public SysUser selectUserById(Long userId) {
        return sysUserMapper.selectUserById(userId);
    }
    
    @Override
    public int deleteUserById(Long userId) {
        return 0;
    }
    
    @Override
    public int deleteUserByIds(String ids) {
        return 0;
    }
    
    @Override
    public int insertUser(SysUser user) {
        return 0;
    }
    
    @Override
    public boolean registerUser(SysUser user) {
        return false;
    }
    
    @Override
    public int updateUser(SysUser user) {
        return 0;
    }
    
    @Override
    public int updateUserInfo(SysUser user) {
        return sysUserMapper.updateUser(user);
    }
    
    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {
    
    }
    
    @Override
    public int resetUserPwd(SysUser user) {
        return updateUserInfo(user);
    }
    
    @Override
    public String checkLoginNameUnique(String loginName) {
        return null;
    }
    
    @Override
    public String checkPhoneUnique(SysUser user) {
        SysUser tmpUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().lambda().eq(SysUser::getPhonenumber, user.getPhonenumber()));
        return tmpUser == null || !tmpUser.getUserId().equals(user.getUserId()) ? UserConstants.USER_PHONE_NOT_UNIQUE : UserConstants.USER_PHONE_UNIQUE;
    }
    
    @Override
    public String checkEmailUnique(SysUser user) {
        SysUser tmpUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().lambda().eq(SysUser::getEmail, user.getEmail()));
        return tmpUser == null || !tmpUser.getUserId().equals(user.getUserId()) ? UserConstants.USER_EMAIL_NOT_UNIQUE : UserConstants.USER_EMAIL_UNIQUE;
    }
    
    @Override
    public void checkUserAllowed(SysUser user) {
    
    }
    
    @Override
    public void checkUserDataScope(Long userId) {
    
    }
    
    @Override
    public String selectUserRoleGroup(Long userId) {
        List<SysRole> list = sysRoleMapper.selectRolesByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }
    
    @Override
    public String selectUserPostGroup(Long userId) {
        List<SysPost> list = postMapper.selectPostsByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }
    
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        return null;
    }
    
    @Override
    public int changeStatus(SysUser user) {
        return 0;
    }
}
