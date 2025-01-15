package com.nl.pm.server.security.filter;

import com.nl.pm.server.common.UrlUtils;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.model.MessageResult;
import com.nl.pm.server.security.provider.BaseAuthenticationProvider;
import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenTools tokenTools;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BaseAuthenticationProvider baseAuthenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String pathInfo = request.getRequestURI();
        if (pathInfo == null) {
            pathInfo = "";
        }
        //放行swagger资源
        if (pathInfo.startsWith(UrlUtils.BASE_URL + UrlUtils.SWAGGER_WEBJARS)) {
            filterChain.doFilter(request, response);
        } else if (pathInfo.startsWith(UrlUtils.BASE_URL + UrlUtils.SWAGGER_RESOURCES)) {
            filterChain.doFilter(request, response);
        } else {
            switch (pathInfo) {
                //放行swagger访问
                case UrlUtils.BASE_URL + UrlUtils.SWAGGER_URL:
                case UrlUtils.BASE_URL + UrlUtils.SWAGGER_V2_API_DOCS_URL:
                    //系统名称放行
                case UrlUtils.BASE_URL + UrlUtils.SYSTEM_NAME:
                    //放行登录访问
                case UrlUtils.BASE_URL + UrlUtils.LOGIN_URL:
                    //放行区域列表
                case UrlUtils.BASE_URL + UrlUtils.AREA_LIST_URL:

                    filterChain.doFilter(request, response);
                    break;
                default:
                    String authHeader = request.getHeader(tokenTools.getAuthHeader());
                    if (authHeader != null && authHeader.startsWith(authHeader)) {
                        final String token = authHeader.substring(tokenTools.getBearerHead().length());
                        try {
                            UserModel userModel = tokenTools.checkJwtToken(token);
                            if (userModel != null) {
                                //先设置区域ID
                                Integer areaId = userModel.getAreaId();
                                if (areaId != null) {
                                    TokenTools.CURRENT_AREA_ID.set(areaId);
                                }
                                //再验证用户有效性
                                String username = userModel.getUsername();
                                String password = userModel.getPassword();
                                Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
                                Authentication authenticate = baseAuthenticationProvider.authenticate(auth);
                                SecurityContextHolder.getContext().setAuthentication(authenticate);

                                filterChain.doFilter(request, response);
                            }
                        } catch (BaseAuthException e) {
                            responseErr(response);
                        }

                    } else {
                        responseErr(response);
                    }
                    break;
            }
        }

    }


    private void responseErr(HttpServletResponse response) throws IOException {
        MessageResult messageResult = new MessageResult();
        messageResult.setErrorCode(AuthErrorCodeEnum.NO_AUTH.getErrorCode());
        messageResult.setErrorMessage(AuthErrorCodeEnum.NO_AUTH.getErrorMessage());
        response.setContentType("application/json");
        response.setStatus(AuthErrorCodeEnum.NO_AUTH.getHttpCode());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(messageResult.toString());
    }
}
