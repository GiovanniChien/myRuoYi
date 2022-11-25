package cn.chien.security.thymeleaf.processor.attribute;

import cn.chien.security.thymeleaf.SecurityFacade;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;

import static cn.chien.security.thymeleaf.ThymeleafFacade.evaluateAsStringsWithDelimiter;
import static cn.chien.security.thymeleaf.ThymeleafFacade.getRawValue;

/**
 * @author qiandq3
 * @date 2022/11/21
 */
public class HasPermissionAttrProcessor extends AbstractAttributeTagProcessor {
    
    private static final String ATTRIBUTE_NAME = "hasPermission";
    
    private static final int PRECEDENCE = 300;
    
    private static final String DELIMITER = ",";
    
    public HasPermissionAttrProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }
    
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
            String attributeValue, IElementTagStructureHandler structureHandler) {
        String rawValue = getRawValue(tag, attributeName);
        List<String> values = evaluateAsStringsWithDelimiter(context, rawValue, DELIMITER);
        
        if (SecurityFacade.hasAllPermissions(values)) {
            structureHandler.removeAttribute(attributeName);
        } else {
            structureHandler.removeElement();
        }
    }
}
