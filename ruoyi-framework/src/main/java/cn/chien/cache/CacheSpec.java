package cn.chien.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qian.diqi
 * @date 2022/7/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheSpec {

    private String namespace;

    private Long maxInterval;

    private boolean refreshTtlAfterAccess;

}
