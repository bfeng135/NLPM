package com.nl.pm.server.registry.entity;

import java.util.Date;

public class DayReportTaskAdvanceEntity extends DayReportTaskEntity{
    private Date reportDate;

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
