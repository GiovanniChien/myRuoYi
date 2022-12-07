package cn.chien.service.impl;

import cn.chien.annotation.DataScope;
import cn.chien.constant.UserConstants;
import cn.chien.core.page.TableDataInfo;
import cn.chien.domain.SysPost;
import cn.chien.domain.SysUserPost;
import cn.chien.domain.SysUserRole;
import cn.chien.domain.entity.SysRole;
import cn.chien.domain.entity.SysUser;
import cn.chien.exception.ServiceException;
import cn.chien.mapper.SysPostMapper;
import cn.chien.mapper.SysRoleMapper;
import cn.chien.mapper.SysUserMapper;
import cn.chien.mapper.SysUserPostMapper;
import cn.chien.mapper.SysUserRoleMapper;
import cn.chien.request.UserListPageQueryRequest;
import cn.chien.security.util.PrincipalUtil;
import cn.chien.service.ISysUserService;
import cn.chien.util.PageUtil;
import cn.chien.utils.StringUtils;
import cn.chien.utils.spring.SpringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    @Resource
    private SysUserPostMapper sysUserPostMapper;
    
    @Resource
    private SysUserRoleMapper userRoleMapper;
    
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public TableDataInfo selectUserList(UserListPageQueryRequest user) {
        IPage<SysUser> page = new Page<>(user.getPageNum(), user.getPageSize());
        IPage<SysUser> pageRes = sysUserMapper.selectUserList(page, user);
        return PageUtil.getTableDataInfo(pageRes);
    }
    
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user) {
        return null;
    }
    
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
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
    public int deleteUserByIds(Long[] ids) {
        for (Long userId : ids) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(ids);
        // 删除用户与岗位关联
        sysUserPostMapper.deleteUserPost(ids);
        return sysUserMapper.deleteUserByIds(ids);
    }
    
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = sysUserMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user.getUserId(), user.getRoleIds());
        return rows;
    }
    
    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }
    
    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
                sysUserPostMapper.batchUserPost(list);
            }
        }
    }
    
    @Override
    public boolean registerUser(SysUser user) {
        return false;
    }
    
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user.getUserId(), user.getRoleIds());
        // 删除用户与岗位关联
        sysUserPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return sysUserMapper.updateUser(user);
    }
    
    @Override
    public int updateUserInfo(SysUser user) {
        return sysUserMapper.updateUser(user);
    }
    
    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }
    
    @Override
    public int resetUserPwd(SysUser user) {
        return updateUserInfo(user);
    }
    
    @Override
    public String checkLoginNameUnique(String loginName) {
        SysUser tmpUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().lambda().eq(SysUser::getLoginName, loginName));
        return tmpUser == null ? UserConstants.USER_NAME_UNIQUE : UserConstants.USER_NAME_NOT_UNIQUE;
    }
    
    @Override
    public String checkPhoneUnique(SysUser user) {
        SysUser tmpUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().lambda().eq(SysUser::getPhonenumber, user.getPhonenumber()));
        return tmpUser == null || tmpUser.getUserId().equals(user.getUserId()) ? UserConstants.USER_PHONE_UNIQUE
                : UserConstants.USER_PHONE_NOT_UNIQUE;
    }
    
    @Override
    public String checkEmailUnique(SysUser user) {
        SysUser tmpUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().lambda().eq(SysUser::getEmail, user.getEmail()));
        return tmpUser == null || tmpUser.getUserId().equals(user.getUserId()) ? UserConstants.USER_EMAIL_UNIQUE
                : UserConstants.USER_EMAIL_NOT_UNIQUE;
    }
    
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }
    
    @Override
    public void checkUserDataScope(Long userId) {
        if (!PrincipalUtil.isAdmin()) {
            UserListPageQueryRequest user = new UserListPageQueryRequest();
            user.setUserId(userId);
            TableDataInfo tableDataInfo = SpringUtils.getBean(this.getClass()).selectUserList(user);
            List<SysUser> users = (List<SysUser>) tableDataInfo.getRows();
            if (StringUtils.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
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
