package com.ht.authentication.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ht.authentication.model.SsoRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author qp
 * @since 2020-01-23
 */
public interface SsoRoleMapper extends BaseMapper<SsoRole> {

    /**
     * 查询用户id查询角色列表
     * @param userId
     * @return
     */
    List<SsoRole> searchUserRoles(
            @Param("userId") Integer userId);
}
