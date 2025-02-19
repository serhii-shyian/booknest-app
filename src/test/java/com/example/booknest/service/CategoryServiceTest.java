package com.example.booknest.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.booknest.dto.category.CategoryDto;
import com.example.booknest.dto.category.CreateCategoryRequestDto;
import com.example.booknest.exception.EntityNotFoundException;
import com.example.booknest.mapper.CategoryMapper;
import com.example.booknest.model.Category;
import com.example.booknest.repository.category.CategoryRepository;
import com.example.booknest.service.category.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("""
            Save custom category to database from valid DTO
            """)
    public void saveCategory_ValidCreateCategoryDto_ReturnsCategoryDto() {
        //Given
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();
        Category category = getCategoryFromDto(requestDto);
        CategoryDto expected = getDtoFromCategory(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //When
        CategoryDto actual = categoryService.save(requestDto);

        //Then
        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Find category by id when category exists
            """)
    void findByCategoryId_ExistingCategoryId_ReturnsCategory() {
        //Given
        Category category = getCategoryList().get(0);
        CategoryDto expected = getDtoFromCategory(category);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category))
                .thenReturn(expected);

        //When
        CategoryDto actual = categoryService.findById(category.getId());

        //Then
        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Find category by id when category does not exists
            """)
    void findByCategoryId_NonExistingCategoryId_ThrowsException() {
        //Given
        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(99L));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Find all categories by valid parameters when categories exists
            """)
    void getAllCategories_ValidPageable_ReturnsAllCategories() {
        //Given
        Category firstCategory = getCategoryList().get(0);
        Category secondCategory = getCategoryList().get(1);
        CategoryDto firstCategoryDto = getDtoFromCategory(firstCategory);
        CategoryDto secondCategoryDto = getDtoFromCategory(secondCategory);
        List<Category> categoryList = List.of(firstCategory, secondCategory);
        Page<Category> page = new PageImpl<>(categoryList);
        List<CategoryDto> expected = List.of(firstCategoryDto, secondCategoryDto);

        when(categoryRepository.findAll(Pageable.ofSize(5))).thenReturn(page);
        when(categoryMapper.toDtoList(categoryList)).thenReturn(expected);

        //When
        List<CategoryDto> actual = categoryService.findAll(Pageable.ofSize(5));

        //Then
        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Update category by id when category id exists
            """)
    void updateCategory_ExistingId_ReturnsCategoryDto() {
        //Given
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();
        Category category = getCategoryList().get(0);
        category.setName("Fiction123");
        CategoryDto expected = getDtoFromCategory(category);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateEntityFromDto(requestDto, category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        //When
        CategoryDto actual = categoryService.updateById(category.getId(), requestDto);

        //Then
        assertEquals(expected, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Update category by id when category id does not exists
            """)
    void updateCategory_NonExistingId_ThrowsException() {
        //Given
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateById(99L, requestDto));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Delete category by id when category id exists
            """)
    void deleteCategory_ExistingId_ReturnsNothing() {
        //Given
        Category category = getCategoryList().get(0);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        //When
        categoryService.deleteById(category.getId());

        //Then
        verify(categoryRepository).deleteById(category.getId());
    }

    @Test
    @DisplayName("""
            Delete category by id when category id does not exists
            """)
    void deleteCategory_NonExistingId_ThrowsException() {
        //Given
        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(99L));
        verifyNoMoreInteractions(categoryRepository);
    }

    private Category getCategoryFromDto(CreateCategoryRequestDto requestDto) {
        return new Category()
                .setName(requestDto.name())
                .setDescription(requestDto.description());
    }

    private CategoryDto getDtoFromCategory(Category category) {
        return new CategoryDto(
                1L,
                category.getName(),
                category.getDescription());
    }

    private CreateCategoryRequestDto getCreateCategoryRequestDto() {
        return new CreateCategoryRequestDto(
                "Fiction",
                "Fiction books");
    }

    private List<Category> getCategoryList() {
        return List.of(
                new Category()
                        .setId(1L)
                        .setName("Fiction")
                        .setDescription("Fiction books"),
                new Category()
                        .setId(2L)
                        .setName("Autobiography")
                        .setDescription("Autobiography books"));
    }
}
