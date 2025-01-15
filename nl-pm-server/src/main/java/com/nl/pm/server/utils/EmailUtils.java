package com.nl.pm.server.utils;


import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {
    public static Boolean emailVerify(String email) throws BaseServiceException {
        if(StringUtils.isEmpty(email) || email.length() < 8){
            throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
        }
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence) email.trim());
        return matcher.matches();
    }
}
