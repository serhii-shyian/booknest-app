package com.example.bookstore.service.category;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto save(CreateCategoryRequestDto categoryDto);

    CategoryDto findById(Long categoryId);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto updateById(Long categoryId, CreateCategoryRequestDto categoryDto);

    void deleteById(Long categoryId);
}
