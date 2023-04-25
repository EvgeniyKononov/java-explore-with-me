package ru.practicum.ewm.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(PageRequest page);

    CategoryDto getCategory(Long catId);

    Category getCategoryEntity(Long catId);
}
