package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.Supplier;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private static final String SUPPLIER_ALREADY_EXISTS_MESSAGE = "Supplier already exists";

    public Supplier findSupplierById(int supplierId){
        return supplierRepository.findById(supplierId).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
    }

    public Supplier handleCreateSupplier(Supplier reqSupplier) {
        if(this.supplierRepository.existsByName(reqSupplier.getName())) {
            throw new ResourceAlreadyExistsException(SUPPLIER_ALREADY_EXISTS_MESSAGE);
        }
        return this.supplierRepository.save(reqSupplier);
    }

    public Supplier handleUpdateSupplier(Supplier reqSupplier) {
        Supplier currentSupplier = findSupplierById(reqSupplier.getId());
        currentSupplier.setName(reqSupplier.getName());
        currentSupplier.setActive(reqSupplier.isActive());
        currentSupplier.setContactInfo(reqSupplier.getContactInfo());
        return this.supplierRepository.save(currentSupplier);
    }

    public void handleDeleteSupplier(int reqSupplierId) {
        Supplier currentSupplier = findSupplierById(reqSupplierId);
        this.supplierRepository.delete(currentSupplier);
    }

    public PaginationResponse handleFetchAllSuppliers(Pageable pageable) {
        Page<Supplier> supplierPages = this.supplierRepository.findAll(pageable);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(supplierPages.getTotalPages());
        meta.setTotal(supplierPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(supplierPages.getContent());
        return paginationResponse;
    }

    public Supplier handleFetchSupplierById(int reqSupplierId) {
        return findSupplierById(reqSupplierId);
    }
}
