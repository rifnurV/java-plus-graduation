package ru.practicum.core.categoryservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.categoryservice.model.dto.CategoryDto;
import ru.practicum.core.categoryservice.model.dto.CategoryRequest;
import ru.practicum.core.categoryservice.service.CategoryService;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryController {

    private final CategoryService service;
    private final String pathAdmin = "/admin/categories";
    private final String pathCategory = "/categories";
    private final String pathId = "/{catId}";

    @GetMapping(pathCategory)
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return service.getAll(from, size);
    }

    @GetMapping(pathCategory + pathId)
    public ResponseEntity<CategoryDto> getById(@PathVariable(name = "catId") Long categoryId) {
        CategoryDto categoryDto = service.getById(categoryId);
        return ResponseEntity.ok(categoryDto);
    }

    @PostMapping(pathAdmin)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryRequest categoryRequest) {
        return service.create(categoryRequest);
    }

    @PatchMapping(pathAdmin + pathId)
    public ResponseEntity<CategoryDto> update(@RequestBody @Valid CategoryRequest categoryRequest, @PathVariable(name = "catId") Long categoryId) {
        return ResponseEntity.ok(service.update(categoryId, categoryRequest));
    }

    @DeleteMapping(pathAdmin + pathId)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "catId") Long categoryId) {
        service.delete(categoryId);
    }

}
