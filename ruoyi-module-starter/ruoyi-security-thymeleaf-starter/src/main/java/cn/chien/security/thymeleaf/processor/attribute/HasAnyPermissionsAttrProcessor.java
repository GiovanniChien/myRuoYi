package cn.chien.security.thymeleaf.processor.attribute;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author qiandq3
 * @date 2022/11/21
 */
public class HasAnyPermissionsAttrProcessor extends AbstractAttributeTagProcessor {
    
    private static final String ATTRIBUTE_NAME = "hasAllPermissions";
    
    private static final int PRECEDENCE = 300;
    
    public HasAnyPermissionsAttrProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }
    
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
            String attributeValue, IElementTagStructureHandler structureHandler) {
        
    }
}
