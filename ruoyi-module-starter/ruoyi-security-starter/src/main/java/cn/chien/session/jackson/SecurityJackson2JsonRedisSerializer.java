package cn.chien.session.jackson;

import cn.chien.security.access.SecurityUser;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author qiandq3
 * @date 2022/11/2
 */
public class SecurityJackson2JsonRedisSerializer implements RedisSerializer<Object> {
    
    private final ObjectMapper mapper;
    
    /**
     * Creates {@link GenericJackson2JsonRedisSerializer} and configures {@link ObjectMapper} for default typing.
     */
    public SecurityJackson2JsonRedisSerializer() {
        this((String) null);
    }
    
    /**
     * Creates {@link GenericJackson2JsonRedisSerializer} and configures {@link ObjectMapper} for default typing using the
     * given {@literal name}. In case of an {@literal empty} or {@literal null} String the default
     * {@link JsonTypeInfo.Id#CLASS} will be used.
     *
     * @param classPropertyTypeName Name of the JSON property holding type information. Can be {@literal null}.
     * @see ObjectMapper#activateDefaultTypingAsProperty(PolymorphicTypeValidator, ObjectMapper.DefaultTyping, String)
     * @see ObjectMapper#activateDefaultTyping(PolymorphicTypeValidator, ObjectMapper.DefaultTyping, JsonTypeInfo.As)
     */
    public SecurityJackson2JsonRedisSerializer(@Nullable String classPropertyTypeName) {
        
        this(new ObjectMapper());
    
        // 手动注册security相关类的序列化方法
        List<Module> modules = SecurityJackson2Modules.getModules(getClass().getClassLoader());
        for (Module module : modules) {
            mapper.registerModule(module);
        }
        // simply setting {@code mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)} does not help here since we need
        // the type hint embedded for deserialization using the default typing feature.
        registerNullValueSerializer(mapper, classPropertyTypeName);
        
        mapper.addMixIn(SecurityUser.class, SecurityUserMixIn.class);
        
        if (StringUtils.hasText(classPropertyTypeName)) {
            mapper.activateDefaultTypingAsProperty(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL,
                    classPropertyTypeName);
        } else {
            mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }
    }
    
    /**
     * Setting a custom-configured {@link ObjectMapper} is one way to take further control of the JSON serialization
     * process. For example, an extended {@link SerializerFactory} can be configured that provides custom serializers for
     * specific types.
     *
     * @param mapper must not be {@literal null}.
     */
    public SecurityJackson2JsonRedisSerializer(ObjectMapper mapper) {
        
        Assert.notNull(mapper, "ObjectMapper must not be null!");
        this.mapper = mapper;
    }
    
    /**
     * Register {@link GenericJackson2JsonRedisSerializer.NullValueSerializer} in the given {@link ObjectMapper} with an optional
     * {@code classPropertyTypeName}. This method should be called by code that customizes
     * {@link GenericJackson2JsonRedisSerializer} by providing an external {@link ObjectMapper}.
     *
     * @param objectMapper the object mapper to customize.
     * @param classPropertyTypeName name of the type property. Defaults to {@code @class} if {@literal null}/empty.
     * @since 2.2
     */
    public static void registerNullValueSerializer(ObjectMapper objectMapper, @Nullable String classPropertyTypeName) {
        
        // simply setting {@code mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)} does not help here since we need
        // the type hint embedded for deserialization using the default typing feature.
        objectMapper.registerModule(new SimpleModule().addSerializer(new SecurityJackson2JsonRedisSerializer.NullValueSerializer(classPropertyTypeName)));
    }
    
    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.Object)
     */
    @Override
    public byte[] serialize(@Nullable Object source) throws SerializationException {
        
        if (source == null) {
            return new byte[0];
        }
        
        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
     */
    @Override
    public Object deserialize(@Nullable byte[] source) throws SerializationException {
        return deserialize(source, Object.class);
    }
    
    /**
     * @param source can be {@literal null}.
     * @param type must not be {@literal null}.
     * @return {@literal null} for empty source.
     * @throws SerializationException
     */
    @Nullable
    public <T> T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {
        
        Assert.notNull(type,
                "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
        
        if ((source == null || source.length == 0)) {
            return null;
        }
        
        try {
            return mapper.readValue(source, type);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * {@link StdSerializer} adding class information required by default typing. This allows de-/serialization of
     * {@link NullValue}.
     *
     * @author Christoph Strobl
     * @since 1.8
     */
    private static class NullValueSerializer extends StdSerializer<NullValue> {
        
        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;
        
        /**
         * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
         */
        NullValueSerializer(@Nullable String classIdentifier) {
            
            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }
        
        /*
         * (non-Javadoc)
         * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
         */
        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            
            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
    
    
}
