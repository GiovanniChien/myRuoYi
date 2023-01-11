package cn.chien.request;

import cn.chien.domain.entity.SysUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
@Data
public class UserListPageQueryRequest extends BasePageRequest<SysUser> {
    
    private Long userId;
    
    private String loginName;
    
    private String status;
    
    private String phonenumber;
    
    private Long deptId;
    
    private Long roleId;
}
