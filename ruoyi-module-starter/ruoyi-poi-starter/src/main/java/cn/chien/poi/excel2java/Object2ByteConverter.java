package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2ByteConverter implements ExcelType2JavaConverter<Byte> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return Byte.TYPE.isAssignableFrom(targetClazz) || Byte.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public Byte convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toByte(source);
    }
}
