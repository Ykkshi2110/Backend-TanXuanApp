package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchUser;
import com.peter.tanxuannewapp.domain.request.ReqUpdateUserDTO;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.domain.resposne.ResCreateUserDTO;
import com.peter.tanxuannewapp.domain.resposne.ResUpdateUserDTO;
import com.peter.tanxuannewapp.domain.resposne.ResUserDTO;
import com.peter.tanxuannewapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users/create")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User reqCreateUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(reqCreateUser));
    }

    @PostMapping("/users/update")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@Valid @RequestBody ReqUpdateUserDTO reqUpdateUserDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(reqUpdateUserDTO));
    }

    @DeleteMapping("/users/delete/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id){
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<PaginationResponse> fetchAllUsers(Pageable pageable) {
        return ResponseEntity.ok().body(this.userService.handleFetchAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(this.userService.handleFetchUserById(id));
    }

    @PostMapping("/users/filter")
    @ApiMessage("Filter user with criteria")
    public ResponseEntity<PaginationResponse> fetchUserWithCriteria(Pageable pageable, @RequestBody CriteriaSearchUser criteria) {
        return ResponseEntity.ok().body(this.userService.handleFilteredUsers(pageable, criteria));
    }
}
