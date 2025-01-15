package com.nl.pm.server.registry.param;

public class AreaPageableQueryParam {

    private int startIndex;

    private int pageSize;

    private String globalName;

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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }
}
