package cn.chien.poi;

import cn.chien.poi.annotation.Excel;

import java.lang.reflect.Field;

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
    Object excel2JavaFormat(Object value, Class<?> type, Excel excel);
    
    /**
     * 导出格式化
     */
    Object java2ExcelFormat(Object value, Class<?> sourceType, Class<?> targetType, Excel excel, Field field);
}
