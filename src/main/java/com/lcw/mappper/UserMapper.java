package com.lcw.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lcw.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author ManGo
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {

}
