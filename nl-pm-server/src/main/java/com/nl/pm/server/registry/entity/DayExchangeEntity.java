package com.nl.pm.server.registry.entity;

public class DayExchangeEntity {

    /**
     * 调休Id
     */
    private Integer id;

    /**
     * 关联日报Id
     */
    private Integer dayReportId;

    /**
     * 请假时长
     */
    private Double leaveHour;

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDayReportId() {
        return dayReportId;
    }

    public void setDayReportId(Integer dayReportId) {
        this.dayReportId = dayReportId;
    }

    public Double getLeaveHour() {
        return leaveHour;
    }

    public void setLeaveHour(Double leaveHour) {
        this.leaveHour = leaveHour;
    }
}
