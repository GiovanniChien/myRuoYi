package cn.chien.request;

import cn.chien.domain.entity.SysRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleListPageQueryRequest  extends BasePageRequest<SysRole> {
    
    private Long roleId;

    private String roleKey;
    
    private String roleName;
    
    private String status;
    
    private String dataScope;
    
}
