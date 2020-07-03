package com.ht.authentication.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.authentication.model.JsonResult;
import com.ht.authentication.model.SsoUser;

public interface SsoUserService extends IService<SsoUser> {

    /** 查询用户列表 **/
    JsonResult<IPage<SsoUser>> searchUserList(Integer currtentPage, Integer size, String name);
}
