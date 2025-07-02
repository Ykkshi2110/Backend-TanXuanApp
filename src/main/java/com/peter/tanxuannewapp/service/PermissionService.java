package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.Permission;
import com.peter.tanxuannewapp.domain.QPermission;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchPermission;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.PermissionRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private static final String NAME_OR_MODULE_OR_METHOD_OR_ROUTE_EXISTS = "Name or Route already exists";
    private static final String PERMISSION_NOT_EXISTS = "Permission not exists";

    public Permission handleCreatePermission(Permission reqCreatePermission) {
        if(this.permissionRepository.existsByNameAndModuleAndMethodAndRoute(reqCreatePermission.getName(), reqCreatePermission.getModule(), reqCreatePermission.getMethod(), reqCreatePermission.getRoute())) throw new ResourceAlreadyExistsException(NAME_OR_MODULE_OR_METHOD_OR_ROUTE_EXISTS);
        return this.permissionRepository.save(reqCreatePermission);
    }

    public Permission handleUpdatePermission(Permission reqUpdatePermission) {
        Permission currentPermission = this.permissionRepository.findById(reqUpdatePermission.getId()).orElseThrow(() -> new ResourceNotFoundException(PERMISSION_NOT_EXISTS));

        // check name and route and method
        if(this.permissionRepository.existsByNameAndModuleAndMethodAndRoute(reqUpdatePermission.getName(),reqUpdatePermission.getModule(), reqUpdatePermission.getMethod(), reqUpdatePermission.getRoute())) throw new ResourceAlreadyExistsException(NAME_OR_MODULE_OR_METHOD_OR_ROUTE_EXISTS);

        currentPermission.setName(reqUpdatePermission.getName());
        currentPermission.setModule(reqUpdatePermission.getModule());
        currentPermission.setMethod(reqUpdatePermission.getMethod());
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

    public PaginationResponse handleFilteredPermissions(Pageable pageable, CriteriaSearchPermission criteriaSearchPermission) {
        QPermission qPermission = QPermission.permission;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(criteriaSearchPermission.getName() != null && !criteriaSearchPermission.getName().isEmpty()) {
            booleanBuilder.and(qPermission.name.containsIgnoreCase(criteriaSearchPermission.getName()));
        }
        if(criteriaSearchPermission.getModule() != null && !criteriaSearchPermission.getModule().isEmpty()) {
            booleanBuilder.and(qPermission.module.containsIgnoreCase(criteriaSearchPermission.getModule()));
        }
        if(criteriaSearchPermission.getMethod() != null && !criteriaSearchPermission.getMethod().isEmpty()) {
            booleanBuilder.and(qPermission.method.containsIgnoreCase(criteriaSearchPermission.getMethod()));
        }
        if(criteriaSearchPermission.getRoute() != null && !criteriaSearchPermission.getRoute().isEmpty()) {
            booleanBuilder.and(qPermission.route.containsIgnoreCase(criteriaSearchPermission.getRoute()));
        }
        if(criteriaSearchPermission.getCreatedAt() != null && !criteriaSearchPermission.getCreatedAt().isEmpty()) {
            LocalDate localDate = LocalDate.parse(criteriaSearchPermission.getCreatedAt());
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
            booleanBuilder.and(qPermission.createdAt.between(startOfDay, endOfDay));
        }

        Page<Permission> permissionPages = this.permissionRepository.findAll(booleanBuilder, pageable);
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
}
