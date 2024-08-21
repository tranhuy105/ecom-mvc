package com.tranhuy105.admin.utils;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PaginationUtil {
    public static void setPaginationAttributes(int page, int PAGE_SIZE, String search, Model model, Page<?> pageData) {
        long startCount = (long) (page - 1) * PAGE_SIZE + 1;
        long endCount = startCount + PAGE_SIZE - 1;
        if (endCount > pageData.getTotalElements()) {
            endCount = pageData.getTotalElements();
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageData.getTotalElements());
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("q", search);
    }

}
