package com.ht.authentication.security.config;

import com.ht.authentication.properties.Constant;
import com.ht.authentication.properties.SecurityProperties;
import com.ht.authentication.security.filter.image.ValiateImageCodeFilter;
import com.ht.authentication.security.filter.sms.ValidateSmsCodeFilter;
import com.ht.authentication.security.handler.CustomLogoutSuccessHandler;
import com.ht.authentication.security.handler.CustomerAuthenticationFailureHandler;
import com.ht.authentication.security.handler.CustomerAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //系统配置
    @Autowired
    private SecurityProperties securityProperties;

    //自定义登录成功
    @Autowired
    private CustomerAuthenticationSuccessHandler customerAuthenticationSuccessHandler;

    //自定义登录失败处理
    @Autowired
    private CustomerAuthenticationFailureHandler customerAuthenticationFailureHandler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private ValiateImageCodeFilter valiateImageCodeFilter;

    @Autowired
    private ValidateSmsCodeFilter validateSmsCodeFilter;

    @Autowired
    private MobileAuthenticationConfig mobileAuthenticationConfig;

    @Value("${server.servlet.session.cookie.name}")
    private String JSESSIONID;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 加载spring自己的中文认证提示信息
     * @return
     */
    @Bean
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        //后缀的.properties不要加 在ReloadableResourceBundleMessageSource中会自动拼接后缀
        messageSource.setBasename(Constant.MESSAGE_ZH_CN);
        return messageSource;
    }

    /**
     * 解决退出session不清空问题 此处初始化其他地方就可以依赖到
     * @return
     */
    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    /**
     * 记住我功能
     * @return
     */
    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        //启动项目时候自动创建记住我功能数据库表 只能首次使用 第二次使用会报错因为表已经存在
//        tokenRepository.setCreateTableOnStartup(true);
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors(withDefaults());// by default uses a Bean by the name of corsConfigurationSource
        //关闭csrf 跨站请求伪造 用于开发测试
        http.csrf().disable();

        //图形校验过滤器
        http.addFilterBefore(valiateImageCodeFilter,UsernamePasswordAuthenticationFilter.class);
        //验短信验证过滤器 短信认证过滤器在UsernamePasswordAuthenticationFilter之后
        http.addFilterBefore(validateSmsCodeFilter,UsernamePasswordAuthenticationFilter.class);
        http
                //使用form表单post方式进行登录
                .formLogin()
                //如果未认证过会走此接口包装了返回json结果给前端提示未认证
                .loginPage(securityProperties.getLoginPage())
                //真正的登录接口 UsernamePasswordAuthenticationFilter默认登录接口是/login 此处我们可以自定义接口但不一定存在
                .loginProcessingUrl(securityProperties.getLoginProcessingUrl())
                //自定登录成功处理
                .successHandler(customerAuthenticationSuccessHandler)
                //自定义登录失败处理
                .failureHandler(customerAuthenticationFailureHandler)
                .and()
                //允许不登陆就可以访问的方法，多个用逗号分隔
                .authorizeRequests()
                .antMatchers(
                        securityProperties.getAnonymous()
                ).permitAll()
                //退出
                .and().logout().permitAll()
                //其他的需要授权后访问
                .and().authorizeRequests().anyRequest().authenticated();
        //手机认证过滤器在UsernamePasswordAuthenticationFilter之后并将手机认证放在过滤器链上
        http.apply(mobileAuthenticationConfig);
        //security oauth 禁用session 因为是通过token控制的有效性 但是禁用了会导致securityContextHolder获取不到
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //解决中文乱码问题

        http
                .logout()
                .logoutUrl(securityProperties.getLogoutUrl())
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .deleteCookies(JSESSIONID);
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding(Constant.FILTER_ENCODING);
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);
    }

    /**
     * 一般针对静态资源进行放行
     * @param web
     * @throws Exception
     */
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(securityProperties.getStatics());
    }

}
