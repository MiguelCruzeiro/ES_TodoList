package com.es.todolist.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.todolist.models.Task;
import com.es.todolist.services.TaskService;

import com.es.todolist.configuration.CustomUserDetails;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> create(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Task task) {
        String userSub = userDetails.getUserSub();
        String username = userDetails.getUsername();
        String email = userDetails.getEmail();
        return new ResponseEntity<>(taskService.save(task, userSub, username, email), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Iterable<Task>> getTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userSub = userDetails.getUserSub();
        return new ResponseEntity<>(taskService.findByUserSub(userSub), HttpStatus.OK);
    }
    
}
