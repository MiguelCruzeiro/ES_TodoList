package com.es.todolist.services;

import org.springframework.stereotype.Service;

import com.es.todolist.repositories.TaskRepository;

import com.es.todolist.models.Task;

import com.es.todolist.models.AppUser;

import com.es.todolist.services.AppUserService;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final AppUserService appUserService;

    public TaskService(TaskRepository taskRepository, AppUserService appUserService) {
        this.taskRepository = taskRepository;
        this.appUserService = appUserService;
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public Task save(Task task, String userSub, String username, String email) {
        // Check if the user with the given userSub already exists
        AppUser user = appUserService.findBySub(userSub)
            .orElseGet(() -> {
                // Create and save a new user if one doesn't exist
                AppUser newUser = new AppUser();
                newUser.setSub(userSub);
                newUser.setUsername(username);
                newUser.setEmail(email);
                return appUserService.save(newUser);
            });

        // Set the user for the task and save it
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task markAsCompleted(Task task){
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Iterable<Task> findAll() {
        return taskRepository.findAll();
    }

    public Iterable<Task> findByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public Iterable<Task> findByUserSub(String sub) {
        return taskRepository.findByUserSub(sub);
    }


    
}
