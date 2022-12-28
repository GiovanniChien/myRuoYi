package cn.chien.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiandq3
 * @date 2022/11/15
 */
@Slf4j
public final class JsonUtils {
    
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
    
    public static <T> T readValue(String jsonStr, Class<T> clazz) {
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            log.error("deserialize error.", e);
            return null;
        }
    }
    
    public static <T> String writeValueAsString(T obj) {
        return writeValueAsString(obj, DEFAULT_OBJECT_MAPPER.writer());
    }
    
    public static <T> String writeValueAsStringMasking(T obj) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept("password", "oldPassword",
                "newPassword", "confirmPassword");
        FilterProvider filters = new SimpleFilterProvider().addFilter("excludePropertyFilter", theFilter);
        mapper.addMixIn(obj.getClass(), ExcludePropertyFilterMix.class);
        return writeValueAsString(obj, mapper.writer(filters));
    }
    
    private static <T> String writeValueAsString(T obj, ObjectWriter mapper) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("serialize error. ", e);
            return null;
        }
    }
    
    public static Map<String, Object> readValueAsMap(String jsonStr) {
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(jsonStr, new MapTypeReference());
        }
        catch (JsonProcessingException e) {
            log.error("deserialize error.", e);
            return new HashMap<>();
        }
    }
    
    private static class MapTypeReference extends TypeReference<Map<String, Object>> { }
}
