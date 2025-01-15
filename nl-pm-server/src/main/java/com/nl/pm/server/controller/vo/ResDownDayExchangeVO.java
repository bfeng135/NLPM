package com.nl.pm.server.controller.vo;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/14 16:25
 */
public class ResDownDayExchangeVO {
    private Integer userId;
    private String nickname;
    private List<ResDownDayExchangeDateVO> dateVOList;
    private Double totalExchange = 0.0;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<ResDownDayExchangeDateVO> getDateVOList() {
        return dateVOList;
    }

    public void setDateVOList(List<ResDownDayExchangeDateVO> dateVOList) {
        this.dateVOList = dateVOList;
    }

    public Double getTotalExchange() {
        return totalExchange;
    }

    public void setTotalExchange(Double totalExchange) {
        this.totalExchange = totalExchange;
    }
}
