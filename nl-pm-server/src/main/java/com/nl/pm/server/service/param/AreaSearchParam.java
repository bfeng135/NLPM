package com.nl.pm.server.service.param;

public class AreaSearchParam {


    private int pageNumber;

    private int pageSize;

    private String golbalName;

    /*
     *  区域状态标识（true为启用，false为禁用，null为全部）
     */
    private Boolean statusFlag;

    public Boolean getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(Boolean statusFlag) {
        this.statusFlag = statusFlag;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getGolbalName() {
        return golbalName;
    }

    public void setGolbalName(String golbalName) {
        this.golbalName = golbalName;
    }
}
