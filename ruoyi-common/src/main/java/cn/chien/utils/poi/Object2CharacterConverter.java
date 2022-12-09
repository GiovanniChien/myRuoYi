package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

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
