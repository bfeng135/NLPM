package com.nl.pm.server.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EmailTypeEnum {
    NTES_SERVICE_ONE("163.com","smtp.163.com","163服务"),
    NTES_SERVICE_TWO("126.com","smtp.126.com","126服务"),
    QQ_SERVICE("qq.com","smtp.qq.com","qq服务"),

    ;
    private String email;
    private String host;
    private String desc;

    EmailTypeEnum(String email, String host, String desc) {
        this.email = email;
        this.host = host;
        this.desc = desc;
    }

    public static String convertToString(String email){
        String[] split = email.split("@");
        for(EmailTypeEnum e:EmailTypeEnum.values()){
            if(e.getEmail().equals(split[1])){
                return e.getHost();
            }
        }
        return StringUtils.EMPTY;
    }

    //主要判断 email  ....@163.com和host  smtp.163.com 是否属于同一服务商
    //判断邮箱是否在枚举中，在枚举中且邮箱有效exits true,email true;在枚举中邮箱无效exits true,email false,会向前段提示邮箱输入无效;没在枚举中 都返回false
    public static Map<String,Boolean> compareEmail(String email, String host){
        Map<String,Boolean> map = new HashMap<>();
        map.put("email",false);
        map.put("exits",false);
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(host)){
            return map;
        }
        //分割
        String[] split = email.split("@");
        //数组存储@email
        List<String> list = new ArrayList<>();
        for(EmailTypeEnum e:EmailTypeEnum.values()){
            list.add(e.getEmail());
        }
        //判断当前的email属于哪个服务商
        Boolean flag = false;
        if(list.contains(split[1])){
            for(EmailTypeEnum e:EmailTypeEnum.values()){
                if(e.getHost().equals(host)){
                    map.put("email",true);
                    map.put("exits",true);
                    flag = true;
                    return map;
                }
            }
            if(!flag){
                map.put("exits",true);
                return map;
            }
        }
        return map;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
