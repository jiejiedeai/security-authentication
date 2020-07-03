package com.ht.authentication.security.service;

import com.ht.authentication.mapper.SsoUserMapper;
import com.ht.authentication.model.SsoRole;
import com.ht.authentication.model.UserRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 登录认证逻辑
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private SsoUserMapper ssoUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //UserDetailsService 是spring提供的一个service 只有一个方法 判断用户名是否存在 并返回一个userDetai
        Optional<UserRoleVo> optional = Optional.ofNullable(ssoUserMapper.searchUserIsExists(username));
        optional.orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        UserRoleVo userRole = optional.get();
        List<SsoRole> ssoRoles = userRole.getSsoRoles();
        Collection<GrantedAuthority> authorities = getAuthorities(ssoRoles);
        //需要3个参数 用户名，密码，权限 其余4个校验超时、冻结、过期等默认为true
        User user =
                new User(userRole.getUsername(),
                        userRole.getPassword(),
                        authorities);
        return user;
    }

    /**
     * 将用户角色转换成Security需要的
     * @param ssoRoles
     * @return
     */
    private Collection<GrantedAuthority> getAuthorities(List<SsoRole> ssoRoles){
        if(!CollectionUtils.isEmpty(ssoRoles)){
            String [] roleNames = ssoRoles.stream().map(ssoRole -> ssoRole.getId().toString()).collect(Collectors.toList()).toArray(new String[ssoRoles.size()]);
            return AuthorityUtils.createAuthorityList(roleNames);
        }else{
            return AuthorityUtils.createAuthorityList();
        }
    }

}
