package com.xxx.user.utils;

import com.xxx.core.cache.RedisUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


public class SessionUtils {

    public static final String DATA = "DATA";
    public static final String SUPPLIERID = "SUPPLIERID";
    public static final String TOKEN = "TOKEN";

    public static final String PAGETYPE = "PAGETYPE";
    public static final String OPENID = "OPENID";
    public static final String BRANDID = "BRANDID";
    public static final String PAYAPPID = "PAYAPPID";
    public static final String PAYAPPSECRET = "PAYAPPSECRET";



    public static void setSession(HttpServletResponse response,String cookieName,int expireSeconds,Object object){
        String cacheKey = "";
        cacheKey = UUID.randomUUID().toString();
        try {
            RedisUtils.setex(cacheKey, expireSeconds, object);
            Cookie cookie = new Cookie(cookieName,cacheKey);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getSession(HttpServletRequest request, String cookieName){
        if (request == null) return null;
        Cookie[] cookies = request.getCookies();
        Object object = new Object();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    String key = cookie.getValue();
                    object = RedisUtils.get(key);
                    return object;
                }
            }
        }
        return null;
    }

}
