package cn.chien.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author qiandq3
 * @date 2022/11/16
 */
@Data
public class ModifyPasswordRequest implements Serializable {
    
    @NotBlank
    private String oldPassword;
    
    @NotBlank
    private String newPassword;
    
}
