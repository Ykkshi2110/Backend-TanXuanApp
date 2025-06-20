package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.Category;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchCategory;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories/create")
    @ApiMessage("Create a category")
    public ResponseEntity<Category> createCategory(@RequestBody @Valid Category reqCategory) {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(this.categoryService.handleCreateCategory(reqCategory));
    }

    @PostMapping("/categories/update")
    @ApiMessage("Update a category")
    public ResponseEntity<Category> updateCategory(@RequestBody @Valid Category reqCategory) {
        return ResponseEntity.ok(this.categoryService.handleUpdateCategory(reqCategory));
    }

    @DeleteMapping("/categories/delete/{id}")
    @ApiMessage("Delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        this.categoryService.handleDeleteCategoryById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/categories")
    @ApiMessage("Fetch all categories")
    public ResponseEntity<PaginationResponse> fetchAllCategories(Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.handleFetchAllCategories(pageable));
    }

    @GetMapping("/categories/{id}")
    @ApiMessage("Fetch category by id")
    public ResponseEntity<Category> fetchCategoryById(@PathVariable int id) {
        return ResponseEntity.ok(this.categoryService.handleFetchCategoryById(id));
    }

    @PostMapping("/categories/filter")
    @ApiMessage("Filter category with criteria")
    public ResponseEntity<PaginationResponse> filterCategoryWithCriteria(Pageable pageable,@RequestBody CriteriaSearchCategory criteriaSearchCategory) {
        return ResponseEntity.ok(this.categoryService.handleFilteredCategories(pageable, criteriaSearchCategory));
    }
}
