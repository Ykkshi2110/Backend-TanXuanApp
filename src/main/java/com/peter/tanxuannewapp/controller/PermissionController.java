package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.Permission;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("/permissions/create")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@RequestBody @Valid Permission reqCreatePermission) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.permissionService.handleCreatePermission(reqCreatePermission));
    }

    @PostMapping("/permissions/update")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@RequestBody @Valid Permission reqUpdatePermission) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.permissionService.handleUpdatePermission(reqUpdatePermission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable int id) {
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<PaginationResponse> fetchAllPermissions(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.permissionService.handleFetchAllPermissions(pageable));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Fetch permission by id")
    public ResponseEntity<Permission> fetchPermissionById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.permissionService.handleFetchPermissionById(id));
    }
}
