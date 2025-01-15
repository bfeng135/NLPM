package com.nl.pm.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.IAreaService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.AreaSearchParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "区域管理", tags = {"区域管理"})
@RequestMapping("/area")
public class AreaController {
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IUserService userService;

    /**
     * 获取区域详情
     *
     * @return
     */
    @ApiOperation("获取区域详情")
    @RequestMapping(method = RequestMethod.GET, value = "/getAreaInfo")
    public ResAreaVO getAreaInfo(@RequestParam("areaId") Integer areaId) throws Exception {
        if (areaId == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }
        AreaModel areaModel = areaService.getAreaInfo(areaId);

        if (areaModel != null) {
            ResAreaVO resAreaVO = new ResAreaVO();
            resAreaVO.setId(areaModel.getId());
            resAreaVO.setName(areaModel.getName());
            resAreaVO.setDesc(areaModel.getDesc());
            resAreaVO.setManagerId(areaModel.getManagerId());
            resAreaVO.setNickname(areaModel.getNickname());
            resAreaVO.setCreateTime(DateUtils.convertDateToLong(areaModel.getCreateTime()));
            resAreaVO.setUpdateTime(DateUtils.convertDateToLong(areaModel.getUpdateTime()));
            return resAreaVO;
        }else{
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_NULL_ERROR);
        }
    }

    /**
     * 获取区域列表
     *
     * @param reqAreaVO
     */
    @ApiOperation("获取区域列表")
    @RequestMapping(method = RequestMethod.POST, value = "/getAreaList")
    public BasePagesDomain<ResAreaVO> getAreaList(@RequestBody ReqAreaVO reqAreaVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int pageNo = reqAreaVO.getPageNumber() == null ? 1 : reqAreaVO.getPageNumber();
        int pageSize = reqAreaVO.getPageSize() == null ? 0 : reqAreaVO.getPageSize();

        AreaSearchParam areaSearchParam = new AreaSearchParam();
        areaSearchParam.setPageNumber(pageNo);
        areaSearchParam.setPageSize(pageSize);
        areaSearchParam.setGolbalName(reqAreaVO.getGolbalName());
        areaSearchParam.setStatusFlag(reqAreaVO.getStatusFlag());
        BasePagesDomain<AreaModel> areaPagesDomain = areaService.getAreaList(areaSearchParam);
        List<AreaModel> totalList = areaPagesDomain.getTotalList();

        List<ResAreaVO> resAreaVOList = new ArrayList<>();
        if (totalList != null) {
            for (AreaModel areaModel : totalList) {
                ResAreaVO resAreaVO = new ResAreaVO();
                resAreaVO.setId(areaModel.getId());
                resAreaVO.setName(areaModel.getName());
                resAreaVO.setStatus(areaModel.getStatus());
                resAreaVO.setDesc(areaModel.getDesc());
                resAreaVO.setManagerId(areaModel.getManagerId());
                resAreaVO.setNickname(areaModel.getNickname());
                resAreaVO.setCreateTime(DateUtils.convertDateToLong(areaModel.getCreateTime()));
                resAreaVO.setUpdateTime(DateUtils.convertDateToLong(areaModel.getUpdateTime()));
                resAreaVOList.add(resAreaVO);
            }
        }
        IPage<ResAreaVO> page = new Page<>();
        page.setRecords(resAreaVOList);
        page.setTotal(areaPagesDomain.getTotal());
        return new BasePagesDomain<ResAreaVO>(page);
    }

    /**
     * 新增区域
     *
     * @param reqAddAreaVO
     */
    @ApiOperation("新增区域")
    @RequestMapping(method = RequestMethod.POST, value = "/addArea")
    public Integer addArea(@RequestBody ReqAddAreaVO reqAddAreaVO) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if (currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT) {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH_OPERATE_AREA);
        }

        Integer managerId = reqAddAreaVO.getManagerId();
        //-区域负责人必须是区长才可以入选
        if (managerId != null) {
            UserModel userModel = userService.queryUserInfoById(reqAddAreaVO.getManagerId().longValue());
            if (userModel != null && RoleTypeEnum.AREA_MANAGER != RoleTypeEnum.convertToEnum(userModel.getRoleCode())) {
                //-区域负责人必须是区长才可以入选
                throw new BaseServiceException(ServiceErrorCodeEnum.AREA_MANAGERID_ERROR);
            }
        }

        AreaModel areaModel = new AreaModel();
        areaModel.setName(reqAddAreaVO.getName());
        areaModel.setDesc(reqAddAreaVO.getDesc());
        areaModel.setManagerId(reqAddAreaVO.getManagerId());
        if(reqAddAreaVO.getDesc().length() > 400){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_DESC_LENGTH_ERROR);
        }
        return areaService.addArea(areaModel);
    }

    /**
     * 编辑区域
     *
     * @param reqUpdateAreaVO
     */
    @ApiOperation("编辑区域")
    @RequestMapping(method = RequestMethod.PUT, value = "/updateArea")
    public void updateArea(@RequestBody ReqUpdateAreaVO reqUpdateAreaVO) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if (currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT) {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH_OPERATE_AREA);
        }
        if (reqUpdateAreaVO.getId() == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }

        Integer managerId = reqUpdateAreaVO.getManagerId();
        //-区域负责人必须是区长才可以入选
        if (managerId != null) {
            UserModel userModel = userService.queryUserInfoById(reqUpdateAreaVO.getManagerId().longValue());
            if (userModel != null && RoleTypeEnum.AREA_MANAGER != RoleTypeEnum.convertToEnum(userModel.getRoleCode())) {
                //-区域负责人必须是区长才可以入选
                throw new BaseServiceException(ServiceErrorCodeEnum.AREA_MANAGERID_ERROR);
            }
        }

        AreaModel areaModel = new AreaModel();
        areaModel.setId(reqUpdateAreaVO.getId());
        areaModel.setName(reqUpdateAreaVO.getName());
        areaModel.setDesc(reqUpdateAreaVO.getDesc());
        areaModel.setManagerId(reqUpdateAreaVO.getManagerId());
        areaService.updateArea(areaModel);
    }

    /**
     * 编辑区域状态
     *
     * @param reqVO
     */
    @ApiOperation("编辑区域状态")
    @RequestMapping(method = RequestMethod.PUT, value = "/updateStatus")
    public void updateAreaStatus(@RequestBody ReqUpdateAreaStatusVO reqVO) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if (currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT) {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH_OPERATE_AREA);
        }
        if(reqVO.getId()==null || reqVO.getId().equals(1)){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_OPERATE_ERROR);
        }
        if(reqVO.getStatus() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_STATUS_NULL_ERROR);
        }

        areaService.updateAreaStatus(reqVO.getId(),reqVO.getStatus());
    }

    /**
     * 删除区域
     *
     * @param areaId
     */
    @ApiOperation("删除区域")
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteArea")
    public void deleteArea(@RequestParam("areaId") Integer areaId) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if (currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT) {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH_OPERATE_AREA);
        }
        if (areaId == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }
        //该删除的区域下是否有人员存在
        Integer checkUser = areaService.checkAreaOtherUser(areaId);
        if (checkUser != null && checkUser > 0) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_DELETE_USER_EXIST_ERROR);
        }
        //该删除的区域下是否有日报存在
        Integer checkDayReport = areaService.checkAreaDayReport(areaId);
        if (checkDayReport != null && checkDayReport > 0) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_DELETE_DAY_REPORT_EXIST_ERROR);
        }
        //该删除的区域下是否有非默认的项目存在
        Integer checkProject = areaService.checkAreaOtherProject(areaId);
        if (checkProject != null && checkProject > 0) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_DELETE_OTHER_PROJECT_EXIST_ERROR);
        }


        areaService.deleteArea(areaId);
    }

    @ApiOperation(value = "分配人员到区域",notes = "分配人员到区域")
    @PostMapping("/assignUserToArea")
    public void assignUserToArea(@RequestBody ReqAreaAssignUserVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if(currentUserRole != RoleTypeEnum.SUPER_ADMIN
                && currentUserRole != RoleTypeEnum.MANAGEMENT
                && currentUserRole != RoleTypeEnum.HR){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH_CHOOSE_AREA_TO_USER);
        }
        Integer areaId = reqVO.getAreaId();
        List<Integer> userIdList = reqVO.getUserIdList();
        if(areaId == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }
        areaService.assignUserToArea(areaId,userIdList);

    }

    @ApiOperation(value = "查询指定区域关联的其他区人员列表",notes = "查询指定区域关联的其他区人员列表")
    @GetMapping("{id}/otherArea/userList")
    public List<ResOtherAreaAssUserVO> searchOtherAreaAssUser(@PathVariable("id") Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if(currentUserRole != RoleTypeEnum.SUPER_ADMIN
                && currentUserRole != RoleTypeEnum.MANAGEMENT
                && currentUserRole != RoleTypeEnum.HR
                && currentUserRole != RoleTypeEnum.AREA_MANAGER){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH_CHOOSE_AREA_TO_USER);
        }

        List<UserModel> list = areaService.searchOtherAreaAssUser(areaId);
        List<ResOtherAreaAssUserVO> resList = new ArrayList<>();
        for (UserModel userModel : list) {
            ResOtherAreaAssUserVO resOtherAreaAssUserVO = new ResOtherAreaAssUserVO();
            resOtherAreaAssUserVO.setAreaId(userModel.getAreaId());
            resOtherAreaAssUserVO.setAreaName(userModel.getAreaName());
            resOtherAreaAssUserVO.setUserId(userModel.getId());
            resOtherAreaAssUserVO.setNickname(userModel.getNickname());
            resList.add(resOtherAreaAssUserVO);
        }
        return resList;

    }

    @ApiOperation(value = "项目名称关联区域列表",notes = "项目名称关联区域列表")
    @PostMapping("/projectName/areaList")
    public List<ResAreaVO> projectNameASSAreaList(@RequestBody ReqProjectNameAssAreaVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        String projectName = reqVO.getProjectName();
        List<AreaModel> modelList = areaService.projectNameASSAreaList(projectName);
        List<ResAreaVO> voList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(modelList)){
            for (AreaModel areaModel : modelList) {
                ResAreaVO resAreaVO = new ResAreaVO();
                resAreaVO.setId(areaModel.getId());
                resAreaVO.setName(areaModel.getName());
                resAreaVO.setDesc(areaModel.getDesc());
                resAreaVO.setManagerId(areaModel.getManagerId());
                resAreaVO.setNickname(areaModel.getNickname());
                resAreaVO.setCreateTime(DateUtils.convertDateToLong(areaModel.getCreateTime()));
                resAreaVO.setUpdateTime(DateUtils.convertDateToLong(areaModel.getUpdateTime()));
                voList.add(resAreaVO);
            }
        }
        return voList;
    }

}
