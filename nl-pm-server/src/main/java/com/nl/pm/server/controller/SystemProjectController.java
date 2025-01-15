package com.nl.pm.server.controller;

import com.google.gson.Gson;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IAreaRegistry;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.ISystemJob;
import com.nl.pm.server.service.ISystemProjectService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.ProjectModel;
import com.nl.pm.server.service.model.SystemProjectModel;
import com.nl.pm.server.service.model.SystemStageModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.QuerySystemProjectPagingModelParam;
import com.nl.pm.server.service.param.RpaSystemProjectModelParam;
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

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.*;


/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:55
 */
@RestController
@Api(value = "项目模板管理", tags = {"项目模板管理"})
@RequestMapping("/systemProject")
public class SystemProjectController {
    @Autowired
    private ISystemJob iSystemJob;
    @Autowired
    private IUserService userService;
    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private ISystemProjectService systemProjectService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IAreaRegistry areaRegistry;

    private static Integer projectNum = 1;


    @ApiOperation(value = "系统项目分页查询", notes = "系统项目分页查询接口")
    @PostMapping("/list")
    public BasePagesDomain<ResSystemProjectVO> querySystemProjectPaging(@RequestBody ReqSystemProjectPagingVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        QuerySystemProjectPagingModelParam param = new QuerySystemProjectPagingModelParam();
        if (vo.getCurrentPage() != 0) {
            param.setCurrentPage(vo.getCurrentPage());
        }
        if (vo.getPageSize() != 0) {
            param.setPageSize(vo.getPageSize());
        }
        param.setSystemProjectName(vo.getSystemProjectName());
        param.setSearchVal(vo.getSearchVal());
        param.setCrmProjectId(vo.getCrmProjectId());
        param.setCrmStageId(vo.getCrmStageId());
        param.setAreaId(vo.getAreaId());
        param.setEnable(vo.getEnable());
        BasePagesDomain<SystemProjectModel> projectModelBasePagesDomain = systemProjectService.querySystemProjectPaging(param);
        return toBasePagesDomain(param.getCurrentPage(), param.getPageSize(), projectModelBasePagesDomain);
    }


    @ApiOperation(value = "系统项目查询", notes = "系统项目查询接口")
    @GetMapping("/findAllSystemProject")
    public List<ResSystemProjectVO> findAllSystemProject() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
//        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
//        if (RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
//                || RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
        List<ResSystemProjectVO> vos = new ArrayList<>();
        List<SystemProjectModel> allProjectTemplate = systemProjectService.findAllProjectTemplate();
        if (!CollectionUtils.isEmpty(allProjectTemplate)) {
            for (SystemProjectModel model : allProjectTemplate) {
                ResSystemProjectVO vo = new ResSystemProjectVO();
                vo.setId(model.getId());
                vo.setName(model.getName());
                vos.add(vo);
            }
        }
        return vos;
//        }
//        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    /**
     * 禁止管理员等用户对本系统的增删改查权限
     * 修改为  rpa  操作
     *
     * @param vo
     * @throws Exception
     */
    //注释开始
    @ApiOperation(value = "添加项目", notes = "添加项目接口")
    @PostMapping("/addSystemProject")
    public void addSystemProject(@RequestBody ReqSystemProjectVO vo) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if (RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            if (StringUtils.isEmpty(vo.getName())) {
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
            }
            if (vo.getName().indexOf(" ") == 0 || vo.getName().indexOf(" ") == (vo.getName().length()-1)) {
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_INPUT_NAME_EXIST_SPACE);
            }
            Integer integer = systemProjectService.queryProjectTemplateCountByName(vo.getName());
            if (integer != null && integer != 0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_TEMPLATE_EXIST);
            }

            SystemProjectModel model = new SystemProjectModel();
            model.setName(vo.getName());
            //model.setAreaId(vo.getAreaId());
//            model.setEnable(true);
            systemProjectService.addProjectTemplate(model);
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    @ApiOperation(value = "修改项目", notes = "修改项目接口")
    @PutMapping("/editSystemProject")
    public void editSystemProject(@RequestBody ReqSystemProjectVO vo) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if (RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            if (StringUtils.isEmpty(vo.getName()) || vo.getId() == null) {
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
            }
            if (vo.getName().indexOf(" ") == 0 || vo.getName().indexOf(" ") == (vo.getName().length()-1)) {
                throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_INPUT_NAME_EXIST_SPACE);
            }
//            SystemProjectModel systemProjectModel = systemProjectService.queryProjectById(vo.getId());
//            if (!systemProjectModel.getName().equals(vo.getName())) {
//                Integer integer = projectService.queryProjectCountBySystemId(vo.getId());
//                if (integer != null && integer > 0) {
//                    throw new BaseServiceException(ServiceErrorCodeEnum.ENTITY_EXITS_BAN_OPERATE);
//                }
//            }

            Integer integer = systemProjectService.queryProjectTemplateCountByName(vo.getName());
            if (integer != null && integer != 0) {
                SystemProjectModel systemProjectModel = systemProjectService.queryProjectById(vo.getId());
                if(!systemProjectModel.getName().equalsIgnoreCase(vo.getName())){
                    throw new BaseServiceException(ServiceErrorCodeEnum.PROJECT_TEMPLATE_EXIST);
                }

            }
            SystemProjectModel model = new SystemProjectModel();
            model.setId(vo.getId());
            //model.setAreaId(vo.getAreaId());
            model.setName(vo.getName());
            systemProjectService.editProjectTemplate(model);
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    @ApiOperation(value = "变更项目强制日报描述标识", notes = "变更项目强制日报描述标识")
    @PutMapping("/forceDescFlag/change/{id}")
    public void changeForceDescFlag(@PathVariable Integer id) throws Exception {
        SystemProjectModel systemProjectModel = systemProjectService.queryProjectById(id);
        if(systemProjectModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.DEFAULT_SYSTEM_PROJECT_FORCE_DESC_ERROR);
        }
        Integer areaId = systemProjectModel.getAreaId();
        if(null != areaId) {
            Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
            RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
            if (areaId.equals(currentUserAreaId) || RoleTypeEnum.SUPER_ADMIN == currentUserRole ||
                    RoleTypeEnum.MANAGEMENT == currentUserRole || RoleTypeEnum.FINANCE == currentUserRole) {
                if (RoleTypeEnum.AREA_MANAGER == currentUserRole || RoleTypeEnum.SUPER_ADMIN == currentUserRole
                        || RoleTypeEnum.MANAGEMENT == currentUserRole || RoleTypeEnum.FINANCE == currentUserRole) {
                    systemProjectService.changeForceDescFlag(id);
                } else if (RoleTypeEnum.GROUP_MANAGER == currentUserRole) {
                    List<ProjectModel> pList = projectService.searchProjectBySystemProjectId(id);
                    boolean flag = false;
                    Integer currentUserId = securityContextUtils.getCurrentUserId();
                    for (ProjectModel model : pList) {
                        Integer managerId = model.getManagerId();
                        if (currentUserId.equals(managerId)) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        systemProjectService.changeForceDescFlag(id);
                    } else {
                        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
                    }
                } else {
                    throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
                }
            } else {
                throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
            }
        }else{
            throw new BaseServiceException(ServiceErrorCodeEnum.SYSTEM_PROJECT_MAIN_AREA_NULL_FORCE_ERROR);
        }
    }

    @ApiOperation(value = "删除项目", notes = "删除项目接口")
    @DeleteMapping("/delSystemProject")
    public void delSystemProject(@RequestBody ReqSystemProjectVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if (RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            if (vo.getId() == null) {
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
            }
            Integer integer = projectService.queryProjectCountBySystemId(vo.getId());
            if (integer != null && integer > 0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.ENTITY_EXITS_BAN_OPERATE);
            }
            systemProjectService.delProjectTemplate(vo.getId());
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    //注释
    @ApiOperation(value = "项目字典下载", notes = "项目字典下载")
    @PostMapping("/downSystemProject")
    public void downSystemProject(@RequestBody ReqSystemProjectPagingVO vo, HttpServletRequest req, HttpServletResponse res) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if (RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode()) ||
                RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())) {
            QuerySystemProjectPagingModelParam param = new QuerySystemProjectPagingModelParam();
            if (vo.getCurrentPage() != 0) {
                param.setCurrentPage(vo.getCurrentPage());
            }
            if (vo.getPageSize() != 0) {
                param.setPageSize(vo.getPageSize());
            }
            param.setSystemProjectName(vo.getSystemProjectName());
            param.setSearchVal(vo.getSearchVal());
            param.setCrmProjectId(vo.getCrmProjectId());
            param.setCrmStageId(vo.getCrmStageId());
            param.setAreaId(vo.getAreaId());
            param.setEnable(vo.getEnable());
            BasePagesDomain<SystemProjectModel> projectModelBasePagesDomain = systemProjectService.querySystemProjectPaging(param);
            BasePagesDomain<ResSystemProjectVO> resSystemProjectVOBasePagesDomain = toBasePagesDomain(param.getCurrentPage(), param.getPageSize(), projectModelBasePagesDomain);
            List<ResSystemProjectVO> totalList = resSystemProjectVOBasePagesDomain.getTotalList();
            if (org.apache.commons.collections.CollectionUtils.isEmpty(totalList)) {
                throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
            }
            projectNum = 1;
            List<ResDownSystemProjectVO> list = new ArrayList<>();
            for (ResSystemProjectVO resVO : totalList) {
                ResDownSystemProjectVO result = new ResDownSystemProjectVO();
                result.setNum(projectNum++);
                result.setProjectName(resVO.getName());
                result.setEnable(resVO.getEnable());
                result.setCreateTime(resVO.getCreateTime());
                result.setUpdateTime(resVO.getUpdateTime());
                //result.setAreaId(resVO.getAreaId());
                result.setAreaName(resVO.getAreaName());
                result.setCrmProjectId(resVO.getCrmProjectId());
                result.setCrmStageId(resVO.getCrmStageId());
                result.setCrmStageName(resVO.getCrmStageName());
                list.add(result);

            }
            StringBuilder areaNameTar = null;
            List<AreaEntity> allAreaByIds = null;
            if(vo.getAreaId() != null){
                allAreaByIds = areaRegistry.getAllAreaByIds(new Integer[]{vo.getAreaId()});
            }
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(allAreaByIds)){
                areaNameTar = new StringBuilder();
                areaNameTar.append("区域：");
                for(AreaEntity model:allAreaByIds){
                    areaNameTar.append(model.getName()).append("、");
                }
                areaNameTar.deleteCharAt(areaNameTar.length() - 1);
            }

            //创建HSSFWorkbook对象
            HSSFWorkbook wb = new HSSFWorkbook();
            //创建HSSFSheet对象
            HSSFSheet sheet = wb.createSheet("sheet0");

            sheet.setColumnWidth(0, 4500);
            sheet.setColumnWidth(1, 10000);
            sheet.setColumnWidth(2, 4500);
            sheet.setColumnWidth(3, 4500);
            sheet.setColumnWidth(4, 4500);
            sheet.setColumnWidth(5, 4500);
            sheet.setColumnWidth(6, 4500);
            sheet.setColumnWidth(7, 4500);
            sheet.setColumnWidth(8, 4500);

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
            headStyle.setWrapText(true);


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

            HSSFCellStyle otherStyle = wb.createCellStyle();
            otherStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
            otherStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);//前景填充色
            otherStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
            otherStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);


            //冻结窗口设置
            sheet.createFreezePane(1, 2, 1, 2);

            //第一行
            HSSFCell cell = sheet.createRow(0).createCell(0);
            if(areaNameTar != null){
                cell.setCellValue("项目字典管理(" + areaNameTar + ")");
            }else {
                cell.setCellValue("项目字典管理(全区域)");
            }

            cell.setCellStyle(style);

            //设置起止行和列
            CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);
            sheet.addMergedRegion(region);

            //创建2行，并先给第一列赋值，并赋值样式headStyle
            HSSFRow row2 = sheet.createRow(1);
            row2.setHeightInPoints(20);//目的是想把行高设置成20px
            row2.createCell(0).setCellValue("序号");
            row2.createCell(1).setCellValue("项目名称");
            row2.createCell(2).setCellValue("CRM项目ID");
            row2.createCell(3).setCellValue("CRM项目阶段");
            row2.createCell(4).setCellValue("CRM项目阶段ID");
            row2.createCell(5).setCellValue("各大区是否可见");
            row2.createCell(6).setCellValue("主要责任区");
            row2.createCell(7).setCellValue("创建时间");
            row2.createCell(8).setCellValue("更新时间");
            row2.getCell(0).setCellStyle(headStyle);
            row2.getCell(1).setCellStyle(headStyle);
            row2.getCell(2).setCellStyle(headStyle);
            row2.getCell(3).setCellStyle(headStyle);
            row2.getCell(4).setCellStyle(headStyle);
            row2.getCell(5).setCellStyle(headStyle);
            row2.getCell(6).setCellStyle(headStyle);
            row2.getCell(7).setCellStyle(headStyle);
            row2.getCell(8).setCellStyle(headStyle);

            //行
            int rowNum = 2;
            //列
            int lineNum = 0;

            for (int i = 0; i < list.size(); i++) {
                HSSFRow row = sheet.createRow(rowNum++);
                row.setHeightInPoints(20);

                row.createCell(lineNum++).setCellValue(list.get(i).getNum());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);

                row.createCell(lineNum++).setCellValue(list.get(i).getProjectName());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);

                row.createCell(lineNum++).setCellValue(list.get(i).getCrmProjectId());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);

                row.createCell(lineNum++).setCellValue(list.get(i).getCrmStageName());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);

                row.createCell(lineNum++).setCellValue(list.get(i).getCrmStageId());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);

                row.createCell(lineNum++).setCellValue(list.get(i).getEnable());
                if ("是".equals(list.get(i).getEnable())) {
                    row.getCell(lineNum - 1).setCellStyle(bodyStyle);
                } else {
                    row.getCell(lineNum - 1).setCellStyle(otherStyle);
                }

                row.createCell(lineNum++).setCellValue(list.get(i).getAreaName());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);

                row.createCell(lineNum++).setCellValue(list.get(i).getCreateTime());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);
                row.createCell(lineNum++).setCellValue(list.get(i).getUpdateTime());
                row.getCell(lineNum - 1).setCellStyle(bodyStyle);
                lineNum = 0;
            }


            System.out.println("执行完成");
            // 设置response的Header

            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            wb.write(outByteStream);

            String fileName = null;
            if(areaNameTar != null){
                fileName = "项目字典管理("+ areaNameTar +")";
            }else{
                fileName = "项目字典管理(全区域)";
            }

            res.setHeader("content-type", "application/octet-stream");
            res.setContentType("application/octet-stream");
            res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream outputStream = res.getOutputStream();
            // 将文件输出
            wb.write(outputStream);
            outputStream.close();
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    private BasePagesDomain<ResSystemProjectVO> toBasePagesDomain(Integer pageNo, Integer pageSize, BasePagesDomain<SystemProjectModel> projectModelBasePagesDomain) {
        List<ResSystemProjectVO> systemProjectVOS = new ArrayList<>();
        if (projectModelBasePagesDomain.getTotalList().size() > 0) {
            for (SystemProjectModel systemProjectModel : projectModelBasePagesDomain.getTotalList()) {
                ResSystemProjectVO vo = new ResSystemProjectVO();
                vo.setId(systemProjectModel.getId());
                vo.setName(systemProjectModel.getName());
                vo.setEnable(systemProjectModel.getEnable());
                vo.setCreateTime(DateUtils.convertDateToLong(systemProjectModel.getCreateTime()));
                vo.setUpdateTime(DateUtils.convertDateToLong(systemProjectModel.getUpdateTime()));
                vo.setAreaId(systemProjectModel.getAreaId());
                vo.setAreaName(systemProjectModel.getAreaName());
                vo.setCrmProjectId(systemProjectModel.getCrmProjectId());
                vo.setCrmStageId(systemProjectModel.getCrmStageId());
                vo.setCrmStageName(systemProjectModel.getCrmStageName());
                vo.setForceDescFlag(systemProjectModel.getForceDescFlag());
                vo.setGoalHours(systemProjectModel.getGoalHours());
                systemProjectVOS.add(vo);
            }
        }
        BasePagesDomain<ResSystemProjectVO> pageInfo = new BasePagesDomain(pageNo, pageSize, projectModelBasePagesDomain.getTotalPage(), projectModelBasePagesDomain.getTotal());
        pageInfo.setTotalList(systemProjectVOS);
        return pageInfo;
    }

    /**
     * rpa不能单项目推送
     *
     * @ApiOperation(value = "添加项目", notes = "添加项目接口")
     * @PostMapping("/addSystemProject") public void addSystemProject(@RequestBody ReqRpaSystemProjectVO vo) throws Exception {
     * UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
     * if ("rpa".equals(currentUserModel.getUsername())){
     * if(vo.getName() == null || vo.getCrmProjectId() == null ){
     * throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
     * }
     * if(vo.getName().indexOf(" ")!=-1 || vo.getCrmProjectId().indexOf(" ")!=-1 ||
     * vo.getName().length() == 0 || vo.getCrmProjectId().length() == 0){
     * throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
     * }
     * SystemProjectModel model = new SystemProjectModel();
     * model.setName(vo.getName());
     * model.setCrmProjectId(vo.getCrmProjectId());
     * model.setCrmStageId(vo.getCrmStageId());
     * model.setCrmStageName(vo.getCrmStageName());
     * model.setEnable(true);
     * systemProjectService.addProjectTemplate(model);
     * <p>
     * return;
     * }
     * throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
     * }
     * @ApiOperation(value = "修改项目", notes = "修改项目接口")
     * @PutMapping("/editSystemProject") public void editSystemProject(@RequestBody ReqRpaSystemProjectVO vo) throws Exception {
     * UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
     * if ("rpa".equals(currentUserModel.getUsername())){
     * if(vo.getName() == null || vo.getCrmProjectId() == null ){
     * throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
     * }
     * if(vo.getName().indexOf(" ")!=-1 || vo.getCrmProjectId().indexOf(" ")!=-1 ||
     * vo.getName().length() == 0 || vo.getCrmProjectId().length() == 0){
     * throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
     * }
     * SystemProjectModel model = new SystemProjectModel();
     * model.setName(vo.getName());
     * model.setCrmProjectId(vo.getCrmProjectId());
     * model.setCrmStageId(vo.getCrmStageId());
     * model.setCrmStageName(vo.getCrmStageName());
     * systemProjectService.editProjectTemplateByCrmId(model);
     * <p>
     * return;
     * }
     * throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
     * }
     **/

    @ApiOperation(value = "项目分配区域", notes = "项目分配区域接口")
    @PostMapping("/systemProjectDistributionArea")
    private void systemProjectDistributionArea(@RequestBody ReqSystemProjectDistributionAreaVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
//        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if (RoleTypeEnum.SUPER_ADMIN == currentUserRole||
                RoleTypeEnum.MANAGEMENT == currentUserRole || RoleTypeEnum.FINANCE == currentUserRole) {
            if (vo.getId() == null) {
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
            }
            systemProjectService.updateProjectTemplate(vo.getId(), vo.getAreaId());
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }

    @ApiOperation(value = "同步项目字典", notes = "同步项目字典")
    @GetMapping("/sync")
    public ResSyncSystemProjectVO syncSystemProject() throws BaseAuthException, BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, DynamicServiceException, MessagingException, GeneralSecurityException {
        ResSyncSystemProjectVO resSyncSystemProjectVO = iSystemJob.syncSystemProjectFromCrmAndSendEmail();
        return resSyncSystemProjectVO;
    }

    @ApiOperation(value = "获取rpa项目阶段状态", notes = "获取rpa项目阶段状态接口")
    @GetMapping("/getAllStage")
    public List<ResCrmStageVO> getAllStage() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ResCrmStageVO> vo = new ArrayList<>();
        List<SystemStageModel> systemProjectModels = systemProjectService.distinctGetAllStage();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(systemProjectModels)) {
            for (SystemStageModel model : systemProjectModels) {
                ResCrmStageVO v = new ResCrmStageVO();
                v.setCrmStageId(model.getCrmStageId());
                v.setCrmStageName(model.getCrmStageName());
                vo.add(v);
            }
        }
        return vo;
    }


    @ApiOperation(value = "修改项目完结目标小时数", notes = "修改项目完结目标小时数")
    @PutMapping("/editGoalHours")
    public void editGoalHours(@RequestBody ReqSystemProjectGoalHoursVO vo) throws Exception {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if(RoleTypeEnum.SUPER_ADMIN != currentUserRole && RoleTypeEnum.FINANCE != currentUserRole){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }else{
            Integer id = vo.getId();
            Double goalHours = vo.getGoalHours();
            if(null == id){
                throw new BaseServiceException(ServiceErrorCodeEnum.SYSTEM_PROJECT_ID_NULL_ERROR);
            }
            systemProjectService.editGoalHours(id,goalHours);


        }

    }
}
