package cn.chien.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleBinderRequest implements Serializable {
    
    private Long roleId;
    
    private Long[] userIds;
    
}
