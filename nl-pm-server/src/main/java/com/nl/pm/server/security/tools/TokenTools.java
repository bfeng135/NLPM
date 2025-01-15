package com.nl.pm.server.security.tools;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.registry.IUserRegistry;
import com.nl.pm.server.service.model.UserModel;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenTools {
    public static ThreadLocal CURRENT_AREA_ID;
    private static final String SECRET = "I will tell you nothing";
    private static final String BEARER_HEAD = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";
    private static Date FOREVER_DATE;
    @Value("${init.user.password}")
    private String initPassword;

    @Autowired
    private IUserRegistry iUserRegistry;

    static {
        CURRENT_AREA_ID = new ThreadLocal<Integer>();
        FOREVER_DATE = new Date();
        FOREVER_DATE.setTime(FOREVER_DATE.getTime() + 1000 * 60 * 60 * 24 * 365 * 100);
    }

    public static String getBearerHead() {
        return BEARER_HEAD;
    }

    public static String getAuthHeader() {
        return AUTH_HEADER;
    }

    public static Integer getCurrentAreaId() {
        String areaIdStr = String.valueOf(CURRENT_AREA_ID.get());
        Integer areaId = Integer.parseInt(areaIdStr);
        return areaId;
    }

    public String generateJwtToken(Date expireDate, UserModel userModel) throws UnsupportedEncodingException {
        Builder jwtBuilder = JWT.create();
        //头
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", "HS256");
        headerMap.put("typ", "JWT");
        //过期时间
        Date expireDateTemp = new Date();
        if (expireDate == null) {
            expireDateTemp = FOREVER_DATE;
        } else {
            expireDateTemp = expireDate;
        }
        //数据
        Gson gson = new Gson();
        String userStr = gson.toJson(userModel);
        //生成token
        String token = JWT.create().withHeader(headerMap).withExpiresAt(expireDateTemp).withClaim("user", userStr).sign(Algorithm.HMAC256(SECRET));

        return token;
    }

    public UserModel checkJwtToken(String token) throws BaseAuthException, UnsupportedEncodingException {
        Integer count = iUserRegistry.checkTokenExist(token);

        if(count == null || count == 0){
            throw new BaseAuthException(AuthErrorCodeEnum.TOKEN_NOT_EXIST_ERROR);
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decode;
        try {
            decode =  verifier.verify(token);
        }catch (Exception e){
            throw new BaseAuthException(AuthErrorCodeEnum.TOKEN_SIGNATURE_ERROR);
        }

        String userStr = decode.getClaim("user").asString();
        Gson gson = new Gson();
        UserModel userModel = gson.fromJson(userStr, UserModel.class);
        return userModel;
    }

    public String md5Security(String str){
        if(StringUtils.isNotEmpty(str)){
            return DigestUtils.md5Hex(str);
        }
        return DigestUtils.md5Hex(initPassword);
    }
}
