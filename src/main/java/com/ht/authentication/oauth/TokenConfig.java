package com.ht.authentication.oauth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * oauth token 配置管理
 * JWT 非对称加密
 * 公钥与私钥是一对  私钥加密 公钥解密
 * 使用jdk 工具生成秘钥
 * cmd 窗口使用keytool
 * keytool -genkeypair -alias oauth2 -keyalg RSA -keypass oauth2 -keystore oauth2.jks
 * 将oauth2.jks 复制到项目中
 */
@Configuration
public class TokenConfig {

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //采用非对称加密 第一个参数是秘钥证书文件 第二个参数是秘钥证书密码 使用私钥进行加密
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("oauth2.jks"),"oauth2".toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth2"));
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore(){
        //jwt 管理令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

}
