package com.CafeSystem.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class PaginatedResponse <T>{
    List<T> content;
    int currentPage;
    int totalPages;
    long totalItems;
    boolean hasNext;
    boolean hasPrevious;
    String nextPageURL;
    String prevPageURL;
}
