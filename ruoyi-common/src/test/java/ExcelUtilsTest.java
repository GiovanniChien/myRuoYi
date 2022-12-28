import cn.chien.poi.annotation.Excel;
import cn.chien.domain.entity.SysDept;
import cn.chien.domain.entity.SysRole;
import cn.chien.domain.entity.SysUser;
import cn.chien.utils.ReflectionUtils;
import cn.chien.poi.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qiandq3
 * @date 2022/12/6
 */
@Slf4j
public class ExcelUtilsTest {
    
    @Test
    public void excel_field_init_test() {
        ReflectionUtils.doWithFields(SysUser.class, field -> {
            System.out.println(field.getName());
        });
        ExcelUtil<SysUser> excelUtil = new ExcelUtil<>(SysUser.class);
        excelUtil.init(Excel.Type.IMPORT);
    }
    
    @Test
    public void import_template_generate_test()
            throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ExcelUtil<SysUser> excelUtil = new ExcelUtil<>(SysUser.class);
        String filename = "user.xlsx";
        excelUtil.export("用户数据", Files.newOutputStream(Paths.get("D:\\ruoyi\\" + filename)), new ArrayList<>());
    }
    
    @Test
    public void import_data_test() throws IOException {
        InputStream inputStream = Files.newInputStream(
                Paths.get("D:\\ruoyi\\user.xlsx"));
        ExcelUtil<SysUser> excelUtil = new ExcelUtil<>(SysUser.class);
        List<SysUser> sysUsers = excelUtil.importExcel(inputStream);
        log.info("{}", sysUsers);
    }
    
    @Test
    public void export_list_test()
            throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        SysUser mockUser = new SysUser();
        mockUser.setUserId(1L);
        mockUser.setDeptId(1L);
        mockUser.setParentId(null);
        mockUser.setRoleId(1L);
        mockUser.setLoginName("admin");
        mockUser.setUserName("admin");
        mockUser.setUserType("1");
        mockUser.setEmail("admin@qq.com");
        mockUser.setPhonenumber("1374215351");
        mockUser.setSex("0");
        mockUser.setStatus("0");
        ArrayList<SysRole> roles = new ArrayList<>();
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(1L);
        sysRole.setRoleName("admin");
        roles.add(sysRole);
        SysRole sysRole2 = new SysRole();
        sysRole2.setRoleId(2L);
        sysRole2.setRoleName("test");
        roles.add(sysRole2);
        mockUser.setRoles(roles);
        mockUser.setRoleIds(new Long[] {1L, 2L});
        mockUser.setPostIds(new Long[] {1L, 2L});
        mockUser.setRemark("1232");
        SysDept mockDept = new SysDept();
        mockDept.setDeptName("OSS");
        mockDept.setLeader("Chien");
        mockUser.setDept(mockDept);
        mockUser.setLoginDate(new Date());
        
        ArrayList<SysUser> mockList = new ArrayList<>();
        mockList.add(mockUser);
        ExcelUtil<SysUser> excelUtils = new ExcelUtil<>(SysUser.class);
        excelUtils.export("用户数据", Files.newOutputStream(Paths.get("/Users/giovannichien/Documents/learning/upload_dir/user.xlsx")), mockList);
    }
    
}
