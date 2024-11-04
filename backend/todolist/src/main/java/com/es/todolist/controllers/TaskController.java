package com.es.todolist.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.todolist.models.Task;
import com.es.todolist.services.TaskService;

import com.es.todolist.configuration.CustomUserDetails;

//cors
@CrossOrigin(origins = "http://localhost:4200",  maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    Logger logger = LoggerFactory.getLogger(TaskController.class);

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        String userSub = userDetails.getUserSub();
        Task task = taskService.findById(id);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println(task.getUser());
        logger.info("User sub: " + userSub);
        if (!task.getUser().getSub().equals(userSub)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        taskService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/completed/{id}")
    public ResponseEntity<Task> markAsCompleted(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id){
        String userSub = userDetails.getUserSub();
        Task task = taskService.findById(id);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println(task.getUser());
        logger.info("User sub: " + userSub);
        if (!task.getUser().getSub().equals(userSub)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(taskService.markAsCompleted(task), HttpStatus.OK);
    }
}
