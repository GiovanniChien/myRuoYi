package cn.chien.util;

import cn.chien.core.page.TableDataInfo;
import cn.chien.request.BasePageRequest;
import cn.chien.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.function.Function;

/**
 * @author qiandq3
 * @date 2022/11/25
 */
public final class PageUtil {
    
    private PageUtil() { }
    
    public static <T> TableDataInfo getTableDataInfo(IPage<T> page) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }
    
    public static <T> TableDataInfo queryPageList(BasePageRequest<T> basePageRequest, Function<IPage<T>, IPage<T>> function) {
        Page<T> page;
        if (basePageRequest.getPageNum() != null && basePageRequest.getPageSize() != null) {
            page = new Page<>(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            if (StringUtils.isNotEmpty(basePageRequest.getOrderByColumn())) {
                if ("asc".equals(basePageRequest.getIsAsc())) {
                    page.addOrder(OrderItem.asc(StringUtils.toUnderScoreCase(basePageRequest.getOrderByColumn())));
                } else {
                    page.addOrder(OrderItem.desc(StringUtils.toUnderScoreCase(basePageRequest.getOrderByColumn())));
                }
            }
        } else {
            // 不分页
            page = new Page<>(-1, -1);
        }
        IPage<T> apply = function.apply(page);
        return PageUtil.getTableDataInfo(apply);
    }
    
}
