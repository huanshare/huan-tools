package com.huanshare.tools.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *  2018/3/14.
 */
public class PageResponseDto<T> {

    List<T> rows = new ArrayList<>();
    Long total = 0L;

    public PageResponseDto() {
    }

    public PageResponseDto(List<T> t, Long total) {
        this.rows = t;
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}

