package cn.chien.request;

import lombok.Data;

/**
 * @author qiandq3
 * @date 2022/11/16
 */
@Data
public class UserInfoModifyRequest {
    
    private String userName;
    
    private String email;
    
    private String phonenumber;
    
    private String sex;

}
