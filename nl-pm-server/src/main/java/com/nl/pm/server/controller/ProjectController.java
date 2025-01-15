package com.nl.pm.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.common.pages.BasePagesParam;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IAreaRegistry;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.ISystemProjectService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.*;
import com.nl.pm.server.service.param.DownProjectReportFormModelParam;
import com.nl.pm.server.service.param.ProjectAreaModelParam;
import com.nl.pm.server.service.param.ProjectSearchModelParam;
import com.nl.pm.server.service.param.ProjectUserCountModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(value = "项目管理",tags = {"项目管理"})
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProjectService iProjectService;
    @Autowired
    private ISystemProjectService iSystemProjectService;
    @Autowired
    private IAreaRegistry areaRegistry;
    @Autowired
    private IUserService userService;


    @ApiOperation(value = "新建项目",notes = "新建项目")
    @PostMapping("/create")
    public ResProjectUpdateVO createProject(@RequestBody ReqProjectCreateVO reqVO) throws Exception {

        Integer newAreaId = reqVO.getAreaId();
        if(newAreaId == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_AREA_NULL_ERROR);
        }
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            if(securityContextUtils.getCurrentUserAreaId() != newAreaId) {
                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_CREATE_AUTH_ERROR);
            }
        }else if(currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT){
            throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_CREATE_AUTH_ERROR);
        }

        Integer managerId = reqVO.getManagerId();
        if(managerId == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_MANAGER_NULL_ERROR);
        }
        UserModel userModel = iUserService.queryUserInfoById(managerId.longValue());
        if(userModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }
        if(userModel.getAreaId()!=newAreaId){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_USER_DIFFERENT_AREA_ERROR);
        }
        if(securityContextUtils.getCurrentUserAreaId()!=newAreaId){
            if(securityContextUtils.getCurrentUserRole()!=RoleTypeEnum.SUPER_ADMIN) {
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_USER_DIFFERENT_AREA_ERROR);
            }
        }
        if(RoleTypeEnum.convertToEnum(userModel.getRoleCode()) != RoleTypeEnum.AREA_MANAGER
                && RoleTypeEnum.convertToEnum(userModel.getRoleCode()) != RoleTypeEnum.GROUP_MANAGER){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_MANAGER_ROLE_ERROR);
        }

        String name = reqVO.getName();
        if(StringUtils.isEmpty(name)){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NAME_NULL_ERROR);
        }
        //检查当前区下是否重名
        Integer countThisArea = iProjectService.checkProjectNameExisted(newAreaId, name);
        if(countThisArea==null){
            countThisArea=0;
        }
        if(countThisArea>0){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NAME_EXISTED_ERROR);
        }

        //检查其他区域是否存在相同名称
        Integer countAllArea = iProjectService.checkProjectNameExisted(null, name);
        if(countAllArea==null){
            countAllArea=0;
        }

        ResProjectUpdateVO resProjectUpdateVO = new ResProjectUpdateVO();
        if(countAllArea - countThisArea > 0){
            if(reqVO.getForceFlag()!=true){
                resProjectUpdateVO.setAskForceUpdate(true);
                resProjectUpdateVO.setSuccess(true);
                return resProjectUpdateVO;
            }
        }

        ProjectModel pjModel = new ProjectModel();
        pjModel.setAreaId(newAreaId);
        pjModel.setName(name);
        pjModel.setDesc(reqVO.getDesc());
        pjModel.setManagerId(managerId);
        iProjectService.createProject(pjModel);

        resProjectUpdateVO.setAskForceUpdate(false);
        resProjectUpdateVO.setSuccess(true);
        return resProjectUpdateVO;
    }


    @ApiOperation(value = "编辑项目",notes = "编辑项目")
    @PostMapping("/update")
    public ResProjectUpdateVO updateProject(@RequestBody ReqProjectUpdateVO reqVO) throws Exception {

        Integer id = reqVO.getId();
        String newName = reqVO.getName();
        ProjectAdvanceModel model = iProjectService.fetchProject(id);

        if(model == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NULL_ERROR);
        }

        Integer projectAreaId = model.getAreaId();

        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            if(securityContextUtils.getCurrentUserAreaId() != projectAreaId) {
                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_CREATE_AUTH_ERROR);
            }
        }else if(currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT){
            throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_CREATE_AUTH_ERROR);
        }

        Integer managerId = reqVO.getManagerId();
        if(managerId == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_MANAGER_NULL_ERROR);
        }
        UserModel userModel = iUserService.queryUserInfoById(managerId.longValue());
        if(userModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }
        if(securityContextUtils.getCurrentUserAreaId()!=projectAreaId){
            if(securityContextUtils.getCurrentUserRole() != RoleTypeEnum.SUPER_ADMIN && securityContextUtils.getCurrentUserRole() != RoleTypeEnum.MANAGEMENT) {
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_USER_DIFFERENT_AREA_ERROR);
            }
        }
        if(RoleTypeEnum.convertToEnum(userModel.getRoleCode()) != RoleTypeEnum.AREA_MANAGER
                && RoleTypeEnum.convertToEnum(userModel.getRoleCode()) != RoleTypeEnum.GROUP_MANAGER){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_MANAGER_ROLE_ERROR);
        }

        String name = reqVO.getName();
        if(StringUtils.isEmpty(name)){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NAME_NULL_ERROR);
        }


        //检查项目所在区域是否存在相同名称
        Integer countThisArea = iProjectService.checkProjectNameExisted(projectAreaId, newName);
        if(countThisArea==null){
            countThisArea=0;
        }
        if(countThisArea>0){
            if(!model.getName().equals(newName)){
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NAME_EXISTED_ERROR);
            }
        }
        //检查其他区域是否存在相同名称
        Integer countAllArea = iProjectService.checkProjectNameExisted(null, newName);
        if(countAllArea==null){
            countAllArea=0;
        }

        ResProjectUpdateVO resProjectUpdateVO = new ResProjectUpdateVO();
        if(countAllArea - countThisArea > 0){
            if(reqVO.getForceFlag()!=true){
                resProjectUpdateVO.setAskForceUpdate(true);
                resProjectUpdateVO.setSuccess(true);
                return resProjectUpdateVO;
            }
        }

        //更新项目
        model.setName(reqVO.getName());
        model.setDesc(reqVO.getDesc());
        model.setManagerId(reqVO.getManagerId());
        iProjectService.updateProject(model);

        boolean addUserFlag = true;
        List<ProjectUserModel> allProjectUserById = iProjectService.getAllProjectUserById(id);
        if(!CollectionUtils.isEmpty(allProjectUserById)){
            for (ProjectUserModel projectUserModel : allProjectUserById) {
                Integer userId = projectUserModel.getUserId();
                if(userId == reqVO.getManagerId()){
                    addUserFlag = false;
                }
            }
        }

        if(addUserFlag){
            iProjectService.addUserWithProject(reqVO.getManagerId(),id);
        }


        //更新完成，通知前端
        resProjectUpdateVO.setAskForceUpdate(false);
        resProjectUpdateVO.setSuccess(true);
        return resProjectUpdateVO;
    }

    @ApiOperation(value = "查询指定项目详细内容",notes = "查询指定项目详细内容")
    @GetMapping("/{id}")
    public ResProjectDetailVO fetchProject(@PathVariable Integer id) throws Exception {
        ProjectAdvanceModel model = iProjectService.fetchProject(id);
        if(model == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NULL_ERROR);
        }
        List<ResProjectUserVO> projectUserList = new ArrayList<>();
        List<ProjectUserModel> allProjectUser = iProjectService.getAllProjectUserById(id);
        //查询项目关联成员
        Set<Integer> userIdSet = new HashSet<>();
        for (ProjectUserModel projectUserModel : allProjectUser) {
            Integer userId = projectUserModel.getUserId();
            userIdSet.add(userId);
            ResProjectUserVO puVO = new ResProjectUserVO();
            puVO.setUserId(userId);
            puVO.setNickname(projectUserModel.getNickname());
            projectUserList.add(puVO);
        }

        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        if(currentUserRole==RoleTypeEnum.AREA_MANAGER || currentUserRole==RoleTypeEnum.GROUP_MANAGER || currentUserRole==RoleTypeEnum.EMPLOYEE){
//            if(model.getAreaId()!=currentUserAreaId){
//                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_FETCH_AUTH_ERROR);
//            }
            if(currentUserRole==RoleTypeEnum.GROUP_MANAGER || currentUserRole==RoleTypeEnum.EMPLOYEE){
                if(!userIdSet.contains(securityContextUtils.getCurrentUserId())){
                    throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_FETCH_AUTH_ERROR);
                }
            }
        }


        ResProjectDetailVO resVO = new ResProjectDetailVO();
        resVO.setId(model.getId());
        resVO.setName(model.getName());
        resVO.setDesc(model.getDesc());
        resVO.setAreaId(model.getAreaId());
        resVO.setAreaName(model.getAreaName());
        resVO.setManagerId(model.getManagerId());
        resVO.setManagerName(model.getManagerName());
        resVO.setCreateTime(DateUtils.convertDateToLong(model.getCreateTime()));
        resVO.setUpdateTime(DateUtils.convertDateToLong(model.getUpdateTime()));
        resVO.setEnable(model.getEnable());
        resVO.setProjectUserList(projectUserList);
        return resVO;
    }


    @ApiOperation(value = "查询项目列表",notes = "查询项目列表")
    @PostMapping("/list")
    public BasePagesDomain<ResProjectVO> searchProjectList(@RequestBody ReqProjectSearchVO reqProjectSearchVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        ProjectSearchModelParam modelParam = new ProjectSearchModelParam();
        modelParam.setName(reqProjectSearchVO.getName());
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER || currentUserRole == RoleTypeEnum.GROUP_MANAGER || currentUserRole == RoleTypeEnum.EMPLOYEE){
            if(reqProjectSearchVO.getManagerId()!=null) {
                UserModel managerModel = iUserService.queryUserInfoById(reqProjectSearchVO.getManagerId().longValue());
                if (managerModel.getAreaId() != currentUserAreaId) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_USER_DIFFERENT_AREA_ERROR);
                }
            }
            modelParam.setAreaId(currentUserAreaId);

        }else{
            modelParam.setAreaId(reqProjectSearchVO.getAreaId());
        }
        modelParam.setUserId(reqProjectSearchVO.getUserId());
        modelParam.setPageSize(reqProjectSearchVO.getPageSize());
        modelParam.setCurrentPage(reqProjectSearchVO.getCurrentPage());
        modelParam.setEnableFlag(reqProjectSearchVO.getEnableFlag());
        BasePagesDomain<ProjectAdvanceModel> pmb = iProjectService.searchProject(modelParam);
        BasePagesDomain<ResProjectVO> res = new BasePagesDomain<>(pmb.getCurrentPage(),pmb.getPageSize(),pmb.getTotalPage(),pmb.getTotal());

        //判断当前登录人所属区是否为项目总负责区
        //如果是超级管理员，行政，财务，则可以点击跳转单项目工时详情页面
        //如果是总负责区的区长或者是总负责区的项目负责人，则可以跳转工时页面
        Map<String,Integer> ptMap = new HashMap<>();
        List<SystemProjectModel> allProjectTemplate = iSystemProjectService.findAllProjectTemplate();
        if(!CollectionUtils.isEmpty(allProjectTemplate)){
            for (SystemProjectModel systemProjectModel : allProjectTemplate) {
                ptMap.put(systemProjectModel.getName(),systemProjectModel.getAreaId());
            }
        }


        List<ResProjectVO> list = new ArrayList<>();
        for (ProjectAdvanceModel projectModel : pmb.getTotalList()) {
            ResProjectVO vo = new ResProjectVO();
            vo.setId(projectModel.getId());
            vo.setName(projectModel.getName());
            vo.setDesc(projectModel.getDesc());
            vo.setAreaId(projectModel.getAreaId());
            vo.setAreaName(projectModel.getAreaName());
            vo.setManagerId(projectModel.getManagerId());
            vo.setManagerName(projectModel.getManagerName());
            vo.setCreateTime(DateUtils.convertDateToLong(projectModel.getCreateTime()));
            vo.setUpdateTime(DateUtils.convertDateToLong(projectModel.getUpdateTime()));
            vo.setEnable(projectModel.getEnable());
            vo.setForceDescFlag(projectModel.getForceDescFlag());

            vo.setSystemProjectId(projectModel.getSystemProjectId());
            if(
                    ( ptMap.containsKey(projectModel.getName()) && currentUserId==projectModel.getManagerId()   && currentUserAreaId== ptMap.get(projectModel.getName()))
                    || (ptMap.containsKey(projectModel.getName()) && currentUserRole==RoleTypeEnum.AREA_MANAGER && currentUserAreaId== ptMap.get(projectModel.getName()))
                    ||(currentUserRole==RoleTypeEnum.SUPER_ADMIN ||currentUserRole==RoleTypeEnum.MANAGEMENT || currentUserRole==RoleTypeEnum.FINANCE )
            ){
                vo.setLeaderFlag(true);
            }else{
                vo.setLeaderFlag(false);
            }
            list.add(vo);
        }
        res.setTotalList(list);
        return res;

    }

    @ApiOperation(value = "查询当前登录用户关联的项目列表",notes = "查询当前登录用户关联的项目列表")
    @PostMapping("/list/assignUser")
    public BasePagesDomain<ResProjectVO> searchProjectListByAssignUser(@RequestBody BasePagesParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        IPage<ProjectAdvanceModel> modelPages = iProjectService.searchProjectListByAssignUser(param);

        //判断当前登录人所属区是否为项目总负责区
        //如果是超级管理员，行政，财务，则可以点击跳转单项目工时详情页面
        //如果是总负责区的区长或者是总负责区的项目负责人，则可以跳转工时页面
        Map<String,Integer> ptMap = new HashMap<>();
        List<SystemProjectModel> allProjectTemplate = iSystemProjectService.findAllProjectTemplate();
        if(!CollectionUtils.isEmpty(allProjectTemplate)){
            for (SystemProjectModel systemProjectModel : allProjectTemplate) {
                ptMap.put(systemProjectModel.getName(),systemProjectModel.getAreaId());
            }
        }

        List<ResProjectVO> list = new ArrayList<>();
        for (ProjectAdvanceModel projectModel : modelPages.getRecords()) {
            ResProjectVO vo = new ResProjectVO();
            vo.setId(projectModel.getId());
            vo.setName(projectModel.getName());
            vo.setDesc(projectModel.getDesc());
            vo.setAreaId(projectModel.getAreaId());
            vo.setAreaName(projectModel.getAreaName());
            vo.setManagerId(projectModel.getManagerId());
            vo.setManagerName(projectModel.getManagerName());
            vo.setCreateTime(DateUtils.convertDateToLong(projectModel.getCreateTime()));
            vo.setUpdateTime(DateUtils.convertDateToLong(projectModel.getUpdateTime()));
            vo.setEnable(projectModel.getEnable());
            vo.setSystemProjectId(projectModel.getSystemProjectId());
            vo.setForceDescFlag(projectModel.getForceDescFlag());

            if(
                    ( ptMap.containsKey(projectModel.getName()) && currentUserId==projectModel.getManagerId()   && currentUserAreaId== ptMap.get(projectModel.getName()))
                            || (ptMap.containsKey(projectModel.getName()) && currentUserRole==RoleTypeEnum.AREA_MANAGER && currentUserAreaId== ptMap.get(projectModel.getName()))
                            ||(currentUserRole==RoleTypeEnum.SUPER_ADMIN ||currentUserRole==RoleTypeEnum.MANAGEMENT || currentUserRole==RoleTypeEnum.FINANCE )
            ){
                vo.setLeaderFlag(true);
            }else{
                vo.setLeaderFlag(false);
            }
            list.add(vo);
        }

        IPage<ResProjectVO> voPages = new Page<>();
        voPages.setRecords(list);
        voPages.setCurrent(modelPages.getCurrent());
        voPages.setPages(modelPages.getPages());
        voPages.setSize(modelPages.getSize());
        voPages.setTotal(modelPages.getTotal());

        BasePagesDomain<ResProjectVO> resVO = new BasePagesDomain<>(voPages);
        return resVO;
    }

        @ApiOperation(value = "项目启用/禁用",notes = "项目启用/禁用")
    @GetMapping("/openOrClose/{id}")
    public void openOrClose(@PathVariable Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        ProjectAdvanceModel projectAdvanceModel = iProjectService.fetchProject(id);
        if(projectAdvanceModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_NULL_ERROR);
        }
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            Integer projectAreaId = projectAdvanceModel.getAreaId();
            if(projectAreaId != currentUserAreaId){
                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_OPERATE_AUTH_ERROR);
            }
        }else {
            if(currentUserRole != RoleTypeEnum.SUPER_ADMIN && currentUserRole != RoleTypeEnum.MANAGEMENT){
                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_OPERATE_AUTH_ERROR);
            }
        }

        Boolean enable = projectAdvanceModel.getEnable();

        Integer mainAreaId = projectAdvanceModel.getMainAreaId();
        Integer areaId = projectAdvanceModel.getAreaId();
        Integer systemProjectId = projectAdvanceModel.getSystemProjectId();
        //主负责区如果关闭项目，则将所有区对应的项目关闭
        if(mainAreaId!=null && areaId!=null && mainAreaId.equals(areaId)){
            if(enable==true){
                iProjectService.closeAllRelativeProjectBySystemProjectId(systemProjectId);
            }else{
                iProjectService.openOrClose(id,!enable);
            }
        }else{
            //如果非主负责区要开启项目，前提是主负责区必须是开启的。
            if(enable==false){
                ProjectModel mainProjectModel = iProjectService.fetchProjectBySystemProjectIdAndAreaId(systemProjectId, mainAreaId);
                if(mainProjectModel.getEnable()==true){
                    iProjectService.openOrClose(id, !enable);
                }else{
                    throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_MAIN_AREA_CLOSED);
                }
            }else {
                iProjectService.openOrClose(id, !enable);
            }
        }

    }

    @ApiOperation(value = "项目分配员工",notes = "项目分配员工")
    @PostMapping("/assignEmployees")
    public void assignEmployees(@RequestBody ReqAssignProjectUserVO reqVO) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        if(currentUserRole != RoleTypeEnum.SUPER_ADMIN &&
                currentUserRole != RoleTypeEnum.MANAGEMENT &&
                currentUserRole != RoleTypeEnum.HR &&
                currentUserRole != RoleTypeEnum.AREA_MANAGER ){
            throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_OPERATE_AUTH_ERROR);
        }

        Integer projectId = reqVO.getProjectId();
        ProjectAdvanceModel projectAdvanceModel = iProjectService.fetchProject(projectId);
        Integer areaId = projectAdvanceModel.getAreaId();
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            if(areaId!=currentUserAreaId){
                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_AREA_BELONG_ERROR);
            }
        }

        List<Integer> userIdList = reqVO.getUserIdList();
        if(CollectionUtils.isEmpty(userIdList)){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_USER_MUST_EXIST_ERROR);
        }

        Set<Integer> userIdSet = new HashSet<>();
        for (Integer userId : userIdList) {
            userIdSet.add(userId);
        }

        Integer managerId = projectAdvanceModel.getManagerId();
        if(!userIdSet.contains(managerId)){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_MANAGER_MUST_EXIST_ERROR);
        }


        if(!CollectionUtils.isEmpty(userIdList)){
            iProjectService.assignEmployees(projectId,userIdList);
        }
    }

    @ApiOperation(value = "删除项目",notes = "删除项目")
    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        if(currentUserRole != RoleTypeEnum.AREA_MANAGER
            && currentUserRole != RoleTypeEnum.SUPER_ADMIN
                && currentUserRole != RoleTypeEnum.MANAGEMENT ){
            throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_OPERATE_AUTH_ERROR);
        }

        ProjectAdvanceModel projectAdvanceModel = iProjectService.fetchProject(id);
        Integer areaId = projectAdvanceModel.getAreaId();
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            if(currentUserAreaId!=areaId){
                throw new BaseAuthException(AuthErrorCodeEnum.PROJECT_AREA_BELONG_ERROR);
            }
        }

        Integer countCheck = iProjectService.checkDayReportByProjectId(id);
        if(countCheck > 0){
            throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_DELETE_DAY_REPORT_EXIST_ERROR);
        }

        List<ProjectUserModel> allProjectUser = iProjectService.getAllProjectUserById(id);
        Integer managerId = projectAdvanceModel.getManagerId();
        for (ProjectUserModel projectUserModel : allProjectUser) {
            if(projectUserModel.getUserId() != managerId ){
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_DELETE_OTHER_USER_EXIST_ERROR);
            }
        }

        iProjectService.deleteProject(id);


    }


    @ApiOperation(value = "查询首页面板项目展示",notes = "查询首页面板项目展示")
    @GetMapping("/board/list")
    public List<ResBoardProjectVO> searchBoardProjectList(@RequestParam("pjNumber") Integer pjNumber) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectAdvanceModel> modelList = iProjectService.searchBoardProjectList(pjNumber);
        List<ResBoardProjectVO> voList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(modelList)){
            for (ProjectAdvanceModel model : modelList) {
                ResBoardProjectVO vo =new ResBoardProjectVO();

                vo.setId(model.getId());
                vo.setName(model.getName());
                vo.setValue(model.getHours());
                voList.add(vo);
            }

        }
        return voList;

    }


    @ApiOperation(value = "查询首页面板项目人员统计",notes = "查询首页面板项目人员统计")
    @GetMapping("/board/projectUserlist")
    public List<ResBoardProjectUserVO> searchBoardProjectUserList() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        List<ProjectUserCountModelParam> projectUserCountModelParams = iProjectService.searchBoardProjectUser();
        List<ResBoardProjectUserVO> voList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(projectUserCountModelParams)){
            for (ProjectUserCountModelParam projectUserCountModelParam : projectUserCountModelParams) {
                ResBoardProjectUserVO vo =new ResBoardProjectUserVO();
                vo.setName(projectUserCountModelParam.getProjectName());
                vo.setValue(new Double(projectUserCountModelParam.getCountUser()));
                voList.add(vo);
            }
        }
        return voList;
    }

    @ApiOperation(value = "项目报表下载",notes = "项目报表下载")
    @PostMapping("/downProjectReportForm")
    public void downProjectReportForm(@RequestBody ReqProjectSearchVO reqProjectSearchVO, HttpServletRequest req, HttpServletResponse res) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();

        StringBuilder areaNameTar = null;
        List<AreaEntity> allAreaByIds = null;
        if(reqProjectSearchVO.getAreaId() != null){
            allAreaByIds = areaRegistry.getAllAreaByIds(new Integer[]{reqProjectSearchVO.getAreaId()});
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(allAreaByIds)){
            areaNameTar = new StringBuilder();
            areaNameTar.append("区域：");
            for(AreaEntity model:allAreaByIds){
                areaNameTar.append(model.getName()).append("、");
            }
            areaNameTar.deleteCharAt(areaNameTar.length() - 1);
        }

        ProjectSearchModelParam modelParam = new ProjectSearchModelParam();
        modelParam.setName(reqProjectSearchVO.getName());
        if(currentUserRole == RoleTypeEnum.AREA_MANAGER || currentUserRole == RoleTypeEnum.GROUP_MANAGER || currentUserRole == RoleTypeEnum.EMPLOYEE){
            if(reqProjectSearchVO.getManagerId()!=null) {
                UserModel managerModel = iUserService.queryUserInfoById(reqProjectSearchVO.getManagerId().longValue());
                if (managerModel.getAreaId() != currentUserAreaId) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_USER_DIFFERENT_AREA_ERROR);
                }
            }
            modelParam.setAreaId(currentUserAreaId);

        }else{
            modelParam.setAreaId(reqProjectSearchVO.getAreaId());
        }
        modelParam.setUserId(reqProjectSearchVO.getUserId());
        modelParam.setPageSize(reqProjectSearchVO.getPageSize());
        modelParam.setCurrentPage(reqProjectSearchVO.getCurrentPage());
        modelParam.setEnableFlag(reqProjectSearchVO.getEnableFlag());
        BasePagesDomain<ProjectAdvanceModel> pmb = iProjectService.searchProject(modelParam);

        List<ProjectAreaModelParam> params = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isEmpty(pmb.getTotalList())) {
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }
        for (ProjectAdvanceModel projectModel : pmb.getTotalList()) {
            ProjectAreaModelParam param = new ProjectAreaModelParam();
            param.setProjectId(projectModel.getId());
            param.setAreaId(projectModel.getAreaId());
            params.add(param);

        }

        List<DownProjectReportFormModelParam> list = iProjectService.queryProjectAreaUsers(params);

        int size = list.size();

        Map<String,Integer> maps = new HashMap<>();
        for (DownProjectReportFormModelParam param : list) {
            String projectName = param.getProjectName();
            if(maps.containsKey(projectName)){
                Integer integer = maps.get(projectName);
                maps.put(projectName,integer+1);
            }else{
                maps.put(projectName,1);
            }
        }


        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        sheet.setColumnWidth(0, 3500);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 3500);
        sheet.setColumnWidth(4, 3500);


        //设置样式
        CellStyle blackStyle = wb.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);

        //设置样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        DecimalFormat strFormat = new DecimalFormat("0.00");
        HSSFFont redHeadFont = wb.createFont();
        //颜色
        redHeadFont.setColor(Font.COLOR_RED);
        //设置字体大小
        redHeadFont.setFontHeightInPoints((short) 10);
        //字体
        //redHeadFont.setFontName("宋体");
        style.setFont(redHeadFont);

        //设置样式
        //设置headStyle样式
        HSSFCellStyle headStyle = wb.createCellStyle();
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
        headStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);//前景填充色
//        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
//        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
//        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);


        //设置bodyStyle样式
        HSSFCellStyle bodyStyle = wb.createCellStyle();
//        bodyStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
//        bodyStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//前景填充色

//        bodyStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
//        bodyStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
//        bodyStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//        bodyStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        bodyStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        bodyStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);


        bodyStyle.setWrapText(true);//设置自动换行

        //冻结窗口设置
        sheet.createFreezePane(1,2,1,2);

        //第一行
        HSSFCell cell = sheet.createRow(0).createCell(0);
        if(areaNameTar != null){
            cell.setCellValue("项目报表统计("+ areaNameTar + ")");
        }else {
            cell.setCellValue("项目报表统计("+ "全区域" + ")");
        }

        cell.setCellStyle(style);


        //设置起止行和列
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 4);
        sheet.addMergedRegion(region);

        //创建2行，并先给第一列赋值，并赋值样式headStyle
        HSSFRow row2 = sheet.createRow(1);
        row2.setHeightInPoints(20);//目的是想把行高设置成20px
        row2.createCell(0).setCellValue("项目");
        row2.createCell(1).setCellValue("区域");
        row2.createCell(2).setCellValue("人员");
        row2.createCell(3).setCellValue("角色");
        row2.createCell(4).setCellValue("人员状态");
        row2.getCell(0).setCellStyle(headStyle);
        row2.getCell(1).setCellStyle(headStyle);
        row2.getCell(2).setCellStyle(headStyle);
        row2.getCell(3).setCellStyle(headStyle);
        row2.getCell(4).setCellStyle(headStyle);

        //行
        int rowNum = 2;
        //列
        int lineNum = 0;
        String projectName = StringUtils.EMPTY;
        String areaName = StringUtils.EMPTY;
        int count = 1;
        int cycleNum = 0;
        int tarCount = 1;
        //int temp = 0;

        //每个项目的大小
        int everyProjectCount = 1;

        for (int i = 0; i < size; i++) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);
            cycleNum ++;
            if(projectName.equals(list.get(i).getProjectName())){
                everyProjectCount ++ ;
                //执列
                lineNum += 1;
                count ++;
                if(cycleNum == size ){
                    CellRangeAddress region5 = new CellRangeAddress(rowNum - count, rowNum - 1, 0, 0);
                    sheet.addMergedRegion(region5);
                }


                if(areaName.equals(list.get(i).getAreaName())){

                //执列
                    lineNum += 1;
                    tarCount ++;

                    if(maps.get(projectName) == everyProjectCount){
                        CellRangeAddress region2 = new CellRangeAddress(rowNum - tarCount, rowNum - 1, 1, 1);
                        sheet.addMergedRegion(region2);
                        everyProjectCount = 1;
                    }
                }else {
                    areaName = list.get(i).getAreaName();
                    row.createCell(lineNum++).setCellValue(list.get(i).getAreaName());
                    row.getCell(lineNum-1).setCellStyle(bodyStyle);
                    if(tarCount > 1){
                        tarCount += 1;
                        CellRangeAddress region2 = new CellRangeAddress(rowNum - tarCount, rowNum - 2, 1, 1);
                        sheet.addMergedRegion(region2);
                    }
                    tarCount = 1;
                }


            }else {
                projectName = list.get(i).getProjectName();
                row.createCell(lineNum++).setCellValue(projectName);
                if("开启".equals(list.get(i).getProjectEnable())){
                    row.getCell(0).setCellStyle(bodyStyle);
                }else {
                    row.getCell(0).setCellStyle(headStyle);
                }
                //row.createCell(lineNum++).setCellValue(projectName);
                //row.getCell(0).setCellStyle(bodyStyle);
                areaName = list.get(i).getAreaName();
                row.createCell(lineNum++).setCellValue(areaName);
                row.getCell(lineNum-1).setCellStyle(bodyStyle);

                if(count > 1) {
                    count += 1;
                    CellRangeAddress region1 = new CellRangeAddress(rowNum - count, rowNum - 2, 0, 0);
                    sheet.addMergedRegion(region1);
                }
                count = 1;
                tarCount = 1;
                everyProjectCount = 1;
            }
//            row.createCell(lineNum++).setCellValue(list.get(i).getAreaName());
//            row.getCell(lineNum-1).setCellStyle(bodyStyle);
            row.createCell(lineNum++).setCellValue(list.get(i).getNickname());
            row.getCell(lineNum-1).setCellStyle(bodyStyle);
            row.createCell(lineNum++).setCellValue(list.get(i).getRoleName());
            row.getCell(lineNum-1).setCellStyle(bodyStyle);
            row.createCell(lineNum++).setCellValue(list.get(i).getUserStatus());
            if("离职".equals(list.get(i).getUserStatus())){
                row.getCell(lineNum-1).setCellStyle(headStyle);
            }else{
                row.getCell(lineNum-1).setCellStyle(bodyStyle);
            }
            lineNum = 0;
        }



        System.out.println("执行完成");
        // 设置response的Header

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);

        String fileName = null;
        if(areaNameTar != null){
            fileName =  "项目报表统计(" + areaNameTar +")";
        }else {
            fileName = "项目报表统计(" + "全区域" +")";
        }

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();
    }

    @ApiOperation(value = "一键解除人与项目间的关系",notes = "一键解除人与项目间的关系")
    @DeleteMapping("/removeAssociated/{userId}/{areaId}")
    public void removeAssociated(@PathVariable("userId") Integer userId,@PathVariable("areaId") Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        if(userId == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.NO_USER);
        }
//        Integer integer2 = userService.queryReportCountByUserId(userId.intValue());
//        if(integer2 != null && integer2 > 0){
//            throw new BaseServiceException(ServiceErrorCodeEnum.USER_EXIST_DAY_REPORT_ASSOCIATED_ERROR);
//        }

        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        if(RoleTypeEnum.AREA_MANAGER == currentUserRole){
            if(!currentUserAreaId.equals(areaId)){
                throw new BaseServiceException(ServiceErrorCodeEnum.USER_REMOVE_ASSOCIATED_ERROR);
            }

        }else if(RoleTypeEnum.MANAGEMENT != currentUserRole && RoleTypeEnum.SUPER_ADMIN != currentUserRole){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }

        userService.delProjectUserByUserIdAndAreaId(userId,areaId);
    }
    @ApiOperation(value = "查询人员关联项目的区域列表",notes = "查询人员关联项目的区域列表")
    @GetMapping("/searchAssociatedArea/{userId}")
    public List<ResAreaVO> searchAssociatedProjectAreaList(@PathVariable("userId") Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        List<AreaModel> areaModels = userService.searchAssociatedProjectAreaList(userId);
        List<ResAreaVO> voList = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(areaModels)){
            for (AreaModel areaModel : areaModels) {
                ResAreaVO vo =new ResAreaVO();
                vo.setId(areaModel.getId());
                vo.setName(areaModel.getName());
                vo.setManagerId(areaModel.getManagerId());
                voList.add(vo);
            }
        }
        return voList;
    }

    @ApiOperation(value = "查询项目分区工时图表",notes = "查询项目分区工时图表")
    @PostMapping("/distributionChart")
    public List<ResProjectAreaHoursVO> searchAssociatedProjectAreaList(@RequestBody ReqDistributionVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        if(RoleTypeEnum.EMPLOYEE == currentUserRole || RoleTypeEnum.HR == currentUserRole){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }

        Integer mainAreaId = reqVO.getMainAreaId();
        if(mainAreaId==null){
            if(RoleTypeEnum.AREA_MANAGER == currentUserRole || RoleTypeEnum.GROUP_MANAGER == currentUserRole){
                mainAreaId = currentUserAreaId;
            }
        }else{
            if(RoleTypeEnum.SUPER_ADMIN != currentUserRole && RoleTypeEnum.FINANCE != currentUserRole && RoleTypeEnum.MANAGEMENT != currentUserRole){
                if(!currentUserAreaId.equals(mainAreaId)){
                    throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
                }
            }
        }
        Integer managerId = null;
        if(RoleTypeEnum.GROUP_MANAGER == currentUserRole){
            managerId = currentUserId;
        }

        List<ProjectAdvanceModel> projectAdvanceModels = iProjectService.searchAssociatedProjectAreaList(mainAreaId, managerId);
        Map<String,List<ResAreaHoursVO>> projectMap = new HashMap<>();
        Map<String,Boolean> projectEnableMap = new HashMap<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(projectAdvanceModels)){
            for (ProjectAdvanceModel projectAdvanceModel : projectAdvanceModels) {
                String projectName = projectAdvanceModel.getName();
                Boolean enable = projectAdvanceModel.getEnable();
                Integer areaId = projectAdvanceModel.getAreaId();
                if(currentUserAreaId.equals(areaId)){
                    projectEnableMap.put(projectName,enable);
                }
                if(projectMap.containsKey(projectName)){
                    List<ResAreaHoursVO> areaHourList = projectMap.get(projectName);
                    ResAreaHoursVO hoursVO = new ResAreaHoursVO();
                    hoursVO.setName(projectAdvanceModel.getAreaName());
                    hoursVO.setValue(projectAdvanceModel.getHours());
                    areaHourList.add(hoursVO);
                    projectMap.put(projectName,areaHourList);
                }else{
                    List<ResAreaHoursVO> areaHourList = new ArrayList<>();
                    ResAreaHoursVO hoursVO = new ResAreaHoursVO();
                    hoursVO.setName(projectAdvanceModel.getAreaName());
                    hoursVO.setValue(projectAdvanceModel.getHours());
                    areaHourList.add(hoursVO);
                    projectMap.put(projectName,areaHourList);
                }
            }
        }

        List<ResProjectAreaHoursVO> resList = projectMap.entrySet().stream().map(e -> new ResProjectAreaHoursVO(e.getKey(), e.getValue(),projectEnableMap.get(e.getKey()))).collect(Collectors.toList());

        return resList;
    }


    @ApiOperation(value = "查询项目人员工时图表",notes = "查询项目人员工时图表")
    @PostMapping("/userHoursChart")
    public List<ResProjectUserHoursVO> searchAssociatedProjectUserHoursList(@RequestBody ReqDistributionVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        if(RoleTypeEnum.EMPLOYEE == currentUserRole || RoleTypeEnum.HR == currentUserRole){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }

        Integer mainAreaId = reqVO.getMainAreaId();
        if(mainAreaId==null){
            if(RoleTypeEnum.AREA_MANAGER == currentUserRole || RoleTypeEnum.GROUP_MANAGER == currentUserRole){
                mainAreaId = currentUserAreaId;
            }
        }else{
            if(RoleTypeEnum.SUPER_ADMIN != currentUserRole && RoleTypeEnum.FINANCE != currentUserRole && RoleTypeEnum.MANAGEMENT != currentUserRole){
                if(!currentUserAreaId.equals(mainAreaId)){
                    throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
                }
            }
        }
        Integer managerId = null;
        if(RoleTypeEnum.GROUP_MANAGER == currentUserRole){
            managerId = currentUserId;
        }

        List<ProjectAdvanceModel> projectAdvanceModels = iProjectService.searchAssociatedProjectUserHoursList(mainAreaId, managerId);
        Map<String,List<ResUserHoursVO>> projectMap = new HashMap<>();
        Map<String,Boolean> projectEnableMap = new HashMap<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(projectAdvanceModels)){
            for (ProjectAdvanceModel projectAdvanceModel : projectAdvanceModels) {
                Double hours = projectAdvanceModel.getHours();
                if(0.0 == hours || hours == null){
                    continue;
                }
                String nickname = projectAdvanceModel.getNickname();
                if(nickname == null){
                    continue;
                }
                String projectName = projectAdvanceModel.getName();
                Boolean enable = projectAdvanceModel.getEnable();
                Integer areaId = projectAdvanceModel.getAreaId();
                if(currentUserAreaId.equals(areaId)){
                    projectEnableMap.put(projectName,enable);
                }
                if(projectMap.containsKey(projectName)){
                    List<ResUserHoursVO> userHourList = projectMap.get(projectName);
                    ResUserHoursVO hoursVO = new ResUserHoursVO();
                    hoursVO.setName(nickname+"-"+projectAdvanceModel.getAreaName());
                    hoursVO.setValue(projectAdvanceModel.getHours());
                    userHourList.add(hoursVO);
                    projectMap.put(projectName,userHourList);
                }else{
                    List<ResUserHoursVO> userHourList = new ArrayList<>();
                    ResUserHoursVO hoursVO = new ResUserHoursVO();
                    hoursVO.setName(nickname+"-"+projectAdvanceModel.getAreaName());
                    hoursVO.setValue(projectAdvanceModel.getHours());
                    userHourList.add(hoursVO);
                    projectMap.put(projectName,userHourList);
                }
            }
        }

        List<ResProjectUserHoursVO> resList = projectMap.entrySet().stream().map(e -> new ResProjectUserHoursVO(e.getKey(), e.getValue(),projectEnableMap.get(e.getKey()))).collect(Collectors.toList());

        return resList;
    }
}
