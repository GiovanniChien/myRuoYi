package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

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
