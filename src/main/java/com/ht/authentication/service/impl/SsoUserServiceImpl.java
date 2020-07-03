package com.ht.authentication.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.authentication.mapper.SsoUserMapper;
import com.ht.authentication.model.JsonResult;
import com.ht.authentication.model.SsoUser;
import com.ht.authentication.service.SsoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userServiceImpl")
public class SsoUserServiceImpl extends ServiceImpl<SsoUserMapper, SsoUser> implements SsoUserService {

    @Autowired
    private SsoUserMapper ssoUserMapper;

    @Override
    public JsonResult<IPage<SsoUser>> searchUserList(Integer currtentPage, Integer size, String name) {
        Page<SsoUser> page = new Page<>();
        page.setCurrent(currtentPage).setSize(size);
        IPage<SsoUser> userIPage = ssoUserMapper.searchUserList(page,name);
        return JsonResult.success(userIPage);
    }
}
