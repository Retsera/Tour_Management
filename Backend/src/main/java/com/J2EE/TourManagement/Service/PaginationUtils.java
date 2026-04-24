package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.DTO.Meta;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {
    public static <T> ResultPaginationDTO build(Page<T> page, Pageable pageable) {
        Meta meta = new Meta();
        meta.setPageCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }
}
