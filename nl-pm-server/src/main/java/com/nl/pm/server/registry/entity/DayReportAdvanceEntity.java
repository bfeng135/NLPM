package com.nl.pm.server.registry.entity;

import java.util.ArrayList;
import java.util.List;

public class DayReportAdvanceEntity extends DayReportEntity {
    private List<DayReportTaskEntity> dayReportList;
    private DayExchangeEntity leaveEntity;

    public DayReportAdvanceEntity() {
        dayReportList = new ArrayList<>();
        leaveEntity = new DayExchangeEntity();
    }

    public List<DayReportTaskEntity> getDayReportList() {
        return dayReportList;
    }

    public void setDayReportList(List<DayReportTaskEntity> dayReportList) {
        this.dayReportList = dayReportList;
    }

    public DayExchangeEntity getLeaveEntity() {
        return leaveEntity;
    }

    public void setLeaveEntity(DayExchangeEntity leaveEntity) {
        this.leaveEntity = leaveEntity;
    }
}