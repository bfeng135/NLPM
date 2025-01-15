package com.nl.pm.server.service.model;

import java.util.ArrayList;
import java.util.List;

public class DayReportAdvanceModel extends DayReportModel {
    private List<DayReportTaskModel> dayReportList;
    private DayExchangeModel leaveModel;

    public DayReportAdvanceModel() {
        dayReportList = new ArrayList<>();
        leaveModel = new DayExchangeModel();
    }

    public List<DayReportTaskModel> getDayReportList() {
        return dayReportList;
    }

    public void setDayReportList(List<DayReportTaskModel> dayReportList) {
        this.dayReportList = dayReportList;
    }

    public DayExchangeModel getLeaveModel() {
        return leaveModel;
    }

    public void setLeaveModel(DayExchangeModel leaveModel) {
        this.leaveModel = leaveModel;
    }
}