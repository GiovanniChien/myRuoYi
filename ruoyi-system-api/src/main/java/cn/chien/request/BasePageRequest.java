package cn.chien.request;

import lombok.Data;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
@Data
public class BasePageRequest extends BaseRequest {
    
    private Integer pageNum;
    
    private Integer pageSize;
    
    private String orderByColumn;
    
    private Boolean reasonable;
    
}
