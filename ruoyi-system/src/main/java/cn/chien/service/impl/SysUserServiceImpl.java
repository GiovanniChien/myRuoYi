package cn.chien.service.impl;

import cn.chien.domain.entity.SysUser;
import cn.chien.mapper.SysUserMapper;
import cn.chien.service.ISysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author qian.diqi
 * @date 2022/7/3
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

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
        return null;
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
        return 0;
    }

    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {

    }

    @Override
    public int resetUserPwd(SysUser user) {
        return 0;
    }

    @Override
    public String checkLoginNameUnique(String loginName) {
        return null;
    }

    @Override
    public String checkPhoneUnique(SysUser user) {
        return null;
    }

    @Override
    public String checkEmailUnique(SysUser user) {
        return null;
    }

    @Override
    public void checkUserAllowed(SysUser user) {

    }

    @Override
    public void checkUserDataScope(Long userId) {

    }

    @Override
    public String selectUserRoleGroup(Long userId) {
        return null;
    }

    @Override
    public String selectUserPostGroup(Long userId) {
        return null;
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
