package com.nl.pm.server.common.pages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class BasePagesParam {
    @ApiModelProperty(value = "当前页码")
    private int currentPage = 1;
    @ApiModelProperty(value = "每页条数")
    private int pageSize = 20;
    @ApiModelProperty(value = "模糊查询（按需使用）")
    private String searchVal;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchVal() {
        return searchVal;
    }

    public void setSearchVal(String searchVal) {
        this.searchVal = searchVal;
    }
}
