package com.nl.pm.server.controller.vo;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/14 16:27
 */
public class ResDownDayExchangeDateVO {
    private String date;
    private Double exchange = 0.0;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getExchange() {
        return exchange;
    }

    public void setExchange(Double exchange) {
        this.exchange = exchange;
    }
}
