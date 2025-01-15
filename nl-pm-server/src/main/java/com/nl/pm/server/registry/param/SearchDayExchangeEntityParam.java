package com.nl.pm.server.registry.param;

import com.nl.pm.server.common.pages.BasePagesParam;

public class SearchDayExchangeEntityParam extends BasePagesParam {


    private Integer areaId;

    private Integer userId;
    private String nickname;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
