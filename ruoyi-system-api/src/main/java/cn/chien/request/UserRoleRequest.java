package cn.chien.request;

import lombok.Data;

/**
 * @author qiandq3
 * @date 2022/12/6
 */
@Data
public class UserRoleRequest extends BaseRequest {
    
    private Long userId;
    
    private Long[] roleIds;
    
}
