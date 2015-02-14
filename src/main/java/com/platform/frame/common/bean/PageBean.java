package com.platform.frame.common.bean;

/**
 * Manage System.
 * author yujie  2015-02-14
 * version 1.0.1
 */
public class PageBean {

    private boolean pagerFlag = true;

    private Integer limit;

    private Integer offset;

    private Integer total;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public PageBean(){
        this.pagerFlag = true;
    }

    public boolean isPagerFlag() {
        return pagerFlag;
    }

    public void setPagerFlag(boolean pagerFlag) {
        this.pagerFlag = pagerFlag;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
