package com.ht.authentication.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ht.authentication.properties.Constant;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 网易云信工具类
 */
public class WYCloudUtil {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 发送验证码
     * @param mobile
     * @return
     */
    public static JSONObject sendCodeNote(String mobile){
        JSONObject json=new JSONObject();
        CloseableHttpClient httpClient =  HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(Constant.CODE_SEND_URL);
        String appKey = Constant.CODE_APP_KEY;
        String appSecret = Constant.CODE_APP_SECRET;
        String nonce =  UUID.randomUUID().toString().replaceAll("-","");
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = getCheckSum(appSecret, nonce ,curTime);//获取CheckSum

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("mobile", mobile));
        nvps.add(new BasicNameValuePair("codeLen", "6"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // 执行请求
        HttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            String result= EntityUtils.toString(response.getEntity(), "utf-8");
            return JSON.parseObject(result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    // 计算并获取CheckSum
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
}
