package com.es.todolist.controllers;

import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.todolist.models.AppUser;
import com.es.todolist.services.AppUserService;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping
    public ResponseEntity<AppUser> create(@RequestBody AppUser appUser) {
        Optional<AppUser> existingUser = appUserService.findBySub(appUser.getSub());
        if (existingUser != null) {
            return new ResponseEntity<>(existingUser.get(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(appUserService.save(appUser), HttpStatus.CREATED);
    }
    
}
