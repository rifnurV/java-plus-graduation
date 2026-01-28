package ru.practicum.core.categoryservice.service;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.core.categoryservice.client.EventClient;
import ru.practicum.core.categoryservice.exception.ConflictException;
import ru.practicum.core.categoryservice.exception.ConstraintException;
import ru.practicum.core.categoryservice.exception.NotFoundException;
import ru.practicum.core.categoryservice.model.Category;
import ru.practicum.core.categoryservice.model.dto.CategoryDto;
import ru.practicum.core.categoryservice.model.dto.CategoryRequest;
import ru.practicum.core.categoryservice.model.mapper.CategoryMapper;
import ru.practicum.core.categoryservice.repository.CategoryRepository;


import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final EventClient eventClient;
    private final String errorMessageNotFound = "Category with id = %d was not found";
    private final String errorMessageAlreadyExist = "Category with name = %s is already exists";
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size, Sort.by("id").ascending());
        List<Category> categories = categoryRepository.findAll(page).getContent();
        return categories.stream().map(CategoryMapper::toCategoryDto).toList();
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(errorMessageNotFound, categoryId))
        );
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto create(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ConstraintException(String.format(errorMessageAlreadyExist, categoryRequest.getName()));
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryRequest)));
    }

    @Override
    public CategoryDto update(Long categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(errorMessageNotFound, categoryId))
        );
        Optional<Category> existingCategory = categoryRepository.findByName(categoryRequest.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
            throw new ConflictException(String.format(errorMessageAlreadyExist, categoryRequest.getName()));
        }
        category.setName(categoryRequest.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long categoryId) {
        // 1. Проверяем, существует ли сама категория
        categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(errorMessageNotFound, categoryId))
        );
        // 2. Спрашиваем у event-service, есть ли события
        try {
            ResponseEntity<Boolean> response = eventClient.hasEventsWithCategory(categoryId);
            if (Boolean.TRUE.equals(response.getBody())) {
                throw new ConflictException("The category is not empty");
            }
        } catch (FeignException e) {
            // Если сервис событий недоступен, лучше не удалять категорию
            throw new RuntimeException("Event service is unavailable");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(errorMessageNotFound, categoryId)));
    }
}