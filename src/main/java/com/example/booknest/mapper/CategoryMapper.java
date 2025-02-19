package com.example.booknest.mapper;

import com.example.booknest.config.MapperConfig;
import com.example.booknest.dto.category.CategoryDto;
import com.example.booknest.dto.category.CreateCategoryRequestDto;
import com.example.booknest.model.Category;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto categoryDto);

    List<CategoryDto> toDtoList(List<Category> categories);

    void updateEntityFromDto(
            CreateCategoryRequestDto categoryDto,
            @MappingTarget Category category);
}
