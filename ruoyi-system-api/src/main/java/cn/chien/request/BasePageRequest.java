package cn.chien.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
@Data
public class BasePageRequest<T> extends BaseRequest {
    
    private Integer pageNum;
    
    private Integer pageSize;
    
    private String orderByColumn;
    
    private Boolean reasonable;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    
    private String isAsc;
}
