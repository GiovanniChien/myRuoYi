package cn.chien.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
@Data
public class UserListPageQueryRequest extends BasePageRequest {
    
    private Long userId;
    
    private String loginName;
    
    private String status;
    
    private String phonenumber;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    
    private Long deptId;
}
