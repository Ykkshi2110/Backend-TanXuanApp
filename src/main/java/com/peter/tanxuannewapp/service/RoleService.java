package com.peter.tanxuannewapp.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.peter.tanxuannewapp.domain.Permission;
import com.peter.tanxuannewapp.domain.QRole;
import com.peter.tanxuannewapp.domain.Role;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchRole;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.PermissionRepository;
import com.peter.tanxuannewapp.repository.RoleRepository;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private static final String ROLE_NAME_ALREADY_EXIST = "Role name already exist";
    private static final String ROLE_NOT_EXIST = "Role not exist";

    public Role handleCreateRole(Role reqCreateRole) {
        if (this.roleRepository.existsByName(reqCreateRole.getName()))
            throw new ResourceAlreadyExistsException(ROLE_NAME_ALREADY_EXIST);

        // set permission
        if(reqCreateRole.getPermissions() != null){
            List<Integer> permissionIds = reqCreateRole.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionIds);
            reqCreateRole.setPermissions(permissions);
        }

        return this.roleRepository.save(reqCreateRole);
    }

    public Role handleUpdateRole(Role reqUpdateRole) {
        Role currentRole = this.roleRepository.findById(reqUpdateRole.getId()).orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_EXIST));

        currentRole.setName(reqUpdateRole.getName());
        currentRole.setDescription(reqUpdateRole.getDescription());

        // check Permission
        if(reqUpdateRole.getPermissions() != null){
            List<Integer> permissionIds = reqUpdateRole.getPermissions().stream().map(Permission::getId).toList();
            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionIds);
            currentRole.setPermissions(permissions);
        }

        return this.roleRepository.save(currentRole);
    }

    public void handleDeleteRole(int roleId){
        Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_EXIST));
        this.roleRepository.delete(role);
    }

    public PaginationResponse handleFetchAllRoles(Pageable pageable){
        Page<Role> rolePages = this.roleRepository.findAll(pageable);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(rolePages.getTotalPages());
        meta.setTotal(rolePages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(rolePages.getContent());

        return paginationResponse;
    }

    public Role handleFetchRoleById(int roleId){
        return this.roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_EXIST));
    }

    public PaginationResponse handleFilteredRoles(Pageable pageable, CriteriaSearchRole criteriaSearchRole){
        QRole role = QRole.role;
        BooleanBuilder builder = new BooleanBuilder();
        if(criteriaSearchRole.getName() != null && !criteriaSearchRole.getName().isEmpty()){
            builder.and(role.name.containsIgnoreCase(criteriaSearchRole.getName()));
        }
        if(criteriaSearchRole.getCreatedAt() != null && !criteriaSearchRole.getCreatedAt().isEmpty()){
            LocalDate localDate = LocalDate.parse(criteriaSearchRole.getCreatedAt());
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
            builder.and(role.createdAt.between(startOfDay, endOfDay));
        }

        Page<Role> rolePages = this.roleRepository.findAll(builder, pageable);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(rolePages.getTotalPages());
        meta.setTotal(rolePages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(rolePages.getContent());

        return paginationResponse;
    }
}
