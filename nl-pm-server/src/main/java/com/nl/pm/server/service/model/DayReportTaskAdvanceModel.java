package com.nl.pm.server.service.model;

import java.util.Date;

public class DayReportTaskAdvanceModel extends DayReportTaskModel {
    private Date reportDate;

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
