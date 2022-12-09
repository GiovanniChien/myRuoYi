package cn.chien.utils.poi;

import cn.chien.annotation.Excel;

/**
 * Excel数据格式处理适配器
 *
 * @author ruoyi
 */
public interface ExcelHandlerAdapter {
    
    /**
     * 格式化
     *
     * @param value 单元格数据值
     * @return 处理后的值
     */
    Object format(Object value, Class<?> type, Excel excel);
}
