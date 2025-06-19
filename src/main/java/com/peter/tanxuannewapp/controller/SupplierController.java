package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.Supplier;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.domain.resposne.ResSupplierDTO;
import com.peter.tanxuannewapp.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping("/suppliers/create")
    @ApiMessage("Create a supplier")
    public ResponseEntity<ResSupplierDTO> createSupplier(@RequestBody @Valid Supplier reqSupplier) {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(this.supplierService.handleCreateSupplier(reqSupplier));
    }

    @PostMapping("/suppliers/update")
    @ApiMessage("Update a supplier")
    public ResponseEntity<ResSupplierDTO> updateSupplier(@RequestBody @Valid Supplier reqSupplier) {
        return ResponseEntity.ok(this.supplierService.handleUpdateSupplier(reqSupplier));
    }

    @DeleteMapping("/suppliers/delete/{id}")
    @ApiMessage("Delete a supplier")
    public ResponseEntity<Void> deleteSupplier(@PathVariable int id) {
        this.supplierService.handleDeleteSupplier(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/suppliers")
    @ApiMessage("Fetch all suppliers")
    public ResponseEntity<PaginationResponse> fetchAllSuppliers(Pageable pageable) {
        return ResponseEntity.ok(this.supplierService.handleFetchAllSuppliers(pageable));
    }

    @GetMapping("/suppliers/{id}")
    @ApiMessage("Fetch supplier by id")
    public ResponseEntity<ResSupplierDTO> fetchSupplierById(@PathVariable int id) {
        return ResponseEntity.ok(this.supplierService.handleFetchSupplierById(id));
    }
}
