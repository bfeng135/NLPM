package com.nl.pm.server.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.UserUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.service.IAreaService;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.IRoleService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.ProjectUserModel;
import com.nl.pm.server.service.model.RoleModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.*;
import com.nl.pm.server.utils.EmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "员工管理",tags = {"员工管理"})
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private TokenTools tokenTools;
    @Autowired
    private IAreaService areaService;

    @Autowired
    private IProjectService projectService;

    @ApiOperation(value = "添加员工",notes = "添加员工接口")
    @PostMapping("/add")
    public void addUser(@RequestBody ReqAddUserVO vo) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_BAN_OPERATE);
        }else if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            if(!securityContextUtils.getCurrentUserAreaId().equals(vo.getAreaId())){
                throw new BaseAuthException(AuthErrorCodeEnum.USER_ADD_AREA_ERROR);
            }
        }

        UserModel user = userService.queryUserByUserName(vo.getUsername());
        if(user != null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_EXISTING);
        }

        //区域
        if(vo.getAreaId() == 0 || vo.getAreaId() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }
        //角色
        if(vo.getRoleId() == 0 || vo.getRoleId() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.ROLEID_NULL_ERROR);
        }
        RoleModel roleModel = roleService.queryRoleByRoleId(vo.getRoleId());
//        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(roleModel.getCode())){
//            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//        }
//        if(currentUserModel.getRoleCode().equals(roleModel.getCode())){
//            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//        }
        if(!RoleTypeEnum.checkRole(currentUserModel.getRoleCode(),roleModel.getCode())){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }

        //如果指定的区长
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(roleModel.getCode())){
            //查询该区域下是否有角色
            Integer integer = roleService.queryCountByAreaId(vo.getAreaId());
            if(integer != null && integer > 0){
                throw new BaseServiceException(ServiceErrorCodeEnum.AREA_EXITS_MANAGER_ERROR);
            }
        }

        //邮件
        if(!EmailUtils.emailVerify(vo.getEmail())){
            throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
        }
        //手机号
        if(StringUtils.isNotEmpty(vo.getPhone()) && vo.getPhone().length() != 11){
            throw new BaseServiceException(ServiceErrorCodeEnum.PHONE_ERROR);
        }
        UserModel userModel = new UserModel();
        userModel.setUsername(vo.getUsername());
        userModel.setPassword(tokenTools.md5Security(UserUtils.INIT_PASSWORD));
        userModel.setNickname(vo.getNickname());
        userModel.setRoleId(vo.getRoleId());
        userModel.setAreaId(vo.getAreaId());
        userModel.setEmail(vo.getEmail());
        userModel.setPhone(vo.getPhone());
        //默认人员在职
        userModel.setStatus(true);
        //默认发送邮件
        userModel.setEmailNotice(true);

        UserModel userModel1 = userService.saveUser(userModel);
        //增加用户的入职节点
        userService.insertUserComeDate(userModel1.getId(),new Date());
        //如果指定的区长
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(roleModel.getCode())){
            AreaModel areaModel = new AreaModel();
            areaModel.setId(vo.getAreaId());
            areaModel.setManagerId(userModel1.getId());
            areaService.updateAreaById(areaModel);
            projectService.updateManagerByNameAndArea(vo.getAreaId(),userModel1.getId());
        }

    }

    @ApiOperation(value = "更新密码",notes = "更新密码接口")
    @PutMapping("/updatePassword")
    public void updatePassword(@RequestBody ReqUpdatePasswordVO vo) throws Exception {
        if (StringUtils.isEmpty(vo.getOldPassword())
                || StringUtils.isEmpty(vo.getNewPassword())
        || StringUtils.isEmpty(vo.getReNewPassword())){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_PASSWORD_ERROE);
        }
        if(!vo.getNewPassword().equals(vo.getReNewPassword())){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_PASSWORD_ERROE);
        }
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        UserModel userModel = userService.queryUserInfoById(vo.getUserId().longValue());
        UpdatePasswordModelParam param = new UpdatePasswordModelParam();
        param.setId(vo.getUserId());
        param.setPassword(tokenTools.md5Security(vo.getNewPassword()));

        if(currentUserModel.equals(userModel)){
            if(!tokenTools.md5Security(vo.getOldPassword()).equals(currentUserModel.getPassword())){
                throw new BaseServiceException(ServiceErrorCodeEnum.USER_PASSWORD_ERROE);
            }
            userService.updateUserPassword(param);
            return;
        }
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            throw new BaseAuthException(AuthErrorCodeEnum.USER_RESET_ERROR);
        }
        param.setToken("");
        userService.updateUserPassword(param);
    }

    @ApiOperation(value = "初始化密码",notes = "初始化密码接口")
    @PutMapping("/initPassword")
    public void initPassword(@RequestBody ReqInitUserPasswordVO vo) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        UserModel userModel = userService.queryUserInfoById(vo.getUserId().longValue());
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            throw new BaseAuthException(AuthErrorCodeEnum.USER_RESET_ERROR);
        }
//        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//            if(currentUserModel.getAreaId() != userModel.getAreaId()){
//                throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//            }
//        }
        InitUserPasswordModelParam param = new InitUserPasswordModelParam();
        param.setId(vo.getUserId());
        userService.initPassword(param);
    }

    @ApiOperation(value = "查询员工列表",notes = "查询员工列表接口")
    @PostMapping("/list")
    public BasePagesDomain<ResUserVO> queryUserPaging(@RequestBody ReqQueryUserListVO reqQueryUserListVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        QueryUserListModelParam modelParam = new QueryUserListModelParam();
        if(reqQueryUserListVO.getCurrentPage() != 0){
            modelParam.setCurrentPage(reqQueryUserListVO.getCurrentPage());
        }
        if(reqQueryUserListVO.getPageSize() != 0){
            modelParam.setPageSize(reqQueryUserListVO.getPageSize());
        }

        modelParam.setSearchVal(reqQueryUserListVO.getSearchVal());
        modelParam.setAreaId(reqQueryUserListVO.getAreaId());
        modelParam.setProjectId(reqQueryUserListVO.getProjectId());
        //modelParam.setProjectId(reqQueryUserListVO.getProjectId());
        modelParam.setRoleId(reqQueryUserListVO.getRoleId());
        modelParam.setNickname(reqQueryUserListVO.getNickname());

        UserModel userModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(userModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }

        //如果是组长、员工不可查询
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(userModel.getRoleCode())
            || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_BAN_OPERATE);
        }
        //如果是区长，查询当前区下人员
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            //modelParam.setAreaId(userModel.getAreaId());
            modelParam.setAreaId(securityContextUtils.getCurrentUserAreaId());
        }
        BasePagesDomain<UserModel> userModelBasePagesDomain = userService.queryUserPaging(modelParam);
        return toBasePagesDomain(modelParam.getCurrentPage(), modelParam.getPageSize(),userModelBasePagesDomain);

    }


    @ApiOperation(value = "根据角色查询员工列表",notes = "根据角色查询员工列表接口")
    @GetMapping("/role/list")
    public BasePagesDomain<ResUserVO> queryUserPagingByRole(@ApiParam(name = "pageNo", value = "页码", example = "1", required = true) @RequestParam(value = "pageNo", required = true) int pageNo,
                                @ApiParam(name = "pageSize", value = "单页记录条数", example = "20", required = true) @RequestParam(value = "pageSize", required = true) int pageSize,
                                @ApiParam(name = "searchVal", value = "检索条件") @RequestParam(value = "searchVal", required = false) String searchVal) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        List<Integer> ids = new ArrayList<>();
        UserModel userModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(userModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }
        BasePagesDomain<UserModel> userModelBasePagesDomain = null;
        //如果用户存在查询所有角色
        List<RoleModel> roleModelList = roleService.queryAll();

        //如果该用户是超级管理员，查询所有角色
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            userModelBasePagesDomain = userService.queryUserPaging(pageNo, pageSize, searchVal);
           return toBasePagesDomain(pageNo, pageSize,userModelBasePagesDomain);
        }

        //如果是财务，查询 财务、区长、组长、员工角色
        if(RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.FINANCE
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            userModelBasePagesDomain = userService.queryUserPagingByRoleIds(pageNo, pageSize, searchVal,ids);
            return toBasePagesDomain(pageNo, pageSize,userModelBasePagesDomain);
        }
        //如果是人事 查询 人事、区长、组长、员工角色
        if(RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.HR
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            userModelBasePagesDomain = userService.queryUserPagingByRoleIds(pageNo, pageSize, searchVal,ids);
            return toBasePagesDomain(pageNo, pageSize,userModelBasePagesDomain);
        }
        //如果是行政 查询 行政、区长、组长、员工角色
        if(RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.MANAGEMENT
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            userModelBasePagesDomain = userService.queryUserPagingByRoleIds(pageNo, pageSize, searchVal,ids);
            return toBasePagesDomain(pageNo, pageSize,userModelBasePagesDomain);
        }
        //如果是区长 查询 区长、组长、员工角色
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            userModelBasePagesDomain = userService.queryUserPagingByRoleIds(pageNo, pageSize, searchVal,ids);
            return toBasePagesDomain(pageNo, pageSize,userModelBasePagesDomain);
        }
        //如果是组长 查询组长、员工角色
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            userModelBasePagesDomain = userService.queryUserPagingByRoleIds(pageNo, pageSize, searchVal,ids);
            return toBasePagesDomain(pageNo, pageSize,userModelBasePagesDomain);
        }

        //BasePagesDomain<UserModel> userModelBasePagesDomain = userService.queryUserPaging(pageNo, pageSize, searchVal);

        return toBasePagesDomain(pageNo,pageSize,userModelBasePagesDomain);
    }

    @ApiOperation(value = "查询单个员工详细信息",notes = "查询单个员工详细信息接口")
    @GetMapping("/findUserInfo/{userId}")
    public ResUserVO queryUserInfoById(@PathVariable Long userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {

        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        UserModel userModel = userService.queryUserInfoById(userId);

        if(userModel != null){
            if(currentUserModel.equals(userModel)){
                return toResUserVO(userModel);
            }else {
                if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                        || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
                    return toResUserVO(currentUserModel);
                }
//                else if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//                    if(currentUserModel.getAreaId() != userModel.getAreaId()){
//                        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//                    }
//                }
                else {
                    return toResUserVO(userModel);
                }
            }
        }
       return toResUserVO(userModel);
    }

    @ApiOperation(value = "修改员工状态",notes = "修改员工状态接口")
    @GetMapping("/editUserStatus/{userId}")
    public void editUserStatus(@PathVariable Long userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        UserModel userModel = userService.queryUserInfoById(userId);
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_BAN_OPERATE);
        }
//        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//            if(currentUserModel.getAreaId() != userModel.getAreaId()){
//                throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//            }
//        }
        userService.editUserStatus(userId);
    }

    @ApiOperation(value = "删除员工",notes = "删除员工状态接口")
    @DeleteMapping("/delUser/{userId}")
    public void delUserByUserId(@PathVariable Long userId) throws BaseServiceException {
        Integer integer = userService.queryProjectCountByUserId(userId.intValue());
        if(integer != null && integer > 0){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_DELETE);
        }
        Integer integer2 = userService.queryReportCountByUserId(userId.intValue());
        if(integer2 != null && integer2 > 0){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_DELETE);
        }

        userService.delUserByUserId(userId);
    }

    @ApiOperation(value = "检查用户名是否可用",notes = "检查用户名接口")
    @PostMapping("/checkUsername")
    public void checkUsername(@RequestBody ReqCheckUserNameVO reqCheckUserNameVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        UserModel userModel = userService.queryUserByUserName(reqCheckUserNameVO.getUsername());
        if(userModel != null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_EXISTING);
        }
        throw new BaseServiceException(ServiceErrorCodeEnum.USER_CAN_USE);
    }

    @ApiOperation(value = "根据区域ID和角色CODE查找用户",notes = "根据区域ID和角色CODE查找用户接口")
    @PostMapping("/searchUserByRoleOrArea")
    public List<ResUserVO> searchUserByRoleOrArea(@RequestBody ReqSearchUserByRoleOrAreaVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        SearchUserByRoleOrAreaModelParam param = new SearchUserByRoleOrAreaModelParam();
        if(vo.getAreaId() == 0){
            vo.setAreaId(null);
        }
        param.setAreaId(vo.getAreaId());
        param.setListRoleCode(vo.getListRoleCode());

        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_BAN_OPERATE);
        }else if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            if(securityContextUtils.getCurrentUserAreaId() != vo.getAreaId()){
                throw new BaseAuthException(AuthErrorCodeEnum.USER_OPERATE_AREA_ERROR);
            }
            //vo.setAreaId(currentUserModel.getAreaId());
            if(vo.getListRoleCode().size() > 0){
                List<RoleTypeEnum> list = new ArrayList<>();
                list.add(RoleTypeEnum.SUPER_ADMIN);
                list.add(RoleTypeEnum.FINANCE);
                list.add(RoleTypeEnum.HR);
                list.add(RoleTypeEnum.MANAGEMENT);
                vo.getListRoleCode().removeAll(RoleTypeEnum.convertToString(list));
            }
            param.setAreaId(vo.getAreaId());
        }
        param.setListRoleCode(clearSpecialString(vo.getListRoleCode()));
        List<UserModel> list = userService.searchUserByRoleOrArea(param);
        List<UserModel> assList = userService.searchUserByAss(securityContextUtils.getCurrentUserAreaId(),param.getListRoleCode());
        if(assList.size() > 0){
            for(UserModel model:assList){
                list.add(model);
            }
        }
        return toListResUserVO(list);
    }

    @ApiOperation(value = "查询当前区域下项目可被分配的成员列表",notes = "查询当前区域下项目可被分配的成员列表")
    @GetMapping("/searchUserWhoCanBeAssignToProjectByAreaId/{areaId}")
    public List<ResUserVO> searchUserWhoCanBeAssignToProjectByAreaId(@PathVariable("areaId") Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ResUserVO> voList = new ArrayList<>();
        List<UserModel> modelList = userService.searchUserWhoCanBeAssignToProjectByAreaId(areaId);
        if(CollectionUtils.isNotEmpty(modelList)){
            for (UserModel userModel : modelList) {
                ResUserVO vo =new ResUserVO();
                vo.setId(userModel.getId());
                vo.setAreaId(userModel.getAreaId());
                vo.setAreaName(userModel.getAreaName());
                vo.setUsername(userModel.getUsername());
                vo.setNickname(userModel.getNickname());
                vo.setRoleId(userModel.getRoleId());
                vo.setRoleName(userModel.getRoleName());
                vo.setEmail(userModel.getEmail());
                vo.setEmailNotice(userModel.getEmailNotice());
                vo.setPhone(userModel.getPhone());
                vo.setStatus(userModel.getStatus());
                vo.setCreateTime(DateUtils.convertDateToLong(userModel.getCreateTime()));
                vo.setUpdateTime(DateUtils.convertDateToLong(userModel.getUpdateTime()));
                voList.add(vo);
            }

        }
        return voList;
    }

    @ApiOperation(value = "根据项目ID查询项目关联用户",notes = "根据项目ID查询项目关联用户")
    @GetMapping("/searchUserByProjectId/{projectId}")
    public List<ResUserVO> searchUserByProjectId(@PathVariable("projectId") Integer projectId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        List<ProjectUserModel> allProjectUserById = projectService.getAllProjectUserById(projectId);
        List<ResUserVO> list = new ArrayList<>();
        for (ProjectUserModel projectUserModel : allProjectUserById) {
            ResUserVO vo = new ResUserVO();
            vo.setId(projectUserModel.getUserId());
            vo.setNickname(projectUserModel.getNickname());
            list.add(vo);
        }
        return list;
    }

    @ApiOperation(value = "根据项目负责人ID（组长 ID）查询关联组员用户",notes = "根据项目负责人ID（组长 ID）查询关联组员用户")
    @GetMapping("/searchUserByManagerId/{managerId}")
    public List<ResUserVO> searchUserByManagerId(@PathVariable("managerId") Integer managerId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        List<ProjectUserModel> allProjectUserById = projectService.getAllProjectUserByManagerId(managerId);
        List<ResUserVO> list = new ArrayList<>();
        for (ProjectUserModel projectUserModel : allProjectUserById) {
            ResUserVO vo = new ResUserVO();
            vo.setId(projectUserModel.getUserId());
            vo.setNickname(projectUserModel.getNickname());
            list.add(vo);
        }
        return list;
    }

    @ApiOperation(value = "根据区域ID查询关联组员用户",notes = "根据区域ID查询关联组员用户")
    @GetMapping("/searchUserByAreaId/{areaId}")
    public List<ResUserVO> searchUserByAreaId(@PathVariable("areaId") Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        List<ProjectUserModel> allProjectUserById = projectService.getAllProjectUserByAreaId(areaId);
        List<ResUserVO> list = new ArrayList<>();
        for (ProjectUserModel projectUserModel : allProjectUserById) {
            ResUserVO vo = new ResUserVO();
            vo.setId(projectUserModel.getUserId());
            vo.setNickname(projectUserModel.getNickname());
            list.add(vo);
        }
        return list;
    }



    @ApiOperation(value = "修改员工信息",notes = "修改员工信息接口")
    @PutMapping("/editUserInfo")
    @Transactional
    public void editUserInfo(@RequestBody ReqUpdateUserInfoVO vo) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        UserModel userModel = userService.queryUserInfoById(vo.getUserId().longValue());
        if(currentUserModel.equals(userModel)){
            //userModel.setRoleId(currentUserModel.getRoleId());
            //userModel.setAreaId(currentUserModel.getAreaId());
            //userModel.setStatus(currentUserModel.getStatus());
            //userModel.setEmailNotice(currentUserModel.getEmailNotice());
            userModel.setNickname(vo.getNickname());
            if(!EmailUtils.emailVerify(vo.getEmail().toLowerCase())){
                throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_ERROR);
            }
            if(StringUtils.isNotEmpty(vo.getPhone()) && vo.getPhone().length() != 11){
                throw new BaseServiceException(ServiceErrorCodeEnum.PHONE_ERROR);
            }
            userModel.setEmail(vo.getEmail().toLowerCase());
            userModel.setPhone(vo.getPhone());
            userService.editUserInfo(userModel);
            return;
        }
        RoleModel roleModel = roleService.queryRoleByRoleId(vo.getRoleId());
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(roleModel.getCode())){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        RoleModel roleModel2 = roleService.queryRoleByRoleId(userModel.getRoleId());
        //之前是区长，编辑完后不是区长，将区域的区长置为空
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(roleModel2.getCode())
        && RoleTypeEnum.AREA_MANAGER != RoleTypeEnum.convertToEnum(roleModel.getCode())){
            //AreaModel areaModel = areaService.queryAreaByUserId(vo.getAreaId(),vo.getUserId());
            areaService.updateAreaByAreaId(vo.getAreaId());
        }

        //如果指定的区长
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(roleModel.getCode())){
            //查询该区域下是否有角色
            Integer integer = roleService.queryCountByAreaId(vo.getAreaId());
            if(integer != null && integer > 0){
                throw new BaseServiceException(ServiceErrorCodeEnum.AREA_EXITS_MANAGER_ERROR);
            }

            AreaModel areaModel = new AreaModel();
            areaModel.setId(vo.getAreaId());
            areaModel.setManagerId(vo.getUserId());
            areaService.updateAreaById(areaModel);
            projectService.updateManagerByNameAndArea(vo.getAreaId(),vo.getUserId());


        }

        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            userService.editUserInfo(toUserModel(userModel,vo,true));
            return;
        }
        //RoleModel roleModel = roleService.queryRoleByRoleId(vo.getRoleId());
        if(RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(roleModel.getCode())
            || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(roleModel.getCode())
            || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(roleModel.getCode())
                    || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(roleModel.getCode())){
                throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
            }
            userService.editUserInfo(toUserModel(userModel,vo,true));
            return;
        }
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            userModel.setAreaId(currentUserModel.getAreaId());
            if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(roleModel.getCode())
                    || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(roleModel.getCode())
                    || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(roleModel.getCode())
                    || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(roleModel.getCode())
                    || RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(roleModel.getCode())){
                throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
            }
            userService.editUserInfo(toUserModel(userModel,vo,true));
        }
    }
    @ApiOperation(value = "根据用户本身角色查询项目关联成员下拉列表",notes = "根据用户本身角色查询项目关联成员下拉列表")
    @GetMapping("/byProjectAndArea")
    public List<ResUserVO> searchUserByProjectAndArea(@RequestParam(value = "areaId" , required = false) Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();


        if(currentUserRole == RoleTypeEnum.AREA_MANAGER
                || currentUserRole == RoleTypeEnum.GROUP_MANAGER
                || currentUserRole == RoleTypeEnum.EMPLOYEE){
            if( areaId!=null){
                if( currentUserAreaId!=areaId ) {
                    throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_USER_BELONG_ERROR);
                }
            }else{
                areaId=currentUserAreaId;
            }
        }
        Integer userId = null;
        if(currentUserRole == RoleTypeEnum.GROUP_MANAGER || currentUserRole == RoleTypeEnum.EMPLOYEE){
            userId = securityContextUtils.getCurrentUserId();
        }

        List<UserModel> list = userService.searchUserByProjectAndArea(areaId, userId);
        List<ResUserVO> resList = new ArrayList<>();
        for (UserModel userModel : list) {
            ResUserVO resUserVO = toResUserVO(userModel);
            resUserVO.setRoleId(userModel.getRoleId());
            resUserVO.setAreaId(userModel.getAreaId());
            resList.add(resUserVO);
        }
        return resList;
    }

    @ApiOperation(value = "修改邮件通知",notes = "修改邮件通知接口")
    @GetMapping("/editEmailNotice/{userId}")
    public void editEmailNotice(@PathVariable Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        UserModel userModel = userService.queryUserInfoById(userId.longValue());
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_BAN_OPERATE);
        }
//        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//            if(currentUserModel.getAreaId() != userModel.getAreaId()){
//                throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//            }
//        }
        userService.editEmailNotice(userId);
    }

    //toListResUserVO
    private List<ResUserVO> toListResUserVO(List<UserModel> list){
        List<ResUserVO> voList = new ArrayList<>();
        if(list.size() > 0){
            for(UserModel userModel:list){
                voList.add(toResUserVO(userModel));
            }
        }
        return voList;
    }

    //toResUserVO
    private ResUserVO toResUserVO( UserModel userModel){
        ResUserVO vo = new ResUserVO();
        vo.setId(userModel.getId());
        vo.setUsername(userModel.getUsername());
        vo.setNickname(userModel.getNickname());
        vo.setRoleName(userModel.getRoleName());
        vo.setAreaName(userModel.getAreaName());
        vo.setEmail(userModel.getEmail());
        vo.setPhone(userModel.getPhone());
        vo.setStatus(userModel.getStatus());
        vo.setEmailNotice(userModel.getEmailNotice());
        vo.setCreateTime(DateUtils.convertDateToLong(userModel.getCreateTime()));
        vo.setUpdateTime(DateUtils.convertDateToLong(userModel.getUpdateTime()));
        return vo;
    }

    //去掉前段list空字符串
    private List<String> clearSpecialString(List<String> list){
        List<String> specialString = new ArrayList<>();
        specialString.add("");
        if(list.size() > 0){
            for(String l:list){
                if(specialString.size() > 0 && list.size() > 0){
                    for (String s:specialString){
                        if(s.equals(l)){
                            list.remove(l);
                        }
                    }
                    break;
                }
            }
        }
        return list;
    }

    //toBasePagesDomain
    private BasePagesDomain<ResUserVO> toBasePagesDomain(int pageNo,int pageSize,BasePagesDomain<UserModel> userModelBasePagesDomain){
        List<ResUserVO> resUserInfoVOList = new LinkedList<>();
        if(userModelBasePagesDomain.getTotalList().size() > 0) {
            for (UserModel userModel : userModelBasePagesDomain.getTotalList()) {
                ResUserVO vo = new ResUserVO();
                vo.setId(userModel.getId());
                vo.setUsername(userModel.getUsername());
                vo.setNickname(userModel.getNickname());
                vo.setRoleId(userModel.getRoleId());
                vo.setRoleName(userModel.getRoleName());
                vo.setAreaId(userModel.getAreaId());
                vo.setAreaName(userModel.getAreaName());
                vo.setEmail(userModel.getEmail());
                vo.setPhone(userModel.getPhone());
                vo.setStatus(userModel.getStatus());
                vo.setEmailNotice(userModel.getEmailNotice());
                vo.setCreateTime(DateUtils.convertDateToLong(userModel.getCreateTime()));
                vo.setUpdateTime(DateUtils.convertDateToLong(userModel.getUpdateTime()));
                resUserInfoVOList.add(vo);
            }
        }
        BasePagesDomain<ResUserVO> pageInfo = new BasePagesDomain(pageNo,pageSize,userModelBasePagesDomain.getTotalPage(),userModelBasePagesDomain.getTotal());
        pageInfo.setTotalList(resUserInfoVOList);
        return pageInfo;
    }

    private UserModel toUserModel(UserModel userModel,ReqUpdateUserInfoVO vo,Boolean flag){
        UserModel model = new UserModel();
        model.setId(vo.getUserId());
        model.setNickname(vo.getNickname());
        model.setEmail(vo.getEmail());
        model.setPhone(vo.getPhone());
        model.setEmailNotice(vo.getEmailNotice());
        if(userModel.getRoleId() != vo.getRoleId()
        || userModel.getAreaId() != vo.getAreaId()){
            if(flag){
                model.setToken("");
            }
        }
        if(vo.getStatus() != null && userModel.getStatus() != vo.getStatus()){
            if(flag){
                model.setToken("");
            }
        }
        model.setRoleId(vo.getRoleId());
        model.setAreaId(vo.getAreaId());
        model.setStatus(vo.getStatus());
        return model;
    }


    @ApiOperation(value = "获取指定区域外所有用户",notes = "获取指定区域外所有用户")
    @GetMapping("/allUser/withoutArea/{areaId}")
    public List<ResUserVO> getAllOtherAreaUser(@PathVariable("areaId") Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        List<UserModel> allOtherAreaUser = userService.getAllOtherAreaUser(areaId);
        List<ResUserVO> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(allOtherAreaUser)){
            for (UserModel userModel : allOtherAreaUser) {
                ResUserVO vo = new ResUserVO();
                vo.setId(userModel.getId());
                vo.setUsername(userModel.getUsername());
                vo.setNickname(userModel.getNickname());
                vo.setRoleId(userModel.getRoleId());
                vo.setRoleName(userModel.getRoleName());
                vo.setAreaId(userModel.getAreaId());
                vo.setAreaName(userModel.getAreaName());
                vo.setEmail(userModel.getEmail());
                vo.setPhone(userModel.getPhone());
                vo.setStatus(userModel.getStatus());
                vo.setEmailNotice(userModel.getEmailNotice());
                vo.setCreateTime(DateUtils.convertDateToLong(userModel.getCreateTime()));
                vo.setUpdateTime(DateUtils.convertDateToLong(userModel.getUpdateTime()));
                list.add(vo);
            }
        }
        return list;
    }

    @ApiOperation(value = "查询区域下人员数量列表",notes = "查询区域下人员数量列表")
    @GetMapping("/board/areaUser")
    public List<ResBoardUserVO> searchAreaUserCountList() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        List<AreaUsersModelParam> areaUsersModelParams = userService.searchAreaUserCountList();
        List<ResBoardUserVO> voList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(areaUsersModelParams)){
            for (AreaUsersModelParam areaUsersModelParam : areaUsersModelParams) {
                ResBoardUserVO vo =new ResBoardUserVO();
                vo.setId(areaUsersModelParam.getAreaId());
                vo.setName(areaUsersModelParam.getAreaName());
                vo.setValue(areaUsersModelParam.getValue());
                voList.add(vo);
            }

        }
        return  voList;
    }

    @ApiOperation(value = "查询所选区域关联外部人员",notes = "查询所选区域关联外部人员")
    @PostMapping("/associated/otherAreaUser")
    public BasePagesDomain<ResUserVO> searchAssociatedOtherAreaUserList(@RequestBody ReqQueryUserListVO reqQueryUserListVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        int currentPage = reqQueryUserListVO.getCurrentPage()==0?1:reqQueryUserListVO.getCurrentPage();
        int pageSize = reqQueryUserListVO.getPageSize()==0?10:reqQueryUserListVO.getPageSize();
        Integer areaId = reqQueryUserListVO.getAreaId();
        if(RoleTypeEnum.SUPER_ADMIN == currentUserRole ||RoleTypeEnum.MANAGEMENT == currentUserRole ||RoleTypeEnum.HR == currentUserRole ){
            if(areaId==null){
                throw new BaseServiceException(ServiceErrorCodeEnum.AREANAME_NULL_ERROR);
            }
        }else if(RoleTypeEnum.AREA_MANAGER == currentUserRole){
            areaId = currentUserAreaId;
        }else{
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        BasePagesDomain<UserModel> userModelBasePagesDomain = userService.searchAssociatedOtherAreaUserList(currentPage, pageSize, areaId);

        return  toBasePagesDomain(currentPage, pageSize,userModelBasePagesDomain);
    }

}
