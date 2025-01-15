package com.nl.pm.server.common.enums;

import java.util.ArrayList;
import java.util.List;
public enum CostTypeEnum {
    MY_MAIN_COST("MY_MAIN_COST","本区主要负责的项目花费--本区+外区总消耗合计"),
    MY_EARN("MY_EARN","帮助其他区做的--进项收入"),
    OTHER_COST("OTHER_COST","其他区帮我做的--外区消耗");

    private String code;
    private String desc;

    CostTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<String> returnList(){
        List<String> tempList = new ArrayList<>();
        for(CostTypeEnum e : CostTypeEnum.values()){
            tempList.add(e.getCode());
        }
        return tempList;
    }

    public static CostTypeEnum convertToEnum(String code){
        for(CostTypeEnum e : CostTypeEnum.values()){
            if(e.getCode().equals(code)){
                return e;
            }
        }
        return null;
    }

    public static List<String> convertToStringList(List<CostTypeEnum> costTypeEnums){
        List<String> list = new ArrayList<>();
        if(costTypeEnums.size() > 0){
            for (CostTypeEnum typeEnum:costTypeEnums){
                for (CostTypeEnum e : CostTypeEnum.values()){
                    if(e.getCode().equals(typeEnum.getCode())){
                        list.add(typeEnum.getCode());
                    }
                }
            }
        }
        return list;
    }

    public static String convertToString(CostTypeEnum costTypeEnum){
        if(costTypeEnum!=null){
            for (CostTypeEnum e : CostTypeEnum.values()){
                if(e.getCode().equals(costTypeEnum.getCode())){
                    return costTypeEnum.getCode();
                }
            }
        }
        return null;
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
