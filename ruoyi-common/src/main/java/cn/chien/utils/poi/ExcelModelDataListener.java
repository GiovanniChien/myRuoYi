package cn.chien.utils.poi;

import cn.chien.annotation.ExcelClass;
import cn.chien.utils.ReflectionUtils;
import cn.chien.utils.StringUtils;
import cn.chien.utils.json.JsonUtils;
import cn.chien.utils.spring.SpringUtils;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
@Slf4j
public class ExcelModelDataListener<T>  extends AnalysisEventListener<Map<Integer, Object>> {
    
    private final List<ExcelField> excelFieldList;
    
    private final Map<Object, T> mergeMap;
    
    private List<T> datas;
    
    private final Class<T> clazz;
    
    private int curIdx;
    
    private final ServiceLoader<ExcelType2JavaConverter> converters;
    
    private final String excelClassKey;
    
    private final boolean requireMerge;
    
    public ExcelModelDataListener(List<ExcelField> excelFieldList, Class<T> clazz, List<T> datas) {
        this.excelFieldList = excelFieldList;
        this.clazz = clazz;
        ExcelClass excelClass = clazz.getDeclaredAnnotation(ExcelClass.class);
        if (excelClass == null || StringUtils.isEmpty(excelClass.key())) {
            requireMerge = false;
            excelClassKey = null;
        } else {
            requireMerge = true;
            excelClassKey = excelClass.key();
        }
        mergeMap = new HashMap<>();
        this.datas = datas;
        this.converters = ServiceLoader.load(ExcelType2JavaConverter.class);
    }
    
    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        log.info("解析到一条数据: {}", JsonUtils.writeValueAsString(data));
        curIdx = 0;
        try {
            T t = createObject(data, clazz, this.excelFieldList);
            mergeIfNecessary(t);
            log.info("mergeMap: {}", mergeMap);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchFieldException e) {
            log.error("Read data error", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 有些字段为list，在excel表格中以多行形式存在，这里根据注解配置读取之后进行合并
     */
    private void mergeIfNecessary(T t) throws NoSuchFieldException, IllegalAccessException {
        if (!requireMerge) {
            datas.add(t);
            return;
        }
        Field keyField = ReflectionUtils.findField(t.getClass(), excelClassKey);
        keyField.setAccessible(true);
        Object key = keyField.get(t);
        if (mergeMap.containsKey(key)) {
            T ori = mergeMap.get(key);
            for (ExcelField excelField : excelFieldList) {
                if (excelField.getExcelAnnotation().requireMerge()) {
                    Field field = excelField.getField();
                    if (field.getType().isAssignableFrom(List.class)) {
                        List n = (List) field.get(t);
                        List o = (List) field.get(ori);
                        o.addAll(n);
                    }
                }
            }
        } else {
            mergeMap.put(key, t);
        }
    }
    
    private <R> R createObject(Map<Integer, Object> data, Class<R> oClazz, List<ExcelField> excelFields)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        R instance = oClazz.getDeclaredConstructor().newInstance();
        int i = 0;
        while (i < excelFields.size()) {
            ExcelField excelField = excelFields.get(i);
            if (CollectionUtils.isEmpty(excelField.getSubField())) {
                Object obj = data.get(curIdx);
                Class<?> handler = excelField.getExcelAnnotation().handler();
                ExcelHandlerAdapter adapter = (ExcelHandlerAdapter) handler.getDeclaredConstructor().newInstance();
                if (adapter instanceof DefaultExcelFieldHandler defaultAdapter) {
                    defaultAdapter.setConverters(this.converters);
                }
                
                // 枚举关系存在，转换为实际枚举值，如0=男,1=女,2=未知，需要将男/女转换为0/1
                String readConverterExp = excelField.getExcelAnnotation().readConverterExp();
                if (StringUtils.isNotEmpty(readConverterExp)) {
                    obj = readConvert(obj, readConverterExp);
                }
                
                Object fieldVal = adapter.format(obj, excelField.getField().getType(), excelField.getExcelAnnotation());
                excelField.getField().set(instance, fieldVal);
                curIdx++;
            } else {
                // 嵌套对象如果是数组的话
                Class<?> type = excelField.getField().getType();
                if (List.class.isAssignableFrom(type)) {
                    ParameterizedType pt = (ParameterizedType) excelField.getField().getGenericType();
                    Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
                    Object subObject = createObject(data, subClass, excelField.getSubField());
                    List subList = new ArrayList();
                    subList.add(subObject);
                    excelField.getField().set(instance, subList);
                } else {
                    Object subObj = createObject(data, excelField.getField().getType(), excelField.getSubField());
                    excelField.getField().set(instance, subObj);
                }
            }
            i++;
        }
        return instance;
    }
    
    /**
     * 枚举关系存在，转换为实际枚举值，如0=男,1=女,2=未知，需要将男/女转换为0/1
     */
    private Object readConvert(Object source, String readConverterExp) {
        if (source == null) {
            return null;
        }
        String[] split = readConverterExp.split(",");
        for (String s : split) {
            String[] relas = s.split("=");
            if (relas[1].equals(source.toString())) {
                return relas[0];
            }
        }
        return source;
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!requireMerge) {
            return;
        }
        datas.addAll(mergeMap.values());
    }
}
