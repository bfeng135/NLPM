package com.nl.pm.server.controller;


import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.config.Configuration;
import com.nl.pm.server.controller.vo.ReqLoginVO;
import com.nl.pm.server.controller.vo.ResLoginVO;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.operationLog.BaseFormatter;
import com.nl.pm.server.operationLog.OperationLog;
import com.nl.pm.server.security.provider.BaseAuthenticationProvider;
import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.service.IAreaService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Date;

@RestController
@Api(value = "登录",tags = {"登录管理"})
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private Configuration conf;
    @Autowired
    private TokenTools tokenTools;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IAreaService iAreaService;
    @Autowired
    private BaseAuthenticationProvider baseAuthenticationProvider;
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @ApiOperation(value = "登录",notes = "登录接口")
    @PostMapping("/login")
    @OperationLog(formatter = BaseFormatter.class)
    public ResLoginVO login(@RequestBody ReqLoginVO reqLoginVO) throws Exception {

        Integer areaId = reqLoginVO.getAreaId();
        if(areaId == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }else{
            TokenTools.CURRENT_AREA_ID.set(areaId);
        }

        String username = reqLoginVO.getUsername();
        String password = reqLoginVO.getPassword();
        password = tokenTools.md5Security(password);

        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = baseAuthenticationProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String credentials = (String) authenticate.getCredentials();
        if ("".equals(credentials)) {
            throw new BaseAuthException(AuthErrorCodeEnum.PASSWORD_NULL_ERROR);
        }

        boolean authenticated = authenticate.isAuthenticated();
        if (authenticated) {

            UserModel model = iUserService.getUserByUsernameAndPasswordAndAreaId(username, password,areaId);
            if (model == null) {
                throw new BaseAuthException(AuthErrorCodeEnum.USER_NONE_ERROR);
            }

            Long jwt_expire_time = conf.getJWT_EXPIRE_TIME();
            Date expireDate = new Date();
            expireDate.setTime(expireDate.getTime() + jwt_expire_time);

            //token 设置当前登录的区域 id，不是隶属的 id，解决区长兼职其他区的时候登陆的数据问题
            model.setAreaId(areaId);
            AreaModel areaInfo = iAreaService.getAreaInfo(areaId);
            model.setAreaName(areaInfo.getName());
            String token = tokenTools.generateJwtToken(expireDate, model);

            iUserService.loginTokenUpdate(username,token);

            ResLoginVO resVO = new ResLoginVO();
            resVO.setId(model.getId());
            resVO.setUsername(model.getUsername());
            resVO.setNickname(model.getNickname());
            resVO.setRoleCode(model.getRoleCode());
            resVO.setAreaId(model.getAreaId());
            resVO.setToken(token);

            Integer currentLoginUserId = securityContextUtils.getCurrentUserId();
            UserModel userBelongModel = iUserService.queryUserInfoById(currentLoginUserId.longValue());
            Integer userBelongAreaId = userBelongModel.getAreaId();
            Integer currentLoginAreaId = securityContextUtils.getCurrentUserAreaId();
            if(userBelongAreaId.equals(currentLoginAreaId)){
                resVO.setReadonly(false);
            }else{
                resVO.setReadonly(true);
            }

            log.info("登录用户：" + resVO.getUsername());
            return resVO;
        } else {
            throw new BaseAuthException(AuthErrorCodeEnum.LOGIN_ERROR);
        }
    }
    @GetMapping("/logout/{username}")
    public void logout(@PathParam("username") String username){
        String currentUsername = securityContextUtils.getCurrentUsername();
        iUserService.logout(currentUsername);
    }

}
