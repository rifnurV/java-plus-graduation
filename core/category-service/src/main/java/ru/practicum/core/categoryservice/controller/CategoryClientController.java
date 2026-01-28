package ru.practicum.core.categoryservice.controller;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.core.categoryservice.model.dto.CategoryDto;
import ru.practicum.core.categoryservice.service.CategoryService;

@RestController
@AllArgsConstructor
@RequestMapping("/client/categories")
public class CategoryClientController {

    private final CategoryService service;

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Long categoryId) throws FeignException{
        CategoryDto categoryDto = service.getById(categoryId);
        return ResponseEntity.ok(categoryDto).getBody();
    };

}
