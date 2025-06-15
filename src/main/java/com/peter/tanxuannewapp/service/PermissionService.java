package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.Permission;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private static final String NAME_OR_ROUTE_EXISTS = "Name or Route already exists";
    private static final String PERMISSION_NOT_EXISTS = "Permission not exists";

    public Permission handleCreatePermission(Permission reqCreatePermission) {
        if(this.permissionRepository.existsByNameAndRoute(reqCreatePermission.getName(), reqCreatePermission.getRoute())) throw new ResourceAlreadyExistsException(NAME_OR_ROUTE_EXISTS);
        return this.permissionRepository.save(reqCreatePermission);
    }

    public Permission handleUpdatePermission(Permission reqUpdatePermission) {
        Permission currentPermission = this.permissionRepository.findById(reqUpdatePermission.getId()).orElseThrow(() -> new ResourceNotFoundException(PERMISSION_NOT_EXISTS));

        // check name and route
        if(this.permissionRepository.existsByNameAndRoute(reqUpdatePermission.getName(), reqUpdatePermission.getRoute())) throw new ResourceAlreadyExistsException(NAME_OR_ROUTE_EXISTS);

        currentPermission.setName(reqUpdatePermission.getName());
        currentPermission.setRoute(reqUpdatePermission.getRoute());
        return this.permissionRepository.save(currentPermission);
    }

    public void handleDeletePermission(int permissionId) {
        Permission currentPermission = this.permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(PERMISSION_NOT_EXISTS));
        this.permissionRepository.delete(currentPermission);
    }

    public PaginationResponse handleFetchAllPermissions(Pageable pageable) {
        Page<Permission> permissionPages = this.permissionRepository.findAll(pageable);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(permissionPages.getTotalPages());
        meta.setTotal(permissionPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(permissionPages.getContent());

        return paginationResponse;
    }

    public Permission handleFetchPermissionById(int permissionId) {
        return this.permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }
}
