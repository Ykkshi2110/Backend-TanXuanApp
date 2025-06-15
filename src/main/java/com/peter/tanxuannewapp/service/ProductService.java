package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.Category;
import com.peter.tanxuannewapp.domain.Product;
import com.peter.tanxuannewapp.domain.QProduct;
import com.peter.tanxuannewapp.domain.Supplier;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchProduct;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.domain.resposne.ResProductDTO;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.CategoryRepository;
import com.peter.tanxuannewapp.repository.ProductRepository;
import com.peter.tanxuannewapp.repository.SupplierRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private static final String PRODUCT_NOT_FOUND = "Product Not Found";

    public ResProductDTO convertToProductDTO(Product product) {
        return modelMapper.map(product, ResProductDTO.class);
    }

    public Product handleCreateProduct(Product reqProduct){
        if(this.productRepository.existsByName(reqProduct.getName())) throw new ResourceAlreadyExistsException("Product already exists");

        // check exists supplier
        if(reqProduct.getSupplier() != null){
            Supplier supplier = this.supplierRepository.findById(reqProduct.getSupplier().getId()).orElse(null);
            reqProduct.setSupplier(supplier);
        }

        // check exists category
        if(reqProduct.getCategory() != null){
            Category category = this.categoryRepository.findById(reqProduct.getCategory().getId()).orElse(null);
            reqProduct.setCategory(category);
        }

        return this.productRepository.save(reqProduct);
    }

    public Product handleUpdateProduct(Product reqProduct){
        Product currentProduct = this.productRepository.findById(reqProduct.getId()).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));

        if(reqProduct.getSupplier() != null){
            Supplier supplier = this.supplierRepository.findById(reqProduct.getSupplier().getId()).orElse(null);
            currentProduct.setSupplier(supplier);
        }

        if(reqProduct.getCategory() != null){
            Category category = this.categoryRepository.findById(reqProduct.getCategory().getId()).orElse(null);
            reqProduct.setCategory(category);
        }

        currentProduct.setName(reqProduct.getName());
        currentProduct.setQuantity(reqProduct.getQuantity());
        currentProduct.setDescription(reqProduct.getDescription());
        currentProduct.setUnit(reqProduct.getUnit());
        currentProduct.setProductImage(reqProduct.getProductImage());
        currentProduct.setPrice(reqProduct.getPrice());
        return this.productRepository.save(currentProduct);
    }

    public void handleDeleteProduct(int id){
        Product currentProduct = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
        this.productRepository.delete(currentProduct);
    }

    public PaginationResponse handleFetchAllProducts(Pageable pageable){
        Page<Product> productPages = this.productRepository.findAll(pageable);
        Page<ResProductDTO> productDTOPages = productPages.map(this::convertToProductDTO);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(productDTOPages.getTotalPages());
        meta.setTotal(productDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(productDTOPages.getContent());
        return paginationResponse;
    }

    public ResProductDTO handleFetchProductById(int id){
        Product product = this.productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
        return this.convertToProductDTO(product);
    }

    public PaginationResponse handleFilteredProducts(Pageable pageable, CriteriaSearchProduct criteria){
        QProduct qProduct = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();
        if(criteria.getName() != null && !criteria.getName().isEmpty()){
            builder.and(qProduct.name.contains(criteria.getName()));
        }
        if(criteria.getCategory() != null) {
            builder.and(qProduct.category.id.eq(criteria.getCategory().getId()));
        }
        if(criteria.getSupplier() != null) {
            builder.and(qProduct.supplier.id.eq(criteria.getSupplier().getId()));
        }
        if(criteria.getUnit() != null && !criteria.getUnit().isEmpty()){
            builder.and(qProduct.unit.contains(criteria.getUnit()));
        }
        if(criteria.getQuantity() != 0) {
            builder.and(qProduct.quantity.eq(criteria.getQuantity()));
        }
        if(criteria.getPrice() != 0) {
            builder.and(qProduct.price.eq(criteria.getPrice()));
        }

        Page<Product> productPages = this.productRepository.findAll(builder, pageable);
        Page<ResProductDTO> productDTOPages = productPages.map(this::convertToProductDTO);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(productDTOPages.getTotalPages());
        meta.setTotal(productDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(productDTOPages.getContent());
        return paginationResponse;
    }

}
