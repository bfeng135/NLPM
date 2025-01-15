package com.nl.pm.server.common.pages;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class BasePagesDomain<T> {
    @ApiModelProperty(value = "当前页码")
    private long currentPage;
    @ApiModelProperty(value = "总条数")
    private long total;
    @ApiModelProperty(value = "总页数")
    private long totalPage;
    @ApiModelProperty(value = "每页记录条数")
    private long pageSize;
    @ApiModelProperty(value = "返回数据对象集")
    private List<T> totalList;

    public BasePagesDomain() {
        this.currentPage = 1;
        this.pageSize = 10;
        this.total = 0;
        this.totalPage = 0;
        totalList = new ArrayList<>();
    }

    public BasePagesDomain(IPage<T> iPage) {
        if (iPage != null) {
            this.currentPage = iPage.getCurrent();
            this.pageSize = iPage.getSize();
            this.total = iPage.getTotal();
            this.totalPage = iPage.getPages();
            this.totalList = iPage.getRecords();
        }
    }

    public BasePagesDomain(long currentPage, long pageSize, long totalPage,long total) {
        this.currentPage = currentPage;
        this.total = total;
        this.totalPage = totalPage;
        this.pageSize = pageSize;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getTotalList() {
        return totalList;
    }

    public void setTotalList(List<T> totalList) {
        this.totalList = totalList;
    }

    @Override
    public String toString() {
        return "ResPageVO{" +
                "currentPage=" + currentPage +
                ", total=" + total +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", totalList=" + totalList +
                '}';
    }
}
