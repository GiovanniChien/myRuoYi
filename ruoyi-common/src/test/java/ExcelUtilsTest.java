import cn.chien.annotation.Excel;
import cn.chien.domain.entity.SysUser;
import cn.chien.utils.ReflectionUtils;
import cn.chien.utils.poi.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void import_template_generate_test() throws IOException {
        ExcelUtil<SysUser> excelUtil = new ExcelUtil<>(SysUser.class);
        String filename = "user.xlsx";
        excelUtil.importTemplateExcel("用户数据", Files.newOutputStream(Paths.get("D:\\ruoyi\\" + filename)));
    }
    
    @Test
    public void import_data_test() throws IOException {
        InputStream inputStream = Files.newInputStream(
                Paths.get("D:\\ruoyi\\user.xlsx"));
        ExcelUtil<SysUser> excelUtil = new ExcelUtil<>(SysUser.class);
        List<SysUser> sysUsers = excelUtil.importExcel(inputStream);
        log.info("{}", sysUsers);
    }
    
}
