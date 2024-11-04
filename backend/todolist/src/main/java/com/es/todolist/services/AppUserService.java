package com.es.todolist.services;

import org.springframework.stereotype.Service;

import com.es.todolist.models.AppUser;
import com.es.todolist.repositories.AppUserRepository;

import java.util.Optional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public Optional<AppUser> findBySub(String sub) {
        return appUserRepository.findBySub(sub);
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    public void delete(AppUser appUser) {
        appUserRepository.delete(appUser);
    }
    
}
