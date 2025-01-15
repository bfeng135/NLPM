package com.nl.pm.server.controller;

import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.controller.vo.ResRoleVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.IRoleService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.RoleModel;
import com.nl.pm.server.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "角色管理",tags = {"角色管理"})
@RequestMapping("/role")
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private IRoleService roleService;
    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "查询所有角色",notes = "查询所有角色接口")
    @GetMapping("/findAll")
    public List<ResRoleVO> queryAll() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ResRoleVO> resRoleVOS = new ArrayList<>();
        List<RoleModel> roleModelList = roleService.queryAll();

        return toListResRoleVO(resRoleVOS,roleModelList);
    }

    @ApiOperation(value = "根据角色查询角色",notes = "根据角色查询角色接口")
    @GetMapping("/findAllRoleByDifferentUser")
    public List<ResRoleVO> findAllRoleByDifferentUser() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<Integer> ids = new ArrayList<>();
        //所有角色Model列表
        List<RoleModel> roleModels = new ArrayList<>();
        //向前端返回的数据结构
        List<ResRoleVO> resRoleVOS = new ArrayList<>();

        //根据username查询用户详情
        UserModel userModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        //如果用户不存在，抛异常
        if(userModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }
        //如果用户存在查询所有角色
        List<RoleModel> roleModelList = roleService.queryAll();

        //如果该用户是超级管理员，查询所有角色
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
//            for(RoleModel model:roleModelList){
//                if(!"SUPER_ADMIN".equals(model.getCode())){
//                    roleModels.add(model);
//                }
//            }
            roleModels = roleModelList;
            return toListResRoleVO(resRoleVOS,roleModels);
        }
        //如果是财务，查询 财务、区长、组长、员工角色
        if(RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            roleModels = roleService.queryAllByRoleId(ids);
            return toListResRoleVO(resRoleVOS,roleModels);
        }
        //如果是人事 查询 人事、区长、组长、员工角色
        if(RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            roleModels = roleService.queryAllByRoleId(ids);
            return toListResRoleVO(resRoleVOS,roleModels);
        }
        //如果是行政 查询 行政、区长、组长、员工角色
        if(RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.AREA_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            roleModels = roleService.queryAllByRoleId(ids);
            return toListResRoleVO(resRoleVOS,roleModels);
        }
        //如果是区长 查询 区长、组长、员工角色
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.GROUP_MANAGER
                            || RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            roleModels = roleService.queryAllByRoleId(ids);
            return toListResRoleVO(resRoleVOS,roleModels);
        }
        //如果是组长 查询组长、员工角色
        if(RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(userModel.getRoleCode())){
            ids = roleModelList.stream().filter(
                    roleModel -> RoleTypeEnum.convertToEnum(roleModel.getCode()) == RoleTypeEnum.EMPLOYEE
            ).map(RoleModel::getId).distinct().collect(Collectors.toList());

            roleModels = roleService.queryAllByRoleId(ids);
            return toListResRoleVO(resRoleVOS,roleModels);
        }

        return toListResRoleVO(resRoleVOS,null);
    }

    private List<ResRoleVO> toListResRoleVO(List<ResRoleVO> resRoleVOS ,List<RoleModel> roleModels){
        //List<RoleModel> roleModels = roleService.queryAll();
        if(roleModels.size() > 0){
            for (RoleModel roleModel:roleModels){
                ResRoleVO vo = new ResRoleVO();
                vo.setId(roleModel.getId());
                vo.setName(roleModel.getName());
                resRoleVOS.add(vo);
            }
        }
        return resRoleVOS;
    }
}
