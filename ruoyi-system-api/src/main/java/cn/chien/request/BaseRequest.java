package cn.chien.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
@Data
public class BaseRequest implements Serializable {
    
    private String dataScope;

}
