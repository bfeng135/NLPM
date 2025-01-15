package com.nl.pm.server.common.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RoleTypeEnum {
    SUPER_ADMIN("SUPER_ADMIN","超级管理员"),
    AREA_MANAGER("AREA_MANAGER","区长"),
    GROUP_MANAGER("GROUP_MANAGER","组长"),
    EMPLOYEE("EMPLOYEE","普通员工"),
    FINANCE("FINANCE","财政"),
    HR("HR","人事"),
    MANAGEMENT("MANAGEMENT","行政"),
    ;
    private String code;
    private String desc;

    RoleTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Boolean checkRole(String current,String add){
        //超级管理员
        List<String> superList = Arrays.asList("SUPER_ADMIN","AREA_MANAGER","GROUP_MANAGER","EMPLOYEE","FINANCE","HR","MANAGEMENT");
        //行政
        List<String> managerList = Arrays.asList("AREA_MANAGER","GROUP_MANAGER","EMPLOYEE");
        //人事
        List<String> hrList = Arrays.asList("AREA_MANAGER","GROUP_MANAGER","EMPLOYEE");
        //财务
        List<String> financeList = Arrays.asList("AREA_MANAGER","GROUP_MANAGER","EMPLOYEE");
        //区长
        List<String> areaManagerList = Arrays.asList("GROUP_MANAGER","EMPLOYEE");
        if("SUPER_ADMIN".equals(current)){
            if(superList.contains(add)){
                return true;
            }
        }
        if("MANAGEMENT".equals(current)){
            if(managerList.contains(add)){
                return true;
            }
        }
        if("HR".equals(current)){
            if(hrList.contains(add)){
                return true;
            }
        }
        if("FINANCE".equals(current)){
            if(financeList.contains(add)){
                return true;
            }
        }
        if("AREA_MANAGER".equals(current)){
            if(areaManagerList.contains(add)){
                return true;
            }
        }
        return false;
    }

    public static Boolean checkEditSystemVariable(String str){
        Boolean flg = false;
        List<String> SystemVariableManagerList = Arrays.asList("SUPER_ADMIN","FINANCE","HR","MANAGEMENT");
        if(SystemVariableManagerList.contains(str)){
            return true;

        }
        return flg;
    }
    public static Boolean checkAreaManager(String str){
        Boolean flg = false;
        if(AREA_MANAGER.getCode().equals(str)){
            flg = true;
        }
        return flg;
    }

    public static Boolean checkGroupManager(String str){
        Boolean flg = false;
        if(GROUP_MANAGER.getCode().equals(str)){
            flg = true;
        }
        return flg;
    }

    public static RoleTypeEnum convertToEnum(String code){
        for(RoleTypeEnum e : RoleTypeEnum.values()){
            if(e.getCode().equals(code)){
                return e;
            }
        }
        return null;
    }

    public static List<String> convertToString(List<RoleTypeEnum> roleTypeEnums){
        List<String> list = new ArrayList<>();
        if(roleTypeEnums.size() > 0){
            for (RoleTypeEnum typeEnum:roleTypeEnums){
                for (RoleTypeEnum e : RoleTypeEnum.values()){
                    if(e.getCode().equals(typeEnum.getCode())){
                        list.add(typeEnum.getCode());
                    }
                }
            }
        }
        return list;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
