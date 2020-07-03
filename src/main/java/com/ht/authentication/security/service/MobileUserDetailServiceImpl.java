package com.ht.authentication.security.service;

import com.ht.authentication.mapper.SsoUserMapper;
import com.ht.authentication.model.SsoRole;
import com.ht.authentication.model.UserRoleVo;
import com.ht.authentication.util.BeanUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通过手机号获取用户信息和资源
 */
public class MobileUserDetailServiceImpl implements UserDetailsService {

    private SsoUserMapper ssoUserMapper = BeanUtil.getBean(SsoUserMapper.class);

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        Optional<UserRoleVo> optional = Optional.ofNullable(ssoUserMapper.searchUserByMobile(mobile));
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
