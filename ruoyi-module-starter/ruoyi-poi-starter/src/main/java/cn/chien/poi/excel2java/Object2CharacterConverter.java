package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2CharacterConverter implements ExcelType2JavaConverter<Character> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return Character.TYPE.isAssignableFrom(targetClazz) || Character.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public Character convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toChar(source);
    }
}
