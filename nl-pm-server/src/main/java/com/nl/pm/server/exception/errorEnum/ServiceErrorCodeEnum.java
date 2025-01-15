package com.nl.pm.server.exception.errorEnum;

public enum ServiceErrorCodeEnum {

    //代码相关  从 500-0001 开始
    MODEL_ENTITY_ERROR(500,"500-1002","Model和Entity转换异常！"),

    //用户管理相关  从 500-1001 开始
    NO_USER(500,"500-1001","未找到用户！"),
    USER_NOT_EXIST(500,"500-1002","用户不存在"),
    USER_NOT_DELETE(500,"500-1003","用户存在日报或已分配项目等其他数据禁止删除"),
    USER_BAN_OPERATE(500,"500-1004","该用户禁止操作"),
    USER_EXISTING(500,"500-1005","该用户名已存在"),
    USER_CAN_USE(500,"500-1006","该用户可以正常使用"),
    USER_PASSWORD_ERROE(500,"500-1007","输入的密码不符合规范"),
    USER_DEL_EXITS_AREA_PROJECT_MANAGER(500,"500-1008","该离职的用户存为区域负责人或项目负责人，请修改区域负责人或项目负责人后删除！"),
    USER_EXIST_DAY_REPORT_DEL_ERROR(500,"500-1009","该用户存在日报禁止删除"),
    USER_EXIST_DAY_REPORT_ASSOCIATED_ERROR(500,"500-1010","该用户存在日报禁止解除"),
    USER_REMOVE_ASSOCIATED_ERROR(500,"500-1011","您无权限解除其他区域的项目关联"),

    //区域管理相关  从 500-2001 开始
    AREAID_NULL_ERROR(500,"500-2001","区域ID不可为空！"),
    AREA_MANAGERID_ERROR(500,"500-2002","区域负责人必须是区长才可以入选"),
    AREAMANAGERID_NULL_ERROR(500,"500-2003","区域负责人ID不可为空！"),
    AREA_DELETE_USER_EXIST_ERROR(500,"500-2004","当前区域下存在其他员工，不可删除！"),
    AREA_DELETE_OTHER_PROJECT_EXIST_ERROR(500,"500-2005","当前区域下存在其他项目，不可删除！"),
    AREA_DELETE_DAY_REPORT_EXIST_ERROR(500,"500-2006","当前区域下存在日报，不可删除！"),
    AREA_NULL_ERROR(500,"500-2007","该区域不存在！"),
    AREA_NAME_DUMPLII_ERROR(500,"500-2008","区域名重复！"),
    AREA_DESC_LENGTH_ERROR(500,"500-2009","你输入的区域描述文字过多，请修改后重新保存！"),
    AREA_EXITS_MANAGER_ERROR(500,"500-2010","当前区域下存在区长，请修改后重新保存！"),
    AREA_USER_NOT_CONTAIN_ERROR(500,"500-2011","当前用户不属于当前区域！"),
    AREANAME_NULL_ERROR(500,"500-2012","请先选择区域！"),
    AREA_OPERATE_ERROR(500,"500-2013","该区域不可操作！"),
    AREA_STATUS_NULL_ERROR(500,"500-2014","该区域状态不可为空！"),

    //项目管理相关  从 500-3001 开始
    PROJECT_USER_DIFFERENT_AREA_ERROR(500,"500-3001","项目和用户必须属于同一区域"),
    PROJECT_NAME_NULL_ERROR(500,"500-3002","项目名不可为空"),
    PROJECT_AREA_NULL_ERROR(500,"500-3003","项目所属区域不可为空"),
    PROJECT_MANAGER_NULL_ERROR(500,"500-3004","项目负责人不可为空"),
    PROJECT_NAME_EXISTED_ERROR(500,"500-3005","该区域下已经存在相同项目名"),
    PROJECT_MANAGER_ROLE_ERROR(500,"500-3006","项目负责人必须为区长或组长"),
    PROJECT_NULL_ERROR(500,"500-3007","当前项目不存在"),
    PROJECT_MANAGER_MUST_EXIST_ERROR(500,"500-3008","项目负责人必须在项目成员列表中！区域初始项目的责任人默认为【区域负责人】,若未配置区域负责人，请先进入【区域管理】配置区域负责人"),
    PROJECT_USER_MUST_EXIST_ERROR(500,"500-3009","项目成员不能为空"),
    PROJECT_USER_AREA_DIFFERENT_ERROR(500,"500-3010","项目成员所属区域必须和项目所属区域相同"),
    PROJECT_AREA_ERROR(500,"500-3011","项目所属区域错误"),
    PROJECT_DELETE_DAY_REPORT_EXIST_ERROR(500,"500-3012","不能删除存在日报的项目"),
    PROJECT_DELETE_OTHER_USER_EXIST_ERROR(500,"500-3013","该项目仍存在除项目负责人外的其他员工，不能删除"),
    PROJECT_INPUT_NAME_EXIST_SPACE(500,"500-30014","你输入的项目名两端存在空格，请修改后重新保存"),
    PROJECT_MAIN_AREA_CLOSED(500,"500-30015","该项目主负责区已关闭，请联系对应区长！"),

    //日报管理相关  从 500-4001 开始
    DAY_REPORT_FUTURE_ERROR(500,"500-4001","只能提前记录请假8小时的日报，如有工作任务请当天记录！"),
    DAY_REPORT_EXIST_ERROR(500,"500-4002","当前日期日报已经存在，不能重复录入"),
    DAY_REPORT_NOT_EXIST_ERROR(500,"500-4003","日报不存在"),
    DAY_REPORT_PROJECT_EXIST_ERROR(500,"500-4004","同一日报下不能多次出现同一项目"),
    DAY_REPORT_DATE_NULL_ERROR(500,"500-4005","日报时间不可为空"),
    DAY_REPORT_ID_NULL_ERROR(500,"500-4006","日报ID不可为空"),
    DAY_REPORT_LEAVE_HOUR_8_ERROR(500,"500-4007","当天请假时间不能超过 8 小时"),
    DAY_REPORT_ALL_HOUR_24_ERROR(500,"500-4008","当天请假和工作时间总和不能超过 24 小时"),
    DAY_REPORT_ALL_HOUR_8_ERROR(500,"500-4009","当天请假和工作时间总和不能小于 8 小时"),
    DAY_REPORT_OVER_31_DAY_ERROR(500,"500-4010","时间段不能超过 31 天"),
    DAY_REPORT_START_END_DATE_NULL_ERROR(500,"500-4011","开始或结束时间不可为空"),
    DAY_REPORT_PROJECT_REPEAT_ERROR(500,"500-4012","一天的日报中，同一项目不能记录多次"),
    DAY_REPORT_WORK_HOUR_NOT_NULL_ERROR(500,"500-4013","一天的日报中，工作时长不可为空"),
    DAY_REPORT_PROJECT_ID_NULL_ERROR(500,"500-4014","项目不可为空"),
    DAY_REPORT_PROJECT_TIME_NULL_ERROR(500,"500-4015","项目时长不可为空"),
    DAY_REPORT_HOLIDAY_LEAVE_ERROR(500,"500-4016","当前日期为假期，不能请假！"),
    DAY_REPORT_DAY_OVER_ERROR(500,"500-4017","不能查询将来的日报"),
    DAY_REPORT_OTHER_NULL_ERROR(500,"500-4018","公司其他活动必须录入项目描述信息"),
    DAY_REPORT_DRAFT_NULL_ERROR(500,"500-4019","输入的草稿为空，无法进行保存，请修改后进行保存"),
    DAY_REPORT_LEAVE_HOUR_NULL_ERROR(500,"500-4020","请假时间不能为空"),
    DAY_REPORT_HOLIDAY_WORK_HOUR_NULL_ERROR(500,"500-4021","节假日工作必须有工作时长"),
    DAY_REPORT_OLD_NULL_ERROR(500,"500-4022","您存在未填写的日报，提交失败！请先将当前内容存至【草稿箱】，然后到主页查看未填写日报日期并点击图标【补】进行补填！"),
    DAY_REPORT_NOT_MODIFIED(500,"500-4023","该日报不能进行修改"),
    USER_OPERATE_AREA_ERROR(500,"401-1005","用户无操作此区域权限"),

    //系统管理相关  从 500-5001 开始
    INPUT_INFORMATION_ERROR(500,"500-5001","输入内容部分为空"),
    EMAIL_LENGTH_ERROR(500,"500-5002","输入的邮箱有误"),
    EMAIL_CONFIG_EXIST(500,"500-5003","配置的邮箱数据已存在，请勿重新配置"),
    SYSTEM_INFO_EXIST(500,"500-5004","配置的系统信息已存在"),
    SYSTEM_INFO_NAME_ERROR(500,"500-5005","配置的系统名称不能为空,请修改后重新提交"),
    SYSTEM_PROJECT_MAIN_AREA_NULL_FORCE_ERROR(500,"500-5006","该项目没有配置主要责任区，不能强制"),
    DEFAULT_SYSTEM_PROJECT_FORCE_DESC_ERROR(500,"500-5007","当前项目为系统内部项目，不能修改"),
    SYSTEM_PROJECT_ID_NULL_ERROR(500,"500-5008","系统项目不可为空"),

    //假日管理相关  从 500-6001 开始
    HOLIDAY_DEL_ERROR(500,"500-6001","删除的假期不存在，请重新选择！"),



    //角色管理相关  从 500-7001 开始
    ROLEID_NULL_ERROR(500,"500-7001","角色ID不可为空！"),



    //调休管理相关  从 500-9001 开始
    EXCHANGE_AREA_ERROR(500,"500-9001","您无权查看其他区域人员调休！"),
    EXCHANGE_OTHER_ERROR(500,"500-9002","您无权查看其他人员调休！"),


    //统计管理相关  从 500-10001 开始
    STATISTICS_AREAID_NULL_ERROR(500,"500-10001","区域ID不可为空！"),
    STATISTICS_COST_TYPE_NULL_ERROR(500,"500-10002","统计类型不可为空！"),
    STATISTICS_AREA_OUT_ERROR(500,"500-10003","区长无权查看其他区域统计！"),
    STATISTICS_START_TIME_NULL_ERROR(500,"500-10004","开始时间不可为空！"),
    STATISTICS_END_TIME_NULL_ERROR(500,"500-10005","结束时间不可为空！"),


    //其他相关  从 500-8001 开始
    EMAIL_ERROR(500,"500-8001","请检查email格式是否正确"),
    PHONE_ERROR(500,"500-8002","请检查phone格式是否正确"),
    INPUT_PROJECT_ERROR(500,"500-8003","你选择的项目错误"),
    QUERY_NOT_DATA(500,"500-8004","所选参数查询暂无数据"),
    INPUT_ERROR(500,"500-8005","输入参数有误"),
    ENTITY_ERROR(500,"500-8006","该对象不存在"),
    DATA_TYPE_ERROR(500,"500-8007","数据类型不匹配"),
    JOB_START_STATE_EDIT_ERROR(500,"500-8008","当前任务是开启状态，不能编辑任务，请先关闭任务"),
    ENTITY_EXIST(500,"500-8009","该对象已存在"),
    PROJECT_TEMPLATE_EXIST(500,"500-8010","输入的项目名称已存在，请重新输入！"),
    ENTITY_EXITS_BAN_OPERATE(500,"500-8011","该项目已被分配，禁止操作"),
    EMAIL_EXITS_ERROR(500,"500-8012","你输入的邮箱已存在请重新输入"),
    HOLIDAY_EXITS_ERROR(500,"500-8013","不能重复添加日期"),
    HOLIDAY_INPUT_INFORMATION_ERROR(500,"500-8014","您的操作有误，请选中日期数字"),
    INPUT_START_TIME_NULL_ERROR(500,"500-8015","您输入的开始时间不能为空"),
    INPUT_END_TIME_NULL_ERROR(500,"500-8015","您输入的结束时间不能为空"),





    ;


    private int httpCode;
    private String errorCode;
    private String errorMessage;

    ServiceErrorCodeEnum(int httpCode, String errorCode, String errorMessage) {
        this.httpCode = httpCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
