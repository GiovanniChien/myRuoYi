package cn.chien.security.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author qiandq3
 * @date 2022/11/21
 */
public class HasAnyPermissionsElementProcessor extends AbstractElementTagProcessor {
    
    private static final String ELEMENT_NAME = "hasanypermissions";
    
    private static final int PRECEDENCE = 300;
    
    private static final String DELIMITER = ",";
    
    public HasAnyPermissionsElementProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ELEMENT_NAME, true, null, false, PRECEDENCE);
    }
    
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
            IElementTagStructureHandler structureHandler) {
        
    }
}
