import cn.chien.annotation.Excel;
import cn.chien.domain.entity.SysUser;
import cn.chien.utils.ReflectionUtils;
import cn.chien.utils.poi.ExcelUtil;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author qiandq3
 * @date 2022/12/6
 */
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
        String filename = UUID.randomUUID() + "_" + "user" + ".xlsx";
        excelUtil.importTemplateExcel("用户数据", Files.newOutputStream(Paths.get("D:\\ruoyi\\" + filename)));
    }
    
}
