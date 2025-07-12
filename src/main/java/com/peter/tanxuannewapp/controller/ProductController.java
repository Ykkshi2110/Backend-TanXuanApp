package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.Product;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchProduct;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.domain.resposne.ResProductDTO;
import com.peter.tanxuannewapp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products/create")
    @ApiMessage("Create a product")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product reqProduct) {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(this.productService.handleCreateProduct(reqProduct));
    }

    @PostMapping("/products/update")
    @ApiMessage("Update a product")
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid Product reqProduct) {
        return ResponseEntity.ok(this.productService.handleUpdateProduct(reqProduct));
    }

    @DeleteMapping("/products/delete/{id}")
    @ApiMessage("Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        this.productService.handleDeleteProduct(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/products")
    @ApiMessage("Fetch all products")
    public ResponseEntity<PaginationResponse> fetchAllProducts(Pageable pageable) {
        return ResponseEntity.ok(this.productService.handleFetchAllProducts(pageable));
    }

    @GetMapping("/products/{id}")
    @ApiMessage("Fetch product by id")
    public ResponseEntity<ResProductDTO> fetchProductById(@PathVariable int id) {
        return ResponseEntity.ok(this.productService.handleFetchProductById(id));
    }

    @PostMapping("/products/filter")
    @ApiMessage("Filter product with criteria")
    public ResponseEntity<PaginationResponse> filterProduct(Pageable pageable,
            @RequestBody CriteriaSearchProduct criteriaSearchProduct) {
        return ResponseEntity.ok(this.productService.handleFilteredProducts(pageable, criteriaSearchProduct));
    }
}
