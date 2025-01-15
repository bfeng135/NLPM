package com.nl.pm.server.controller;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.EmailTypeEnum;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.service.IRoleService;
import com.nl.pm.server.service.ISystemService;
import com.nl.pm.server.service.ISystemVariableService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.RoleModel;
import com.nl.pm.server.service.model.SystemInfoModel;
import com.nl.pm.server.service.model.SystemVariableModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.utils.EmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "系统管理",tags = {"系统管理"})
@RequestMapping("/system")
public class SystemController {
    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private ISystemService systemService;
    @Autowired
    private IUserService userService;
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Autowired
    private ISystemVariableService iSystemVariableService;
    @Autowired
    private TokenTools tokenTools;


    @ApiOperation(value = "获取系统名",notes = "获取系统名")
    @GetMapping("/getSystemName")
    public String getSystemName() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<SystemInfoModel> modelList = systemService.queryAllSystemInfo();
        if(modelList.size() > 0){
            SystemInfoModel model = modelList.get(0);
            return model.getName();
        }
        return " ";
    }

    @ApiOperation(value = "添加系统邮件",notes = "添加系统邮件接口")
    @PostMapping("/addSystemEmail")
    private void addSystemEmail(@RequestBody ReqAddSystemInfoVO reqSystemVO) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            List<SystemInfoModel> modelList = systemService.queryAllSystemInfo();
            if(modelList.size() > 0){
                throw new BaseServiceException(ServiceErrorCodeEnum.SYSTEM_INFO_EXIST);
            }
            if(StringUtils.isEmpty(reqSystemVO.getName())){
                throw new BaseServiceException(ServiceErrorCodeEnum.SYSTEM_INFO_NAME_ERROR);
            }
//            if(!EmailUtils.emailVerify(reqSystemVO.getEmail())){
//                throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
//            }
//            if(StringUtils.isEmpty(reqSystemVO.getHost())
//                    || StringUtils.isEmpty(reqSystemVO.getPassword())){
//                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
//            }
//            reqSystemVO.setHost(reqSystemVO.getHost().toLowerCase());
//            reqSystemVO.setEmail(reqSystemVO.getEmail().toLowerCase());
//            Map<String, Boolean> map = EmailTypeEnum.compareEmail(reqSystemVO.getEmail(), reqSystemVO.getHost());
//            if(map.get("exits") && !map.get("email")){
//                throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
//            }
//            SystemInfoModel model = new SystemInfoModel();
//            model.setHost(reqSystemVO.getHost());
//            model.setPassword(reqSystemVO.getPassword());
//            model.setEmail(reqSystemVO.getEmail());
//            model.setName(reqSystemVO.getName());
//            model.setDesc(reqSystemVO.getDesc());
//            model.setCreateTime(new Date());
            SystemInfoModel model = new SystemInfoModel();
            model.setName(reqSystemVO.getName());
            model.setDesc(reqSystemVO.getDesc());
            systemService.addSystemEmail(model);
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    @ApiOperation(value = "查看系统邮件",notes = "查看系统邮件接口")
    @GetMapping("/findSystemEmail")
    public ResSystemInfoVO findSystemEmail() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            ResSystemInfoVO vo = new ResSystemInfoVO();
            List<SystemInfoModel> modelList = systemService.queryAllSystemInfo();
            if(modelList.size() > 0){
                SystemInfoModel model = modelList.get(0);
                vo.setId(model.getId());
                vo.setName(model.getName());
                vo.setDesc(model.getDesc());
                vo.setCreateTime(DateUtils.convertDateToLong(model.getCreateTime()));
                vo.setUpdateTime(DateUtils.convertDateToLong(model.getUpdateTime()));
            }
            return vo;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    @ApiOperation(value = "修改系统邮件",notes = "修改系统邮件接口")
    @PutMapping("/editSystemEmail")
    public void editSystemEmail(@RequestBody ReqEditSystemInfoVO reqSystemVO) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            //SystemInfoModel model = systemService.querySystemInfoById(reqSystemVO.getId());
            if(StringUtils.isEmpty(reqSystemVO.getName())){
                throw new BaseServiceException(ServiceErrorCodeEnum.SYSTEM_INFO_NAME_ERROR);
            }
//            if(!EmailUtils.emailVerify(reqSystemVO.getEmail())){
//                throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
//            }
//            if(StringUtils.isEmpty(reqSystemVO.getHost())
//                    || StringUtils.isEmpty(reqSystemVO.getPassword())){
//                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
//            }
//            Map<String, Boolean> map = EmailTypeEnum.compareEmail(reqSystemVO.getEmail(), reqSystemVO.getHost());
//            if(map.get("exits") && !map.get("email")){
//                throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
//            }
//            SystemInfoModel model = new SystemInfoModel();
//            model.setId(reqSystemVO.getId());
//            model.setHost(reqSystemVO.getHost().toLowerCase());
//            model.setPassword(reqSystemVO.getPassword());
//            model.setEmail(reqSystemVO.getEmail().toLowerCase());
//            model.setName(reqSystemVO.getName());
//            model.setDesc(reqSystemVO.getDesc());
//            model.setCreateTime(new Date());
//            model.setUpdateTime(new Date());
            SystemInfoModel model = new SystemInfoModel();
            model.setId(reqSystemVO.getId());
            model.setName(reqSystemVO.getName());
            model.setDesc(reqSystemVO.getDesc());
            systemService.editSystemEmail(model);
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    @ApiOperation(value = "设置系统日报修改最后期限")
    @PostMapping("/setSystemDailyDeadline")
    public Integer setSystemDailyDeadline(@RequestBody ReqAddSystemVariableVO reqAddSystemVariableVO,HttpServletRequest request, HttpServletResponse response)  throws Exception {
        String token = request.getHeader(tokenTools.getAuthHeader()).substring(tokenTools.getBearerHead().length());
        UserModel userModel = tokenTools.checkJwtToken(token);
        if(!RoleTypeEnum.checkEditSystemVariable(userModel.getRoleCode())){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_OPERATE_AREA_ERROR);
        }
        SystemVariableModel systemVariableModel = new SystemVariableModel();
        if(StringUtils.isNotEmpty(reqAddSystemVariableVO.getDeadline())){
            systemVariableModel.setDeadline(reqAddSystemVariableVO.getDeadline());
        }
        return  iSystemVariableService.setSystemDailyDeadline(systemVariableModel);
    }

    @ApiOperation(value = "获取系统变量信息")
    @GetMapping("/getSystemVariableInfo")
    public ReqSystemVariableInfoVO getSystemVariableInfo() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ReqSystemVariableInfoVO reqSystemVariableInfoVO = new ReqSystemVariableInfoVO();
        SystemVariableModel newDataInfo = iSystemVariableService.getNewDataInfo();
        BeanUtils.copyProperties(newDataInfo,reqSystemVariableInfoVO);
        return  reqSystemVariableInfoVO;
    }
}
