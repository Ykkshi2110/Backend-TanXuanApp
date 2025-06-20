package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.QSupplier;
import com.peter.tanxuannewapp.domain.Supplier;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchSupplier;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.domain.resposne.ResSupplierDTO;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.SupplierRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;
    private static final String SUPPLIER_ALREADY_EXISTS_MESSAGE = "Supplier already exists";

    public ResSupplierDTO convertSupplierToDTO(Supplier supplier) {
        Converter<Collection<Integer>, Integer> collectionToSize = collection -> collection.getSource().size();
        modelMapper.typeMap(Supplier.class, ResSupplierDTO.class).addMappings(mapper -> {
            mapper.using(collectionToSize).map(Supplier::getProducts, ResSupplierDTO::setTotalProducts);
        });
        return modelMapper.map(supplier, ResSupplierDTO.class);
    }

    public Supplier findSupplierById(int supplierId){
        return supplierRepository.findById(supplierId).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
    }

    public ResSupplierDTO handleCreateSupplier(Supplier reqSupplier) {
        if(this.supplierRepository.existsByName(reqSupplier.getName())) {
            throw new ResourceAlreadyExistsException(SUPPLIER_ALREADY_EXISTS_MESSAGE);
        }
        this.supplierRepository.save(reqSupplier);
        return this.convertSupplierToDTO(reqSupplier);
    }

    public ResSupplierDTO handleUpdateSupplier(Supplier reqSupplier) {
        Supplier currentSupplier = findSupplierById(reqSupplier.getId());
        currentSupplier.setName(reqSupplier.getName());
        currentSupplier.setActive(reqSupplier.isActive());
        currentSupplier.setContactInfo(reqSupplier.getContactInfo());
        this.supplierRepository.save(currentSupplier);
        return this.convertSupplierToDTO(currentSupplier);
    }

    public void handleDeleteSupplier(int reqSupplierId) {
        Supplier currentSupplier = findSupplierById(reqSupplierId);
        this.supplierRepository.delete(currentSupplier);
    }

    public PaginationResponse handleFetchAllSuppliers(Pageable pageable) {
        Page<Supplier> supplierPages = this.supplierRepository.findAll(pageable);
        Page<ResSupplierDTO> supplierDTOPages = supplierPages.map(this::convertSupplierToDTO);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(supplierDTOPages.getTotalPages());
        meta.setTotal(supplierDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(supplierDTOPages.getContent());
        return paginationResponse;
    }

    public ResSupplierDTO handleFetchSupplierById(int reqSupplierId) {
        Supplier supplier = findSupplierById(reqSupplierId);
        return this.convertSupplierToDTO(supplier);
    }

    public PaginationResponse handleFilteredSuppliers(Pageable pageable, CriteriaSearchSupplier criteriaSearchSupplier) {
        QSupplier qSupplier = QSupplier.supplier;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(criteriaSearchSupplier.getName() != null && !criteriaSearchSupplier.getName().isEmpty()) {
            booleanBuilder.and(qSupplier.name.containsIgnoreCase(criteriaSearchSupplier.getName()));
        }
        if(criteriaSearchSupplier.getContactInfo() != null && !criteriaSearchSupplier.getContactInfo().isEmpty()) {
            booleanBuilder.and(qSupplier.contactInfo.containsIgnoreCase(criteriaSearchSupplier.getContactInfo()));
        }
        if(criteriaSearchSupplier.getActive() != null) {
            booleanBuilder.and(qSupplier.active.isNotNull());
        }
        if(criteriaSearchSupplier.getCreatedAt() != null && !criteriaSearchSupplier.getCreatedAt().isEmpty()){
            LocalDate localDate = LocalDate.parse(criteriaSearchSupplier.getCreatedAt());
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
            booleanBuilder.and(qSupplier.createdAt.between(startOfDay, endOfDay));
        }

        Page<Supplier> supplierFilteredPages = this.supplierRepository.findAll(booleanBuilder, pageable);
        Page<ResSupplierDTO> supplierFilteredDTOPages = supplierFilteredPages.map(this::convertSupplierToDTO);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(supplierFilteredDTOPages.getTotalPages());
        meta.setTotal(supplierFilteredDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(supplierFilteredDTOPages.getContent());
        return paginationResponse;
    }
}
