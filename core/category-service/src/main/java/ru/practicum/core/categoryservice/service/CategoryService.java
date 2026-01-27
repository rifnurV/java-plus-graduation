package ru.practicum.core.categoryservice.service;

import ru.practicum.core.categoryservice.model.Category;
import ru.practicum.core.categoryservice.model.dto.CategoryDto;
import ru.practicum.core.categoryservice.model.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long categoryId);

    CategoryDto create(CategoryRequest categoryRequest);

    CategoryDto update(Long categoryId, CategoryRequest categoryRequest);

    void delete(Long categoryId);

    Category getCategoryById(Long categoryId);
}
