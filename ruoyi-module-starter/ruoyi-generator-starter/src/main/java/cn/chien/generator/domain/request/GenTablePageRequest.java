package cn.chien.generator.domain.request;

import cn.chien.generator.domain.GenTable;
import cn.chien.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenTablePageRequest extends BasePageRequest<GenTable> {

    private String tableName;
    
    private String tableComment;
    
}
