package com.es.todolist.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.es.todolist.models.Task;
import com.es.todolist.models.Priority;
import com.es.todolist.models.AppUser;
import com.es.todolist.repositories.AppUserRepository;
import com.es.todolist.services.AppUserService;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserService appUserService;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser(1L, "testUser", "testSub", "testemail@gmail.com", null);

    }

    @Test
    void findByUsername() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        Optional<AppUser> foundUser = appUserService.findByUsername("testUser");
        assertEquals(Optional.of(appUser), foundUser);
    }

    @Test
    void findBySub() {
        when(appUserRepository.findBySub("testSub")).thenReturn(Optional.of(appUser));
        Optional<AppUser> foundUser = appUserService.findBySub("testSub");
        assertEquals(Optional.of(appUser), foundUser);
    }

    @Test
    void findByEmail() {
        when(appUserRepository.findByEmail("testemail@gmail.com")).thenReturn(Optional.of(appUser));
        Optional<AppUser> foundUser = appUserService.findByEmail("testemail@gmail.com");
        assertEquals(Optional.of(appUser), foundUser);

    }

    @Test
    void save() {
        when(appUserRepository.save(any())).thenReturn(appUser);
        AppUser savedUser = appUserService.save(appUser);
        assertEquals(appUser, savedUser);
    }

    @Test
    void delete() {
        appUserService.delete(appUser);
        verify(appUserRepository, times(1)).delete(appUser);
    }

    

}
