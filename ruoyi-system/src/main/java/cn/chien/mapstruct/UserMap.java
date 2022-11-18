package cn.chien.mapstruct;

import cn.chien.domain.entity.SysUser;
import cn.chien.request.ModifyPasswordRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author qiandq3
 * @date 2022/11/16
 */
@Mapper
public interface UserMap {

    UserMap INSTANCE = Mappers.getMapper(UserMap.class);
    
    SysUser modifyPasswordRequestToSysUser(ModifyPasswordRequest modifyPasswordRequest);

}
