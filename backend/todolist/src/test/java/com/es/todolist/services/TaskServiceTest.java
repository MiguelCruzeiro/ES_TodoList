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
import com.es.todolist.repositories.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Test Task", "Test Description", false, Priority.LOW, null);
    }

    @Test
    void delete() {
        taskService.delete(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void save() {
        when(appUserService.findBySub(any())).thenReturn(Optional.empty());
        when(appUserService.save(any())).thenReturn(new AppUser(1L, "testUser", "testSub", "testemail@gmail.com", null));
        when(taskRepository.save(any())).thenReturn(task);

        Task savedTask = taskService.save(task, "testSub", "testUser", "testemail@gmail.com");

        assertEquals(task, savedTask);

        verify(appUserService, times(1)).findBySub("testSub");

    }

    @Test
    void findById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.findById(1L);

        assertEquals(task, foundTask);
    }

    @Test
    void findAll() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(task));
        when(taskRepository.findAll()).thenReturn(tasks);

        Iterable<Task> foundTasks = taskService.findAll();

        assertEquals(tasks, foundTasks);
    }

    @Test
    void findByUserId() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(task));
        when(taskRepository.findByUserId(1L)).thenReturn(tasks);

        Iterable<Task> foundTasks = taskService.findByUserId(1L);

        assertEquals(tasks, foundTasks);
    }

    @Test
    void findByUserSub() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(task));
        when(taskRepository.findByUserSub("testSub")).thenReturn(tasks);

        Iterable<Task> foundTasks = taskService.findByUserSub("testSub");

        assertEquals(tasks, foundTasks);
    }
    
}
