package com.tranhuy105.admin.product.service;

import com.tranhuy105.admin.product.DTO.ImageInstructionDTO;
import com.tranhuy105.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    Page<Product> findAll(int page, String search, Integer category);

    List<Product> lazyFetchAttribute(List<Product> products);

    Product findById(Integer id);

    void save(Product product, MultipartFile[] _files, List<ImageInstructionDTO> imageInstructionDTOs) throws IOException;

    void delete(Integer id);
}
