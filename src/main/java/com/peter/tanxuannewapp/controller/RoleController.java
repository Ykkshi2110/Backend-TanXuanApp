package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.Role;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles/create")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@RequestBody @Valid Role reqCreateRole) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.roleService.handleCreateRole(reqCreateRole));
    }

    @PostMapping("/roles/update")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@RequestBody @Valid Role reqUpdateRole) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.roleService.handleUpdateRole(reqUpdateRole));
    }

    @DeleteMapping("/roles/delete/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable int id) {
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<PaginationResponse> fetchAllRoles(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.roleService.handleFetchAllRoles(pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> fetchRoleById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.roleService.handleFetchRoleById(id));
    }
}
