/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chien.session.config;

import cn.chien.security.SecurityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.FlushMode;
import org.springframework.session.MapSession;
import org.springframework.session.SaveMode;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.UuidSessionIdGenerator;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.AbstractRedisHttpSessionConfiguration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.time.Duration;
import java.util.Map;

/**
 * Exposes the {@link SessionRepositoryFilter} as a bean named {@code springSessionRepositoryFilter}. In order to use
 * this a single {@link RedisConnectionFactory} must be exposed as a Bean.
 *
 * @author Rob Winch
 * @author Eddú Meléndez
 * @author Vedran Pavic
 * @see EnableRedisHttpSession
 * @since 1.0
 */
@EnableConfigurationProperties(RedisSessionProperties.class)
public class RedisHttpSessionConfigurationWithoutListener extends AbstractRedisHttpSessionConfiguration<RedisSessionRepository>
        implements BeanClassLoaderAware, EmbeddedValueResolverAware, ImportAware {
    
    public static final String DEFAULT_CLEANUP_CRON = "0 * * * * *";
    
    private final Duration maxInactiveInterval = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL;
    
    private final FlushMode flushMode = FlushMode.ON_SAVE;
    
    private final SaveMode saveMode = SaveMode.ON_SET_ATTRIBUTE;
    
    private ClassLoader classLoader;
    
    private StringValueResolver embeddedValueResolver;
    
    private SecurityProperties securityProperties;
    
    private RedisSessionProperties redisSessionProperties;
    
    private SessionIdGenerator sessionIdGenerator = UuidSessionIdGenerator.getInstance();
    
    
    @Bean
    public RedisSessionRepository sessionRepository() {
        RedisTemplate<String, Object> redisTemplate = createRedisTemplate();
        RedisSessionRepository sessionRepository = new RedisSessionRepository(redisTemplate);
        sessionRepository.setDefaultMaxInactiveInterval(
                securityProperties.getSession().getExpireTime() == null ? this.maxInactiveInterval
                        : Duration.ofMinutes(securityProperties.getSession().getExpireTime()));
        String redisNamespace = RedisIndexedSessionRepository.DEFAULT_NAMESPACE;
        sessionRepository.setRedisKeyNamespace(redisSessionProperties.getNamespace() == null ? redisNamespace
                : redisSessionProperties.getNamespace());
        sessionRepository.setFlushMode(this.flushMode);
        sessionRepository.setSaveMode(this.saveMode);
        sessionRepository.setSessionIdGenerator(sessionIdGenerator);
        getSessionRepositoryCustomizers().forEach(item -> item.customize(sessionRepository));
        return sessionRepository;
    }
    
    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
    
    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> attributeMap = importMetadata.getAnnotationAttributes(
                EnableRedisHttpSession.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);
        if (attributes != null) {
            this.setMaxInactiveInterval(
                    Duration.ofSeconds((long) (Integer) attributes.getNumber("maxInactiveIntervalInSeconds")));
            String redisNamespaceValue = attributes.getString("redisNamespace");
            if (StringUtils.hasText(redisNamespaceValue)) {
                this.setRedisNamespace(this.embeddedValueResolver.resolveStringValue(redisNamespaceValue));
            }
            
            this.setFlushMode(attributes.getEnum("flushMode"));
            this.setSaveMode(attributes.getEnum("saveMode"));
        }
    }
    
    protected RedisTemplate<String, Object> createRedisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setHashKeySerializer(RedisSerializer.string());
		if (getDefaultRedisSerializer() != null) {
			redisTemplate.setDefaultSerializer(getDefaultRedisSerializer());
		}
		redisTemplate.setConnectionFactory(getRedisConnectionFactory());
		redisTemplate.setBeanClassLoader(this.classLoader);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
    
    @Autowired(required = false)
	public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
		this.sessionIdGenerator = sessionIdGenerator;
	}
    
    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
    
    @Autowired
    public void setRedisSessionProperties(RedisSessionProperties redisSessionProperties) {
        this.redisSessionProperties = redisSessionProperties;
    }
    
    @Override
    public void setBeanClassLoader(@NotNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
}
