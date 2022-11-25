package cn.chien.security.thymeleaf.dialect;

import cn.chien.security.thymeleaf.processor.attribute.HasAnyPermissionsAttrProcessor;
import cn.chien.security.thymeleaf.processor.attribute.HasPermissionAttrProcessor;
import cn.chien.security.thymeleaf.processor.element.HasAnyPermissionsElementProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author qiandq3
 * @date 2022/11/21
 */
public class RuoyiDialect extends AbstractProcessorDialect {
    
    private static final String NAME = "Ruoyi";
    
    private static final String PREFIX = "ruoyi";
    
    public RuoyiDialect() {
        super(NAME, PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }
    
    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> allTagProcessors = new HashSet<>();
        
        allTagProcessors.add(new HasAnyPermissionsAttrProcessor(dialectPrefix));
        allTagProcessors.add(new HasAnyPermissionsElementProcessor(dialectPrefix));
        
        allTagProcessors.add(new HasPermissionAttrProcessor(dialectPrefix));
        
        return allTagProcessors;
    }
    
}
