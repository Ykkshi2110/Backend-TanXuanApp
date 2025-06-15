package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.QUser;
import com.peter.tanxuannewapp.domain.Role;
import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchUser;
import com.peter.tanxuannewapp.domain.request.ReqUpdateUserDTO;
import com.peter.tanxuannewapp.domain.resposne.*;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.RoleRepository;
import com.peter.tanxuannewapp.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_EXISTS_EXCEPTION_MESSAGE = "Email already exists";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found";
    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;

    public ResUserDTO convertUserToResUserDTO(User user) {
        return this.modelMapper.map(user, ResUserDTO.class);
    }

    public ResCreateUserDTO handleCreateUser(User reqCreateUser) {
        if (this.userRepository.existsByEmail(reqCreateUser.getEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS_EXCEPTION_MESSAGE);
        reqCreateUser.setPassword(passwordEncoder.encode(reqCreateUser.getPassword()));

        // check role
        if (reqCreateUser.getRole() != null) {
            Role userRole = this.roleRepository
                    .findById(reqCreateUser
                            .getRole()
                            .getId())
                    .orElse(null);
            reqCreateUser.setRole(userRole);
        }
        return modelMapper.map(this.userRepository.save(reqCreateUser), ResCreateUserDTO.class);
    }

    public ResUpdateUserDTO handleUpdateUser(ReqUpdateUserDTO reqUpdateUserDTO) {
        User currentUser = this.userRepository
                .findById(reqUpdateUserDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE));
        currentUser.setName(reqUpdateUserDTO.getName());
        currentUser.setAddress(reqUpdateUserDTO.getAddress());
        currentUser.setPhone(reqUpdateUserDTO.getPhone());

        this.userRepository.save(currentUser);
        return modelMapper.map(currentUser, ResUpdateUserDTO.class);
    }

    public void handleDeleteUser(int userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE));
        this.userRepository.delete(user);
    }

    public PaginationResponse handleFetchAllUsers(Pageable pageable) {
        Page<User> userPages = this.userRepository.findAll(pageable);
        Page<ResUserDTO> userDTOPages = userPages.map(this::convertUserToResUserDTO);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userDTOPages.getTotalPages());
        meta.setTotal(userDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(userDTOPages.getContent());

        return paginationResponse;
    }

    public ResUserDTO handleFetchUserById(int userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE));
        return this.convertUserToResUserDTO(user);
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository
                .findUserByEmail(email)
                .orElse(null);
    }

    // set refreshToken in user
    public void setRefreshTokenInUserDB(String refreshToken, String email) {
        User currentUserDB = this.handleGetUserByEmail(email);
        if (currentUserDB != null) {
            currentUserDB.setRefreshToken(refreshToken);
            this.userRepository.save(currentUserDB);
        }
    }

    public User handleGetUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository
                .findUserByRefreshTokenAndEmail(refreshToken, email)
                .orElse(null);
    }

    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public PaginationResponse handleFilteredUsers(Pageable pageable, CriteriaSearchUser criteria) {
        QUser quser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        if(criteria.getEmail() != null && !criteria.getEmail().isEmpty()){
            builder.and(quser.email.contains(criteria.getEmail()));
        }
        if(criteria.getRole() != null){
            builder.and(quser.role.id.eq(criteria.getRole().getId()));
        }
        if(criteria.getName() != null && !criteria.getName().isEmpty()){
            builder.and(quser.name.contains(criteria.getName()));
        }
        if(criteria.getPhone() != null && !criteria.getPhone().isEmpty()){
            builder.and(quser.phone.contains(criteria.getPhone()));
        }
        if(criteria.getAddress() != null && !criteria.getAddress().isEmpty()){
            builder.and(quser.address.contains(criteria.getAddress()));
        }
        if(criteria.getCreatedAt() != null && !criteria.getCreatedAt().isEmpty()){
            LocalDate localDate = LocalDate.parse(criteria.getCreatedAt());
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
            builder.and(quser.createdAt.between(startOfDay, endOfDay));
        }

        Page<User> userFilteredPages = this.userRepository.findAll(builder,pageable);
        Page<ResUserDTO> userFilteredDTOPages = userFilteredPages.map(this::convertUserToResUserDTO);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userFilteredDTOPages.getTotalPages());
        meta.setTotal(userFilteredDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(userFilteredDTOPages.getContent());

        return paginationResponse;
    }

}
