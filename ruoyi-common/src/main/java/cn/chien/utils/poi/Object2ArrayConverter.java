package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

import java.lang.reflect.Array;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2ArrayConverter implements ExcelType2JavaConverter<Object> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return targetClazz.isArray();
    }
    
    @Override
    public Object convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        Class<?> targetComponentType = targetType.getComponentType();
        String[] fields = Convert.toStrArray(Convert.toStr(source));
        Object target = Array.newInstance(targetComponentType, fields.length);
        for (int i = 0; i < fields.length; i++) {
            String sourceElement = fields[i];
            Object targetElement = defaultExcelFieldHandler.format(sourceElement.trim(), targetComponentType, excel);
            Array.set(target, i, targetElement);
        }
        return target;
    }
}
