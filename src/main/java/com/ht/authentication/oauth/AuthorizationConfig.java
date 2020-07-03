package com.ht.authentication.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer //开启认证服务器
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource(name = "userDetailServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    /**
     * 令牌端点的安全配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //所有人可访问/oauth/token_key 获取公钥 默认拒绝访问
                .tokenKeyAccess("permitAll()")
                //认证后可访问/oauth/check_token 检查token合法性 默认拒绝访问
                .checkTokenAccess("isAuthenticated()");
    }

    /**
     * 授权码管理策略
     * @return
     */
    @Bean
    public AuthorizationCodeServices jdbcAuthorizationCodeServices(){
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * 配置允许访问此认证服务器的客户端信息
     * 1.内存方式
     * 2.数据库方式
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //基于内存的配置模式
//        clients
//                .inMemory()
//                .withClient("test")//客户端id
//                .secret(passwordEncoder.encode("111111"))//客户端密码
//                .resourceIds("producer-server") //资源id，对应的就是微服务的id
//                //授权模式 也可以指定多种模式 refresh_token用作刷新token
//                .authorizedGrantTypes("authorization_code","password","refresh_token")
//                .scopes("all")//授权范围表示，使用资源服务器时候对应这个标识 all代表标识不是所有资源
//                .autoApprove(true)//是否手动点击跳转授权 false 代表不需要自动跳转授权
//                .redirectUris("http://www.baidu.com/");//客户端重定向url 授权码就会带到这个地址后面
//        .and()//多个用and拼接 但是客户端id不能重复


//        //基于数据库的配置模式
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(clientDetailsService);

    }

    /**
     * 认证服务器端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //密码模式需要配置authenticationManager
                .authenticationManager(authenticationManager)
                //刷新令牌时候要指定userDetailsService 否则会报错 "Internal Server Error"
                .userDetailsService(userDetailsService)
                //jwt token 管理方式 再tokenConfig类中已经添加到容器中
                .tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                //授权码存储到数据库中
                .authorizationCodeServices(jdbcAuthorizationCodeServices());
    }

}
