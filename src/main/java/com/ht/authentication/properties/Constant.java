package com.ht.authentication.properties;

public class Constant {

    /** 过滤器编码 **/
    public static final String FILTER_ENCODING="UTF-8";

    public static final String LOGIN_MOBILEIN="/auth/mobileIn";

    /** security 的中文提示认证 **/
    public static final String MESSAGE_ZH_CN="classpath:org/springframework/security/messages_zh_CN";

    /** security 在redis中资源对应角色的的key **/
    public static final String KEY = "security_resource_key";

    /** 发送网易云信短信验证码 **/
    public static final String CODE_SEND_URL ="https://api.netease.im/sms/sendcode.action";

    /** 网易云信验证码 app key **/
    public static final String CODE_APP_KEY ="2f1682f957c837eda9c334b30030c42b";

    /** 网易云信验证码 app 秘钥 **/
    public static final String CODE_APP_SECRET ="30b1333ca0e9";

}
