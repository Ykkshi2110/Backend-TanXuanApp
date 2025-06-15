package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.Category;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category handleCreateCategory(Category reqCategory) {
        if(this.categoryRepository.existsByName(reqCategory.getName())) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

        return this.categoryRepository.save(reqCategory);
    }

    public Category handleFetchCategoryById(int id) {
        return this.categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public Category handleUpdateCategory(Category reqCategory) {
        Category currentCategory = this.handleFetchCategoryById(reqCategory.getId());
        currentCategory.setName(reqCategory.getName());
        currentCategory.setActive(reqCategory.isActive());
        return this.categoryRepository.save(currentCategory);
    }

    public void handleDeleteCategoryById(int id) {
        Category currentCategory = this.handleFetchCategoryById(id);
        this.categoryRepository.delete(currentCategory);
    }

    public PaginationResponse handleFetchAllCategories(Pageable pageable) {
        Page<Category> categoryPages = this.categoryRepository.findAll(pageable);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(categoryPages.getTotalPages());
        meta.setTotal(categoryPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(categoryPages.getContent());
        return paginationResponse;
    }

}
