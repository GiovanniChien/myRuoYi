package cn.chien.util;

import cn.chien.core.page.TableDataInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
public final class PageUtil {
    
    private PageUtil() { }
    
    public static <T> TableDataInfo getTableDataInfo(IPage<T> page) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }
    
}
