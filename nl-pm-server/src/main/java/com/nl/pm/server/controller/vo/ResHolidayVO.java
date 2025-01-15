package com.nl.pm.server.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel
public class ResHolidayVO {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "Integer(year)")
    private Integer year;
    @ApiModelProperty(value = "Integer(month)")
    private Integer month;
    @ApiModelProperty(value = "Integer(day)")
    private Integer day;

    /**
     * 日期(yyyy-mm-dd)
     */
    @ApiModelProperty(value = "日期(yyyy-mm-dd)")
    private Date dateYmd;

    /**
     * 日期字符串（yyyy-mm-dd）
     */
    @ApiModelProperty(value = "日期字符串（yyyy-mm-dd）")
    private String dateStr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Date getDateYmd() {
        return dateYmd;
    }

    public void setDateYmd(Date dateYmd) {
        this.dateYmd = dateYmd;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
