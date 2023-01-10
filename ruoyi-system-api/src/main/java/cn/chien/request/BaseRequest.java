package cn.chien.request;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
@Data
public class BaseRequest implements Serializable {
    
    private Map<String, Object> params;
    
    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

}
