package com.nhanab.demosecurity.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResponse<T> implements Serializable {
    private List<T> content;
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private Sort sort;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.totalElements = (int)page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.sort = page.getSort();
    }
}
