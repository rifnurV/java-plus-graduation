package ru.practicum.core.eventservice.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.core.eventservice.client.dto.CategoryDto;

@FeignClient(name = "category-service", path = "/client/categories")
public interface CategoryClient {

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable("categoryId") Long categoryId) throws FeignException;


}
